<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/../logs/php-errors.log');

require_once '../config/cors.php';
require_once '../config/database.php';
require_once '../config/jwt.php';
require_once '../config/helpers.php';

// Create logs directory if it doesn't exist
$logDir = __DIR__ . '/../logs';
if (!is_dir($logDir)) {
    mkdir($logDir, 0755, true);
}

// Log request details
$logFile = $logDir . '/application-debug.log';
$logMessage = date('Y-m-d H:i:s') . " - Request received\n";
$logMessage .= "Method: " . $_SERVER['REQUEST_METHOD'] . "\n";
$logMessage .= "Action: " . (isset($_GET['action']) ? $_GET['action'] : 'none') . "\n";
$logMessage .= "Headers: " . print_r(getallheaders(), true) . "\n";
$logMessage .= "POST data: " . print_r($_POST, true) . "\n";
$logMessage .= "FILES: " . print_r($_FILES, true) . "\n";
file_put_contents($logFile, $logMessage, FILE_APPEND);

$database = new Database();
$conn = $database->connect();

if (!$conn) {
    $error = 'Database connection failed';
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: $error\n", FILE_APPEND);
    sendError($error, 500);
}

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

// Log connection success
file_put_contents($logFile, date('Y-m-d H:i:s') . " - Database connected successfully\n", FILE_APPEND);

// SUBMIT/UPDATE APPLICATION
if ($method === 'POST' && $action === '') {
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - Processing application submission\n", FILE_APPEND);
    
    $token = JWT::getTokenFromHeader();
    $userData = JWT::decode($token);
    
    if (!$userData) {
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: Unauthorized - no valid token\n", FILE_APPEND);
        sendError('Unauthorized. Please login.', 401);
    }
    
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - User authenticated: ID=" . $userData['userId'] . "\n", FILE_APPEND);
    
    // Get JSON data from POST
    $data = json_decode($_POST['data'] ?? '{}', true);
    
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - Decoded data: " . print_r($data, true) . "\n", FILE_APPEND);
    
    // Log FILES array
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - FILES received: " . print_r(array_keys($_FILES), true) . "\n", FILE_APPEND);
    if (!empty($_FILES)) {
        foreach ($_FILES as $key => $file) {
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - File $key: name=" . $file['name'] . ", size=" . $file['size'] . ", error=" . $file['error'] . "\n", FILE_APPEND);
        }
    } else {
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - WARNING: No files in $_FILES array\n", FILE_APPEND);
    }
    
    if (!$data) {
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: Invalid application data\n", FILE_APPEND);
        sendError('Invalid application data', 400);
    }
    
    try {
        // Check if DRAFT application exists (only update drafts, not submitted apps)
        $stmt = $conn->prepare("SELECT id, application_id, status FROM applications WHERE user_id = ? AND status = 'draft'");
        $stmt->execute([$userData['userId']]);
        $existingApp = $stmt->fetch();
        
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - Existing DRAFT check: " . ($existingApp ? "Found ID=" . $existingApp['id'] : "Not found - will create new") . "\n", FILE_APPEND);
        
        // Handle file uploads
        $uploadedFiles = [];
        $fileFields = ['photo', 'aadhar', 'domicile', 'income', 'tenthMarksheet', 'twelfthMarksheet', 'admissionLetter', 'bankPassbook'];
        
        foreach ($fileFields as $field) {
            if (isset($_FILES[$field]) && $_FILES[$field]['error'] === UPLOAD_ERR_OK) {
                file_put_contents($logFile, date('Y-m-d H:i:s') . " - Processing file: $field\n", FILE_APPEND);
                $result = handleFileUpload($_FILES[$field], $field);
                if (!$result['success']) {
                    file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: File upload failed for $field: " . $result['message'] . "\n", FILE_APPEND);
                    sendError($result['message'], 400);
                }
                $uploadedFiles[$field] = $result['path'];
                file_put_contents($logFile, date('Y-m-d H:i:s') . " - File uploaded: $field -> " . $result['path'] . "\n", FILE_APPEND);
            }
        }
        
        $isSubmit = isset($data['submit']) && $data['submit'] === true;
        $status = $isSubmit ? 'submitted' : 'draft';
        
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - Application status: $status\n", FILE_APPEND);
        
        if ($existingApp) {
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Updating existing application\n", FILE_APPEND);
            // UPDATE existing application
            $updateFields = [
                "first_name = ?",
                "middle_name = ?",
                "last_name = ?",
                "date_of_birth = ?",
                "gender = ?",
                "category = ?",
                "aadhar = ?",
                "mobile = ?",
                "email = ?",
                "address = ?",
                "district = ?",
                "state = ?",
                "pincode = ?",
                "academic_info = ?",
                "father_name = ?",
                "father_occupation = ?",
                "mother_name = ?",
                "mother_occupation = ?",
                "annual_income = ?",
                "account_holder_name = ?",
                "account_number = ?",
                "ifsc_code = ?",
                "bank_name = ?",
                "branch_name = ?",
                "status = ?",
                "declaration = ?",
                "updated_at = NOW()"
            ];
            
            $params = [
                $data['personalInfo']['firstName'],
                $data['personalInfo']['middleName'] ?? '',
                $data['personalInfo']['lastName'],
                $data['personalInfo']['dateOfBirth'],
                $data['personalInfo']['gender'],
                $data['personalInfo']['category'],
                $data['personalInfo']['aadhar'],
                $data['personalInfo']['mobile'],
                $data['personalInfo']['email'],
                $data['personalInfo']['address'],
                $data['personalInfo']['district'],
                $data['personalInfo']['state'],
                $data['personalInfo']['pincode'],
                json_encode($data['academicInfo']),
                $data['familyInfo']['father']['name'] ?? '',
                $data['familyInfo']['father']['occupation'] ?? '',
                $data['familyInfo']['mother']['name'] ?? '',
                $data['familyInfo']['mother']['occupation'] ?? '',
                $data['familyInfo']['annualIncome'] ?? '0',
                $data['bankDetails']['accountHolderName'] ?? '',
                $data['bankDetails']['accountNumber'] ?? '',
                $data['bankDetails']['ifscCode'] ?? '',
                $data['bankDetails']['bankName'] ?? '',
                $data['bankDetails']['branchName'] ?? '',
                $status,
                isset($data['declaration']) && $data['declaration'] ? 1 : 0
            ];
            
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Update params: " . print_r($params, true) . "\n", FILE_APPEND);
            
            // Add file uploads to update
            foreach ($uploadedFiles as $field => $path) {
                $dbField = strtolower(preg_replace('/([a-z])([A-Z])/', '$1_$2', $field));
                if ($field === 'aadhar') $dbField = 'aadhar_doc';
                if ($field === 'income') $dbField = 'income_cert';
                if ($field === 'tenthMarksheet') $dbField = 'tenth_marksheet';
                if ($field === 'twelfthMarksheet') $dbField = 'twelfth_marksheet';
                if ($field === 'admissionLetter') $dbField = 'admission_letter';
                if ($field === 'bankPassbook') $dbField = 'bank_passbook';
                
                $updateFields[] = "$dbField = ?";
                $params[] = $path;
            }
            
            if ($isSubmit) {
                $updateFields[] = "submitted_at = NOW()";
            }
            
            $params[] = $existingApp['id'];
            
            $sql = "UPDATE applications SET " . implode(", ", $updateFields) . " WHERE id = ?";
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Update SQL: $sql\n", FILE_APPEND);
            
            $stmt = $conn->prepare($sql);
            $stmt->execute($params);
            
            if ($isSubmit && $existingApp['status'] !== 'submitted') {
                addToHistory($conn, $existingApp['id'], 'submitted', 'Application submitted by student');
                createNotification($conn, $userData['userId'], $existingApp['id'], 'application_submitted', 
                    'Application Submitted', "Your application {$existingApp['application_id']} has been submitted successfully.");
            }
            
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Application updated successfully\n", FILE_APPEND);
            
            sendSuccess('Application updated successfully (debug mode)', [
                'application' => [
                    'id' => $existingApp['id'],
                    'applicationId' => $existingApp['application_id'],
                    'status' => $status
                ]
            ]);
        } else {
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Creating new application\n", FILE_APPEND);
            
            // CREATE new application
            $applicationId = generateApplicationId();
            
            // Ensure unique application ID
            while (!isApplicationIdUnique($applicationId, $conn)) {
                $applicationId = generateApplicationId();
            }
            
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Generated Application ID: $applicationId\n", FILE_APPEND);
            
            $stmt = $conn->prepare("INSERT INTO applications (
                user_id, application_id, first_name, middle_name, last_name, date_of_birth, gender, category,
                aadhar, mobile, email, address, district, state, pincode, academic_info,
                father_name, father_occupation, father_mobile, mother_name, mother_occupation, mother_mobile,
                annual_income, income_source, account_holder_name, account_number, ifsc_code, bank_name, branch_name,
                photo, aadhar_doc, domicile, income_cert, tenth_marksheet, twelfth_marksheet, admission_letter, bank_passbook,
                status, declaration" . ($isSubmit ? ", submitted_at" : "") . "
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" . ($isSubmit ? ", NOW()" : "") . ")");
            
            $params = [
                $userData['userId'],
                $applicationId,
                $data['personalInfo']['firstName'],
                $data['personalInfo']['middleName'] ?? '',
                $data['personalInfo']['lastName'],
                $data['personalInfo']['dateOfBirth'],
                $data['personalInfo']['gender'],
                $data['personalInfo']['category'],
                $data['personalInfo']['aadhar'],
                $data['personalInfo']['mobile'],
                $data['personalInfo']['email'],
                $data['personalInfo']['address'],
                $data['personalInfo']['district'],
                $data['personalInfo']['state'],
                $data['personalInfo']['pincode'],
                json_encode($data['academicInfo']),
                $data['familyInfo']['father']['name'] ?? '',
                $data['familyInfo']['father']['occupation'] ?? '',
                $data['familyInfo']['father']['mobile'] ?? '',
                $data['familyInfo']['mother']['name'] ?? '',
                $data['familyInfo']['mother']['occupation'] ?? '',
                $data['familyInfo']['mother']['mobile'] ?? '',
                $data['familyInfo']['annualIncome'] ?? '0',
                $data['familyInfo']['incomeSource'] ?? '',
                $data['bankDetails']['accountHolderName'] ?? '',
                $data['bankDetails']['accountNumber'] ?? '',
                $data['bankDetails']['ifscCode'] ?? '',
                $data['bankDetails']['bankName'] ?? '',
                $data['bankDetails']['branchName'] ?? '',
                $uploadedFiles['photo'] ?? null,
                $uploadedFiles['aadhar'] ?? null,
                $uploadedFiles['domicile'] ?? null,
                $uploadedFiles['income'] ?? null,
                $uploadedFiles['tenthMarksheet'] ?? null,
                $uploadedFiles['twelfthMarksheet'] ?? null,
                $uploadedFiles['admissionLetter'] ?? null,
                $uploadedFiles['bankPassbook'] ?? null,
                $status,
                isset($data['declaration']) && $data['declaration'] ? 1 : 0
            ];
            
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Executing INSERT with params: " . print_r($params, true) . "\n", FILE_APPEND);
            
            $stmt->execute($params);
            
            $appId = $conn->lastInsertId();
            
            file_put_contents($logFile, date('Y-m-d H:i:s') . " - Application created with ID: $appId\n", FILE_APPEND);
            
            if ($isSubmit) {
                addToHistory($conn, $appId, 'submitted', 'Application submitted by student');
                createNotification($conn, $userData['userId'], $appId, 'application_submitted', 
                    'Application Submitted', "Your application $applicationId has been submitted successfully.");
                file_put_contents($logFile, date('Y-m-d H:i:s') . " - History and notification created\n", FILE_APPEND);
            }
            
            sendSuccess('Application created successfully', [
                'application' => [
                    'id' => $appId,
                    'applicationId' => $applicationId,
                    'status' => $status
                ]
            ]);
        }
        
    } catch(PDOException $e) {
        $errorMsg = "Database Error: " . $e->getMessage();
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: $errorMsg\n", FILE_APPEND);
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - Stack trace: " . $e->getTraceAsString() . "\n", FILE_APPEND);
        sendError('Failed to process application: ' . $e->getMessage(), 500);
    } catch(Exception $e) {
        $errorMsg = "General Error: " . $e->getMessage();
        file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: $errorMsg\n", FILE_APPEND);
        sendError('Failed to process application: ' . $e->getMessage(), 500);
    }
}

// TRACK APPLICATION (same as before)
else if ($method === 'POST' && $action === 'track') {
    // ... track logic
    sendError('Track endpoint not implemented in debug mode', 501);
}

// GET MY APPLICATIONS
else if ($method === 'GET' && $action === 'my') {
    // ... get my applications logic
    sendError('My applications endpoint not implemented in debug mode', 501);
}

else {
    file_put_contents($logFile, date('Y-m-d H:i:s') . " - ERROR: Invalid endpoint or method\n", FILE_APPEND);
    sendError('Invalid endpoint or method', 404);
}
?>
