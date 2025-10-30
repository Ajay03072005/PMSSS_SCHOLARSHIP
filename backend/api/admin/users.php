<?php
/**
 * Admin Users Management API
 * Handles viewing and managing registered users
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/config/cors.php';
require_once dirname(__DIR__, 2) . '/config/jwt.php';

header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];

// Handle preflight
if ($method === 'OPTIONS') {
    http_response_code(200);
    exit;
}

try {
    // Verify admin authentication
    $headers = getallheaders();
    $authHeader = $headers['Authorization'] ?? '';
    
    if (!preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
        throw new Exception('Unauthorized - No token provided');
    }

    $jwt = new JWT();
    $token = $matches[1];
    $decoded = $jwt->decode($token);

    if (!$decoded || !is_array($decoded)) {
        throw new Exception('Unauthorized - Invalid token format');
    }

    // Check if it's an admin token
    $userId = $decoded['userId'] ?? null;
    if (!is_array($userId) || !isset($userId['type']) || $userId['type'] !== 'admin' || !isset($userId['admin_id'])) {
        throw new Exception('Unauthorized - Invalid admin token');
    }

    $admin = (object)$userId; // Convert to object for easier access

    $database = new Database();
    $db = $database->connect();

    switch ($method) {
        case 'GET':
            handleGetUsers($db, $decoded);
            break;

        case 'PUT':
            handleUpdateUser($db, $decoded);
            break;

        case 'DELETE':
            handleDeleteUser($db, $decoded);
            break;

        default:
            throw new Exception('Method not allowed');
    }

} catch (Exception $e) {
    http_response_code($e->getCode() ?: 400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

/**
 * Get users with filtering and pagination
 */
function handleGetUsers($db, $admin) {
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10;
    $search = isset($_GET['search']) ? $_GET['search'] : null;
    
    $offset = ($page - 1) * $limit;

    // Build query
    $where = [];
    $params = [];

    if ($search) {
        $where[] = "(u.name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)";
        $searchTerm = "%$search%";
        $params[] = $searchTerm;
        $params[] = $searchTerm;
        $params[] = $searchTerm;
    }

    $whereClause = !empty($where) ? 'WHERE ' . implode(' AND ', $where) : '';

    // Get total count
    $countQuery = "
        SELECT COUNT(*) as total 
        FROM users u
        $whereClause
    ";
    
    $stmt = $db->prepare($countQuery);
    $stmt->execute($params);
    $total = $stmt->fetch(PDO::FETCH_ASSOC)['total'];

    // Get users with application count
    $query = "
        SELECT 
            u.id,
            u.name,
            u.email,
            u.phone,
            u.created_at,
            u.updated_at,
            COUNT(a.id) as application_count,
            MAX(a.created_at) as last_application_date,
            GROUP_CONCAT(DISTINCT a.status) as application_statuses
        FROM users u
        LEFT JOIN applications a ON u.id = a.user_id
        $whereClause
        GROUP BY u.id, u.name, u.email, u.phone, u.created_at, u.updated_at
        ORDER BY u.created_at DESC
        LIMIT ? OFFSET ?
    ";

    $params[] = $limit;
    $params[] = $offset;

    $stmt = $db->prepare($query);
    $stmt->execute($params);
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $totalPages = ceil($total / $limit);

    echo json_encode([
        'success' => true,
        'users' => $users,
        'total' => (int)$total,
        'page' => $page,
        'limit' => $limit,
        'totalPages' => $totalPages
    ]);
}

/**
 * Update user information
 */
function handleUpdateUser($db, $admin) {
    $data = json_decode(file_get_contents('php://input'), true);
    
    $userId = $data['user_id'] ?? '';
    $name = $data['name'] ?? '';
    $email = $data['email'] ?? '';
    $phone = $data['phone'] ?? '';

    if (empty($userId)) {
        throw new Exception('User ID is required');
    }

    // Check if user exists
    $stmt = $db->prepare("SELECT * FROM users WHERE id = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        throw new Exception('User not found');
    }

    // Build update query dynamically
    $updates = [];
    $params = [];

    if (!empty($name)) {
        $updates[] = "name = ?";
        $params[] = $name;
    }

    if (!empty($email)) {
        // Check if email is already taken by another user
        $emailCheck = $db->prepare("SELECT id FROM users WHERE email = ? AND id != ?");
        $emailCheck->execute([$email, $userId]);
        if ($emailCheck->fetch()) {
            throw new Exception('Email already in use by another user');
        }
        $updates[] = "email = ?";
        $params[] = $email;
    }

    if (!empty($phone)) {
        $updates[] = "phone = ?";
        $params[] = $phone;
    }

    if (empty($updates)) {
        throw new Exception('No fields to update');
    }

    $updates[] = "updated_at = NOW()";
    $params[] = $userId;

    // Update user
    $updateQuery = "UPDATE users SET " . implode(', ', $updates) . " WHERE id = ?";
    $stmt = $db->prepare($updateQuery);
    $stmt->execute($params);

    // Log update
    $logStmt = $db->prepare("
        INSERT INTO admin_activity_logs 
        (admin_id, action, details, created_at)
        VALUES (?, 'update_user', ?, NOW())
    ");
    $logStmt->execute([
        $admin->admin_id,
        json_encode(['user_id' => $userId, 'changes' => $data])
    ]);

    echo json_encode([
        'success' => true,
        'message' => 'User updated successfully'
    ]);
}

/**
 * Delete user
 */
function handleDeleteUser($db, $admin) {
    $data = json_decode(file_get_contents('php://input'), true);
    $userId = $data['user_id'] ?? '';

    if (empty($userId)) {
        throw new Exception('User ID is required');
    }

    // Check if user exists
    $stmt = $db->prepare("SELECT * FROM users WHERE id = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        throw new Exception('User not found');
    }

    // Check if user has applications
    $appStmt = $db->prepare("SELECT COUNT(*) as count FROM applications WHERE user_id = ?");
    $appStmt->execute([$userId]);
    $appCount = $appStmt->fetch(PDO::FETCH_ASSOC)['count'];

    if ($appCount > 0) {
        throw new Exception('Cannot delete user with existing applications. Delete applications first.');
    }

    // Delete user
    $deleteStmt = $db->prepare("DELETE FROM users WHERE id = ?");
    $deleteStmt->execute([$userId]);

    // Log deletion
    $logStmt = $db->prepare("
        INSERT INTO admin_activity_logs 
        (admin_id, action, details, created_at)
        VALUES (?, 'delete_user', ?, NOW())
    ");
    $logStmt->execute([
        $admin->admin_id,
        json_encode(['user_id' => $userId, 'user' => $user['name'] . ' (' . $user['email'] . ')'])
    ]);

    echo json_encode([
        'success' => true,
        'message' => 'User deleted successfully'
    ]);
}
