<?php
/**
 * Admin Applications Management API
 * Handles viewing, updating, and managing scholarship applications
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
            handleGetApplications($db, $decoded);
            break;

        case 'PUT':
            handleUpdateApplication($db, $decoded);
            break;

        case 'DELETE':
            handleDeleteApplication($db, $decoded);
            break;

        default:
            throw new Exception('Method not allowed');
    }

} catch (Exception $e) {
    $errorCode = $e->getCode();
    // Ensure error code is a valid HTTP status code
    $httpCode = (is_int($errorCode) && $errorCode >= 100 && $errorCode < 600) ? $errorCode : 400;
    http_response_code($httpCode);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage(),
        'error_code' => $errorCode
    ]);
}

/**
 * Get applications with filtering and pagination
 */
function handleGetApplications($db, $admin) {
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10;
    $status = isset($_GET['status']) ? $_GET['status'] : null;
    $search = isset($_GET['search']) ? $_GET['search'] : null;
    
    $offset = ($page - 1) * $limit;

    // Build query
    $where = [];
    $params = [];

    if ($status && $status !== 'all') {
        $where[] = "a.status = ?";
        $params[] = $status;
    }

    if ($search) {
        $where[] = "(a.application_id LIKE ? OR a.first_name LIKE ? OR a.last_name LIKE ? OR u.email LIKE ?)";
        $searchTerm = "%$search%";
        $params[] = $searchTerm;
        $params[] = $searchTerm;
        $params[] = $searchTerm;
        $params[] = $searchTerm;
    }

    $whereClause = !empty($where) ? 'WHERE ' . implode(' AND ', $where) : '';

    // Get total count
    $countQuery = "
        SELECT COUNT(*) as total 
        FROM applications a
        LEFT JOIN users u ON a.user_id = u.id
        $whereClause
    ";
    
    $stmt = $db->prepare($countQuery);
    $stmt->execute($params);
    $total = $stmt->fetch(PDO::FETCH_ASSOC)['total'];

    // Get applications
    $query = "
        SELECT 
            a.*,
            u.email,
            CONCAT(u.first_name, ' ', IFNULL(u.middle_name, ''), ' ', u.last_name) as user_name
        FROM applications a
        LEFT JOIN users u ON a.user_id = u.id
        $whereClause
        ORDER BY a.created_at DESC
        LIMIT ? OFFSET ?
    ";

    $params[] = $limit;
    $params[] = $offset;

    $stmt = $db->prepare($query);
    $stmt->execute($params);
    $applications = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $totalPages = ceil($total / $limit);

    echo json_encode([
        'success' => true,
        'applications' => $applications,
        'total' => (int)$total,
        'page' => $page,
        'limit' => $limit,
        'totalPages' => $totalPages
    ]);
}

/**
 * Update application status
 */
function handleUpdateApplication($db, $admin) {
    $data = json_decode(file_get_contents('php://input'), true);
    
    $applicationId = $data['application_id'] ?? '';
    $newStatus = $data['status'] ?? '';
    $remarks = $data['remarks'] ?? '';

    if (empty($applicationId) || empty($newStatus)) {
        throw new Exception('Application ID and status are required');
    }

    // Validate status
    $validStatuses = ['pending', 'under_review', 'approved', 'rejected'];
    if (!in_array($newStatus, $validStatuses)) {
        throw new Exception('Invalid status');
    }

    // Get current application
    $stmt = $db->prepare("SELECT * FROM applications WHERE application_id = ?");
    $stmt->execute([$applicationId]);
    $application = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$application) {
        throw new Exception('Application not found');
    }

    $oldStatus = $application['status'];

    // Update status
    $updateStmt = $db->prepare("
        UPDATE applications 
        SET status = ?, updated_at = NOW()
        WHERE application_id = ?
    ");
    $updateStmt->execute([$newStatus, $applicationId]);

    // Log status change
    $logStmt = $db->prepare("
        INSERT INTO application_status_history 
        (application_id, old_status, new_status, changed_by, remarks, created_at)
        VALUES (?, ?, ?, ?, ?, NOW())
    ");
    $logStmt->execute([
        $application['id'],
        $oldStatus,
        $newStatus,
        $admin->admin_id,
        $remarks
    ]);

    echo json_encode([
        'success' => true,
        'message' => 'Application status updated successfully',
        'old_status' => $oldStatus,
        'new_status' => $newStatus
    ]);
}

/**
 * Delete application
 */
function handleDeleteApplication($db, $admin) {
    $data = json_decode(file_get_contents('php://input'), true);
    $applicationId = $data['application_id'] ?? '';

    if (empty($applicationId)) {
        throw new Exception('Application ID is required');
    }

    // Get application details first
    $stmt = $db->prepare("SELECT * FROM applications WHERE application_id = ?");
    $stmt->execute([$applicationId]);
    $application = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$application) {
        throw new Exception('Application not found');
    }

    // Delete uploaded files
    $uploadDir = dirname(__DIR__, 2) . '/uploads/';
    $fileFields = ['photo', 'aadhar', 'income', 'domicile', 'tenth_marksheet', 'twelfth_marksheet', 'admission_letter', 'bank_passbook'];
    
    foreach ($fileFields as $field) {
        if (!empty($application[$field]) && file_exists($uploadDir . $application[$field])) {
            unlink($uploadDir . $application[$field]);
        }
    }

    // Delete application
    $deleteStmt = $db->prepare("DELETE FROM applications WHERE application_id = ?");
    $deleteStmt->execute([$applicationId]);

    // Log deletion
    $logStmt = $db->prepare("
        INSERT INTO admin_activity_logs 
        (admin_id, action, details, created_at)
        VALUES (?, 'delete_application', ?, NOW())
    ");
    $logStmt->execute([
        $admin->admin_id,
        json_encode(['application_id' => $applicationId, 'applicant' => $application['full_name']])
    ]);

    echo json_encode([
        'success' => true,
        'message' => 'Application deleted successfully'
    ]);
}
