<?php
// Helper functions

// Send JSON response
function sendResponse($success, $data = [], $statusCode = 200) {
    http_response_code($statusCode);
    echo json_encode(array_merge(['success' => $success], $data));
    exit();
}

// Send error response
function sendError($message, $statusCode = 400) {
    sendResponse(false, ['message' => $message], $statusCode);
}

// Send success response
function sendSuccess($message, $data = []) {
    sendResponse(true, array_merge(['message' => $message], $data));
}

// Validate email
function validateEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL);
}

// Validate mobile (10 digits)
function validateMobile($mobile) {
    return preg_match('/^[0-9]{10}$/', $mobile);
}

// Validate Aadhar (12 digits)
function validateAadhar($aadhar) {
    return preg_match('/^[0-9]{12}$/', $aadhar);
}

// Sanitize input
function sanitizeInput($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

// Validate required fields
function validateRequired($data, $fields) {
    foreach ($fields as $field) {
        if (!isset($data[$field]) || empty($data[$field])) {
            return $field;
        }
    }
    return null;
}

// Generate unique application ID
function generateApplicationId() {
    return 'PMSSS' . date('Y') . rand(100000, 999999);
}

// Check if application ID is unique
function isApplicationIdUnique($applicationId, $conn) {
    $stmt = $conn->prepare("SELECT id FROM applications WHERE application_id = ?");
    $stmt->execute([$applicationId]);
    return $stmt->fetch() === false;
}

// Handle file upload
function handleFileUpload($file, $fieldName) {
    $allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
    $maxSize = 2 * 1024 * 1024; // 2MB
    
    if (!isset($file['error']) || $file['error'] !== UPLOAD_ERR_OK) {
        return ['success' => false, 'message' => "Error uploading $fieldName"];
    }
    
    if (!in_array($file['type'], $allowedTypes)) {
        return ['success' => false, 'message' => "$fieldName must be PDF, JPG, JPEG, or PNG"];
    }
    
    if ($file['size'] > $maxSize) {
        return ['success' => false, 'message' => "$fieldName must be less than 2MB"];
    }
    
    $extension = pathinfo($file['name'], PATHINFO_EXTENSION);
    $filename = $fieldName . '_' . time() . '_' . uniqid() . '.' . $extension;
    $uploadDir = __DIR__ . '/../uploads/';
    
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0755, true);
    }
    
    $filepath = $uploadDir . $filename;
    
    if (move_uploaded_file($file['tmp_name'], $filepath)) {
        return ['success' => true, 'filename' => $filename, 'path' => 'uploads/' . $filename];
    }
    
    return ['success' => false, 'message' => "Failed to save $fieldName"];
}

function createNotification($conn, $userId, $applicationId, $type, $title, $message) {
    try {
        $stmt = $conn->prepare("INSERT INTO notifications (user_id, application_id, type, title, message) VALUES (?, ?, ?, ?, ?)");
        $stmt->execute([$userId, $applicationId, $type, $title, $message]);
        return true;
    } catch(PDOException $e) {
        error_log("Notification Error: " . $e->getMessage());
        return false;
    }
}

// Add to application history
function addToHistory($conn, $applicationId, $status, $remarks = '', $updatedBy = null) {
    try {
        $stmt = $conn->prepare("INSERT INTO application_history (application_id, status, remarks, updated_by) VALUES (?, ?, ?, ?)");
        $stmt->execute([$applicationId, $status, $remarks, $updatedBy]);
        return true;
    } catch(PDOException $e) {
        error_log("History Error: " . $e->getMessage());
        return false;
    }
}
?>
