<?php
/**
 * Admin Authentication API
 * Handles admin login and verification
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/config/cors.php';
require_once dirname(__DIR__, 2) . '/config/jwt.php';

header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

// Handle preflight
if ($method === 'OPTIONS') {
    http_response_code(200);
    exit;
}

try {
    $database = new Database();
    $db = $database->connect();
    $jwt = new JWT();

    switch ($action) {
        case 'login':
            if ($method !== 'POST') {
                throw new Exception('Method not allowed');
            }
            
            $data = json_decode(file_get_contents('php://input'), true);
            $username = $data['username'] ?? '';
            $password = $data['password'] ?? '';
            
            if (empty($username) || empty($password)) {
                throw new Exception('Username and password are required');
            }

            // Check admin credentials
            $stmt = $db->prepare("SELECT * FROM admins WHERE username = ? AND is_active = 1");
            $stmt->execute([$username]);
            $admin = $stmt->fetch(PDO::FETCH_ASSOC);

            if (!$admin) {
                // Log failed attempt
                logLoginAttempt($db, $username, false);
                throw new Exception('Invalid credentials');
            }

            // Verify password
            if (!password_verify($password, $admin['password_hash'])) {
                logLoginAttempt($db, $username, false);
                throw new Exception('Invalid credentials');
            }

            // Log successful login
            logLoginAttempt($db, $username, true);

            // Update last login
            $updateStmt = $db->prepare("UPDATE admins SET last_login = NOW() WHERE id = ?");
            $updateStmt->execute([$admin['id']]);

            // Generate JWT token
            $tokenData = [
                'admin_id' => $admin['id'],
                'username' => $admin['username'],
                'role' => $admin['role'],
                'type' => 'admin'
            ];
            
            $token = $jwt->encode($tokenData, 'admin');

            echo json_encode([
                'success' => true,
                'message' => 'Login successful',
                'token' => $token,
                'admin' => [
                    'id' => $admin['id'],
                    'name' => $admin['name'],
                    'username' => $admin['username'],
                    'role' => $admin['role'],
                    'email' => $admin['email']
                ]
            ]);
            break;

        case 'verify':
            if ($method !== 'GET') {
                throw new Exception('Method not allowed');
            }

            // Get token from Authorization header
            $headers = getallheaders();
            $authHeader = $headers['Authorization'] ?? '';
            
            if (!preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
                throw new Exception('No token provided');
            }

            $token = $matches[1];
            $decoded = $jwt->decode($token);

            if (!$decoded || !isset($decoded->admin_id)) {
                throw new Exception('Invalid token');
            }

            // Verify admin still exists and is active
            $stmt = $db->prepare("SELECT id, username, role FROM admins WHERE id = ? AND is_active = 1");
            $stmt->execute([$decoded->admin_id]);
            $admin = $stmt->fetch(PDO::FETCH_ASSOC);

            if (!$admin) {
                throw new Exception('Admin not found or inactive');
            }

            echo json_encode([
                'success' => true,
                'valid' => true,
                'admin' => $admin
            ]);
            break;

        case 'logout':
            // For now, logout is handled client-side by removing token
            // Can add token blacklisting here if needed
            echo json_encode([
                'success' => true,
                'message' => 'Logged out successfully'
            ]);
            break;

        default:
            throw new Exception('Invalid action');
    }

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => $e->getMessage()
    ]);
}

/**
 * Log login attempts
 */
function logLoginAttempt($db, $username, $success) {
    try {
        $stmt = $db->prepare("
            INSERT INTO admin_login_logs (username, ip_address, user_agent, success, created_at)
            VALUES (?, ?, ?, ?, NOW())
        ");
        
        $stmt->execute([
            $username,
            $_SERVER['REMOTE_ADDR'] ?? 'unknown',
            $_SERVER['HTTP_USER_AGENT'] ?? 'unknown',
            $success ? 1 : 0
        ]);
    } catch (Exception $e) {
        // Log error but don't fail the request
        error_log('Failed to log login attempt: ' . $e->getMessage());
    }
}
