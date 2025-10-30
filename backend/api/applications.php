<?php
require_once '../config/cors.php';
require_once '../config/database.php';
require_once '../config/jwt.php';
require_once '../config/helpers.php';
require_once '../config/email.php';

$database = new Database();
$conn = $database->connect();

if (!$conn) {
    sendError('Database connection failed', 500);
}

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

// SUBMIT/UPDATE APPLICATION
if ($method === 'POST' && $action === '') {
    $token = JWT::getTokenFromHeader();
    $userData = JWT::decode($token);
    
    if (!$userData) {
        sendError('Unauthorized. Please login.', 401);
    }
    
    // Get JSON data from POST
    $data = json_decode($_POST['data'] ?? '{}', true);
        
    if (!$data) {
        sendError('Invalid application data', 400);
    }
    
    try {
        // Check if DRAFT application exists (only update drafts, not submitted apps)
        $stmt = $conn->prepare("SELECT id, application_id, status FROM applications WHERE user_id = ? AND status = 'draft'");
        $stmt->execute([$userData['userId']]);
        $existingApp = $stmt->fetch();
        
        // Handle file uploads
        $uploadedFiles = [];
        $fileFields = ['photo', 'aadhar', 'domicile', 'income', 'tenthMarksheet', 'twelfthMarksheet', 'admissionLetter', 'bankPassbook'];
        
        foreach ($fileFields as $field) {
            if (isset($_FILES[$field]) && $_FILES[$field]['error'] === UPLOAD_ERR_OK) {
                $result = handleFileUpload($_FILES[$field], $field);
                if (!$result['success']) {
                    sendError($result['message'], 400);
                }
                $uploadedFiles[$field] = $result['path'];
            }
        }
        
        $isSubmit = isset($data['submit']) && $data['submit'] === true;
        $status = $isSubmit ? 'submitted' : 'draft';
        
        if ($existingApp) {
            // UPDATE existing application
            $updateFields = [
                "firstName = ?",
                "middleName = ?",
                "lastName = ?",
                "dob = ?",
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
                "father_mobile = ?",
                "mother_name = ?",
                "mother_occupation = ?",
                "mother_mobile = ?",
                "annual_income = ?",
                "income_source = ?",
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
                $data['bankDetails']['branchName'] ?? ''
            ];
            
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
            $stmt = $conn->prepare($sql);
            $stmt->execute($params);
            
            if ($isSubmit && $existingApp['status'] !== 'submitted') {
                addToHistory($conn, $existingApp['id'], 'submitted', 'Application submitted by student');
                createNotification($conn, $userData['userId'], $existingApp['id'], 'application_submitted', 
                    'Application Submitted', "Your application {$existingApp['application_id']} has been submitted successfully.");
                
                // Send confirmation email
                $userName = $data['personalInfo']['firstName'] . ' ' . $data['personalInfo']['lastName'];
                $userEmail = $data['personalInfo']['email'];
                sendApplicationConfirmation($userEmail, $userName, $existingApp['application_id']);
            }
            
            sendSuccess('Application updated successfully', [
                'application' => [
                    'id' => $existingApp['id'],
                    'applicationId' => $existingApp['application_id'],
                    'status' => $status
                ]
            ]);
            
        } else {
            // CREATE new application
            $applicationId = generateApplicationId();
            
            // Ensure unique application ID
            while (!isApplicationIdUnique($applicationId, $conn)) {
                $applicationId = generateApplicationId();
            }
            
            $stmt = $conn->prepare("INSERT INTO applications (
                user_id, application_id, first_name, middle_name, last_name, date_of_birth, gender, category,
                aadhar, mobile, email, address, district, state, pincode, academic_info,
                father_name, father_occupation, father_mobile, mother_name, mother_occupation, mother_mobile,
                annual_income, income_source, account_holder_name, account_number, ifsc_code, bank_name, branch_name,
                photo, aadhar_doc, domicile, income_cert, tenth_marksheet, twelfth_marksheet, admission_letter, bank_passbook,
                status, declaration" . ($isSubmit ? ", submitted_at" : "") . "
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" . ($isSubmit ? ", NOW()" : "") . ")");
            
            $stmt->execute([
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
            ]);
            
            $appId = $conn->lastInsertId();
            
            if ($isSubmit) {
                addToHistory($conn, $appId, 'submitted', 'Application submitted by student');
                createNotification($conn, $userData['userId'], $appId, 'application_submitted', 
                    'Application Submitted', "Your application $applicationId has been submitted successfully.");
                
                // Send confirmation email
                $userName = $data['personalInfo']['firstName'] . ' ' . $data['personalInfo']['lastName'];
                $userEmail = $data['personalInfo']['email'];
                sendApplicationConfirmation($userEmail, $userName, $applicationId);
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
        error_log("Application Error: " . $e->getMessage());
        sendError('Failed to process application', 500);
    }
}

// TRACK APPLICATION
else if ($method === 'POST' && $action === 'track') {
    $data = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($data['applicationId']) || !isset($data['dateOfBirth'])) {
        sendError('Application ID and Date of Birth are required', 400);
    }
    
    try {
        $stmt = $conn->prepare("SELECT * FROM applications WHERE application_id = ?");
        $stmt->execute([$data['applicationId']]);
        $application = $stmt->fetch();
        
        if (!$application) {
            sendError('Application not found', 404);
        }
        
        // Verify date of birth
        if ($application['date_of_birth'] !== $data['dateOfBirth']) {
            sendError('Invalid credentials', 401);
        }
        
        // Get status history
        $stmt = $conn->prepare("SELECT status, remarks, created_at FROM application_history WHERE application_id = ? ORDER BY created_at ASC");
        $stmt->execute([$application['id']]);
        $history = $stmt->fetchAll();
        
        sendSuccess('Application found', [
            'application' => [
                'applicationId' => $application['application_id'],
                'status' => $application['status'],
                'submittedAt' => $application['submitted_at'],
                'reviewedAt' => $application['reviewed_at'],
                'reviewRemarks' => $application['review_remarks'],
                'statusHistory' => $history,
                'personalInfo' => [
                    'firstName' => $application['first_name'],
                    'lastName' => $application['last_name'],
                    'email' => $application['email'],
                    'mobile' => $application['mobile']
                ]
            ]
        ]);
        
    } catch(PDOException $e) {
        error_log("Track Error: " . $e->getMessage());
        sendError('Failed to track application', 500);
    }
}

// GET MY APPLICATIONS
else if ($method === 'GET' && $action === 'my') {
    $token = JWT::getTokenFromHeader();
    $userData = JWT::decode($token);
    
    if (!$userData) {
        sendError('Unauthorized. Please login.', 401);
    }
    
    try {
        $stmt = $conn->prepare("SELECT * FROM applications WHERE user_id = ? ORDER BY created_at DESC");
        $stmt->execute([$userData['userId']]);
        $applications = $stmt->fetchAll();
        
        sendSuccess('Applications retrieved', [
            'applications' => $applications
        ]);
        
    } catch(PDOException $e) {
        error_log("Get Applications Error: " . $e->getMessage());
        sendError('Failed to retrieve applications', 500);
    }
}

// GET SINGLE APPLICATION BY ID
else if ($method === 'GET' && $action === 'view') {
    $token = JWT::getTokenFromHeader();
    $userData = JWT::decode($token);
    
    if (!$userData) {
        sendError('Unauthorized. Please login.', 401);
    }
    
    $applicationId = $_GET['id'] ?? '';
    
    if (empty($applicationId)) {
        sendError('Application ID is required', 400);
    }
    
    try {
        // Get application and verify it belongs to the logged-in user
        $stmt = $conn->prepare("SELECT * FROM applications WHERE application_id = ? AND user_id = ?");
        $stmt->execute([$applicationId, $userData['userId']]);
        $application = $stmt->fetch();
        
        if (!$application) {
            sendError('Application not found or access denied', 404);
        }
        
        sendSuccess('Application retrieved', [
            'application' => $application
        ]);
        
    } catch(PDOException $e) {
        error_log("Get Application Error: " . $e->getMessage());
        sendError('Failed to retrieve application', 500);
    }
}

else {
    sendError('Invalid endpoint or method', 404);
}
?>
