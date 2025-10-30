<?php
/**
 * Admin Statistics API
 * Provides dashboard statistics
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/config/cors.php';
require_once dirname(__DIR__, 2) . '/config/jwt.php';

header('Content-Type: application/json');

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
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
    if (is_array($userId) && isset($userId['type']) && $userId['type'] === 'admin' && isset($userId['admin_id'])) {
        // Valid admin token
        $adminId = $userId['admin_id'];
    } else {
        throw new Exception('Unauthorized - Invalid admin token');
    }

    $database = new Database();
    $db = $database->connect();

    // Get statistics
    $stats = [];

    // Total applications
    $stmt = $db->query("SELECT COUNT(*) as total FROM applications");
    $stats['total'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];

    // Pending applications
    $stmt = $db->query("SELECT COUNT(*) as pending FROM applications WHERE status = 'pending'");
    $stats['pending'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['pending'];

    // Under review
    $stmt = $db->query("SELECT COUNT(*) as under_review FROM applications WHERE status = 'under_review'");
    $stats['under_review'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['under_review'];

    // Approved applications
    $stmt = $db->query("SELECT COUNT(*) as approved FROM applications WHERE status = 'approved'");
    $stats['approved'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['approved'];

    // Rejected applications
    $stmt = $db->query("SELECT COUNT(*) as rejected FROM applications WHERE status = 'rejected'");
    $stats['rejected'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['rejected'];

    // Applications this month
    $stmt = $db->query("
        SELECT COUNT(*) as this_month 
        FROM applications 
        WHERE MONTH(created_at) = MONTH(CURRENT_DATE())
        AND YEAR(created_at) = YEAR(CURRENT_DATE())
    ");
    $stats['this_month'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['this_month'];

    // Applications today
    $stmt = $db->query("
        SELECT COUNT(*) as today 
        FROM applications 
        WHERE DATE(created_at) = CURRENT_DATE()
    ");
    $stats['today'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['today'];

    // Total users
    $stmt = $db->query("SELECT COUNT(*) as total_users FROM users");
    $stats['total_users'] = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total_users'];

    // Status distribution
    $stmt = $db->query("
        SELECT status, COUNT(*) as count 
        FROM applications 
        GROUP BY status
    ");
    $statusDistribution = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $statusDistribution[$row['status']] = (int)$row['count'];
    }
    $stats['status_distribution'] = $statusDistribution;

    // Monthly trend (last 6 months)
    $stmt = $db->query("
        SELECT 
            DATE_FORMAT(created_at, '%Y-%m') as month,
            COUNT(*) as count
        FROM applications
        WHERE created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH)
        GROUP BY DATE_FORMAT(created_at, '%Y-%m')
        ORDER BY month ASC
    ");
    $monthlyTrend = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $monthlyTrend[] = [
            'month' => $row['month'],
            'count' => (int)$row['count']
        ];
    }
    $stats['monthly_trend'] = $monthlyTrend;

    echo json_encode([
        'success' => true,
        'stats' => $stats
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}
