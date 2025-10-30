<?php
require_once '../config/cors.php';
require_once '../config/database.php';
require_once '../config/jwt.php';
require_once '../config/helpers.php';

$database = new Database();
$conn = $database->connect();

if (!$conn) {
    sendError('Database connection failed', 500);
}

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

// REGISTER
if ($method === 'POST' && $action === 'register') {
    $data = json_decode(file_get_contents('php://input'), true);
    
    // Validate required fields
    $required = ['firstName', 'lastName', 'email', 'password', 'mobile', 'aadhar', 'dateOfBirth'];
    $missing = validateRequired($data, $required);
    
    if ($missing) {
        sendError("$missing is required", 400);
    }
    
    // Validate email
    if (!validateEmail($data['email'])) {
        sendError('Invalid email address', 400);
    }
    
    // Validate mobile
    if (!validateMobile($data['mobile'])) {
        sendError('Mobile number must be 10 digits', 400);
    }
    
    // Validate Aadhar
    if (!validateAadhar($data['aadhar'])) {
        sendError('Aadhar number must be 12 digits', 400);
    }
    
    // Validate password
    if (strlen($data['password']) < 8) {
        sendError('Password must be at least 8 characters', 400);
    }
    
    try {
        
        // Check if user already exists
        $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? OR aadhar = ?");
        $stmt->execute([$data['email'], $data['aadhar']]);
        if ($stmt->fetch()) {
            sendError('User already exists with this email or Aadhar number', 400);
        }
        
        // Hash password
        $hashedPassword = password_hash($data['password'], PASSWORD_BCRYPT);
        
        // Insert user
        $stmt = $conn->prepare("INSERT INTO users (first_name, middle_name, last_name, email, password, mobile, aadhar, date_of_birth, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'student')");
        
        $stmt->execute([
            sanitizeInput($data['firstName']),
            isset($data['middleName']) ? sanitizeInput($data['middleName']) : '',
            sanitizeInput($data['lastName']),
            $data['email'],
            $hashedPassword,
            $data['mobile'],
            $data['aadhar'],
            $data['dateOfBirth']
        ]);
        
        $userId = $conn->lastInsertId();
        
        // Generate token
        $token = JWT::encode($userId, 'student');
        
        sendSuccess('Registration successful', [
            'token' => $token,
            'user' => [
                'id' => $userId,
                'firstName' => $data['firstName'],
                'lastName' => $data['lastName'],
                'email' => $data['email'],
                'mobile' => $data['mobile'],
                'role' => 'student'
            ]
        ]);
        
    } catch(PDOException $e) {
        error_log("Register Error: " . $e->getMessage());
        sendError('Registration failed. Please try again.', 500);
    }
}

// LOGIN
else if ($method === 'POST' && $action === 'login') {
    $data = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($data['email']) || !isset($data['password'])) {
        sendError('Email and password are required', 400);
    }
    
    try {
        $stmt = $conn->prepare("SELECT id, first_name, middle_name, last_name, email, password, mobile, role, is_verified FROM users WHERE email = ?");
        $stmt->execute([$data['email']]);
        $user = $stmt->fetch();
        
        if (!$user || !password_verify($data['password'], $user['password'])) {
            sendError('Invalid email or password', 401);
        }
        
        // Generate token
        $token = JWT::encode($user['id'], $user['role']);
        
        sendSuccess('Login successful', [
            'token' => $token,
            'user' => [
                'id' => $user['id'],
                'firstName' => $user['first_name'],
                'middleName' => $user['middle_name'],
                'lastName' => $user['last_name'],
                'email' => $user['email'],
                'mobile' => $user['mobile'],
                'role' => $user['role'],
                'isVerified' => (bool)$user['is_verified']
            ]
        ]);
        
    } catch(PDOException $e) {
        error_log("Login Error: " . $e->getMessage());
        sendError('Login failed. Please try again.', 500);
    }
}

// GET CURRENT USER
else if ($method === 'GET' && $action === 'me') {
    $token = JWT::getTokenFromHeader();
    $userData = JWT::decode($token);
    
    if (!$userData) {
        sendError('Unauthorized. Please login again.', 401);
    }
    
    try {
        $stmt = $conn->prepare("SELECT id, first_name, middle_name, last_name, email, mobile, aadhar, date_of_birth, role, is_verified, created_at FROM users WHERE id = ?");
        $stmt->execute([$userData['userId']]);
        $user = $stmt->fetch();
        
        if (!$user) {
            sendError('User not found', 404);
        }
        
        sendSuccess('User retrieved', [
            'user' => [
                'id' => $user['id'],
                'firstName' => $user['first_name'],
                'middleName' => $user['middle_name'],
                'lastName' => $user['last_name'],
                'email' => $user['email'],
                'mobile' => $user['mobile'],
                'aadhar' => $user['aadhar'],
                'dateOfBirth' => $user['date_of_birth'],
                'role' => $user['role'],
                'isVerified' => (bool)$user['is_verified'],
                'createdAt' => $user['created_at']
            ]
        ]);
        
    } catch(PDOException $e) {
        error_log("Get User Error: " . $e->getMessage());
        sendError('Failed to retrieve user', 500);
    }
}

else {
    sendError('Invalid endpoint or method', 404);
}
?>
