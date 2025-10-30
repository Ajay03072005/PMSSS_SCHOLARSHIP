<?php
// Email Configuration

// Enable development mode to log emails instead of sending
define('DEVELOPMENT_MODE', true); // Set to false in production

// SMTP Settings (Configure these for production)
define('SMTP_HOST', 'smtp.gmail.com');
define('SMTP_PORT', 587);
define('SMTP_SECURE', 'tls'); // or 'ssl' for port 465
define('SMTP_USERNAME', 'your-email@gmail.com'); // TODO: Change this to your email
define('SMTP_PASSWORD', 'your-app-password');    // TODO: Change this to your Gmail App Password
define('SMTP_FROM_EMAIL', 'your-email@gmail.com'); // TODO: Change this
define('SMTP_FROM_NAME', 'PMSSS Scholarship Portal');

// Email Templates Configuration
define('EMAIL_LOGO_URL', 'https://your-domain.com/assets/images/logo.png');
define('PORTAL_URL', 'http://localhost:8000');

/**
 * Send email using PHP's mail() function or SMTP
 * For production, use PHPMailer with SMTP
 */
function sendEmail($to, $subject, $body, $isHTML = true) {
    // For now, we'll use PHP's mail function
    // In production, replace this with PHPMailer
    
    $headers = "MIME-Version: 1.0" . "\r\n";
    if ($isHTML) {
        $headers .= "Content-type:text/html;charset=UTF-8" . "\r\n";
    }
    $headers .= 'From: ' . SMTP_FROM_NAME . ' <' . SMTP_FROM_EMAIL . '>' . "\r\n";
    
    // Log email for debugging
    $logFile = __DIR__ . '/../logs/email-log.txt';
    $logMessage = date('Y-m-d H:i:s') . " - Email to: $to, Subject: $subject\n";
    file_put_contents($logFile, $logMessage, FILE_APPEND);
    
    // In development, just log the email instead of sending
    if (defined('DEVELOPMENT_MODE') && DEVELOPMENT_MODE === true) {
        file_put_contents($logFile, "Body: $body\n\n", FILE_APPEND);
        return true;
    }
    
    return mail($to, $subject, $body, $headers);
}

/**
 * Send application confirmation email
 */
function sendApplicationConfirmation($email, $name, $applicationId) {
    $subject = "Application Submitted Successfully - $applicationId";
    
    $body = getEmailTemplate([
        'title' => 'Application Submitted Successfully!',
        'greeting' => "Dear $name,",
        'message' => "Thank you for submitting your application for the PMSSS Scholarship program. Your application has been received successfully.",
        'details' => [
            'Application ID' => $applicationId,
            'Status' => 'Submitted',
            'Submitted On' => date('d M Y, h:i A')
        ],
        'note' => 'Please save your Application ID for future reference. You can track your application status using this ID on our portal.',
        'cta_text' => 'Track Application',
        'cta_link' => PORTAL_URL . '/status.html'
    ]);
    
    return sendEmail($email, $subject, $body, true);
}

/**
 * Send status update email
 */
function sendStatusUpdate($email, $name, $applicationId, $status, $remarks = '') {
    $statusMessages = [
        'submitted' => 'Your application has been submitted and is under review.',
        'under_review' => 'Your application is currently being reviewed by our team.',
        'approved' => 'Congratulations! Your application has been approved.',
        'rejected' => 'We regret to inform you that your application has not been approved.',
        'pending_documents' => 'Your application requires additional documents.',
        'on_hold' => 'Your application is currently on hold.'
    ];
    
    $subject = "Application Status Update - $applicationId";
    
    $body = getEmailTemplate([
        'title' => 'Application Status Update',
        'greeting' => "Dear $name,",
        'message' => $statusMessages[$status] ?? 'Your application status has been updated.',
        'details' => [
            'Application ID' => $applicationId,
            'Current Status' => ucfirst(str_replace('_', ' ', $status)),
            'Updated On' => date('d M Y, h:i A')
        ],
        'note' => $remarks ?: 'Please check the portal for more details.',
        'cta_text' => 'View Application',
        'cta_link' => PORTAL_URL . '/status.html'
    ]);
    
    return sendEmail($email, $subject, $body, true);
}

/**
 * Get email template HTML
 */
function getEmailTemplate($data) {
    $title = $data['title'] ?? 'PMSSS Notification';
    $greeting = $data['greeting'] ?? 'Dear User,';
    $message = $data['message'] ?? '';
    $details = $data['details'] ?? [];
    $note = $data['note'] ?? '';
    $ctaText = $data['cta_text'] ?? '';
    $ctaLink = $data['cta_link'] ?? '';
    
    $detailsHTML = '';
    if (!empty($details)) {
        $detailsHTML = '<table style="width: 100%; margin: 20px 0; border-collapse: collapse;">';
        foreach ($details as $label => $value) {
            $detailsHTML .= '<tr>';
            $detailsHTML .= '<td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f5f5f5;">' . htmlspecialchars($label) . '</td>';
            $detailsHTML .= '<td style="padding: 10px; border: 1px solid #ddd;">' . htmlspecialchars($value) . '</td>';
            $detailsHTML .= '</tr>';
        }
        $detailsHTML .= '</table>';
    }
    
    $ctaHTML = '';
    if ($ctaText && $ctaLink) {
        $ctaHTML = '<div style="text-align: center; margin: 30px 0;">
            <a href="' . htmlspecialchars($ctaLink) . '" style="background-color: #e91e63; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">' . htmlspecialchars($ctaText) . '</a>
        </div>';
    }
    
    return '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>' . htmlspecialchars($title) . '</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4;">
    <div style="max-width: 600px; margin: 20px auto; background-color: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
        <!-- Header -->
        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
            <h1 style="color: white; margin: 0; font-size: 28px;">
                <span style="color: #ffd700;">P</span><span style="color: #ff69b4;">M</span><span style="color: #87ceeb;">SSS</span>
            </h1>
            <p style="color: rgba(255,255,255,0.9); margin: 5px 0 0 0;">Scholarship Portal</p>
        </div>
        
        <!-- Content -->
        <div style="padding: 40px 30px;">
            <h2 style="color: #667eea; margin-top: 0;">' . htmlspecialchars($title) . '</h2>
            <p style="font-size: 16px; margin: 20px 0;">' . htmlspecialchars($greeting) . '</p>
            <p style="font-size: 15px; line-height: 1.8;">' . nl2br(htmlspecialchars($message)) . '</p>
            
            ' . $detailsHTML . '
            
            ' . ($note ? '<div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0;">
                <p style="margin: 0; color: #856404;"><strong>Note:</strong> ' . nl2br(htmlspecialchars($note)) . '</p>
            </div>' : '') . '
            
            ' . $ctaHTML . '
            
            <p style="margin-top: 30px; color: #666; font-size: 14px;">
                If you have any questions, please contact us at <a href="mailto:support@pmsss.gov.in" style="color: #667eea;">support@pmsss.gov.in</a>
            </p>
        </div>
        
        <!-- Footer -->
        <div style="background-color: #f8f9fa; padding: 20px 30px; text-align: center; border-top: 1px solid #dee2e6;">
            <p style="margin: 0; color: #666; font-size: 13px;">
                &copy; ' . date('Y') . ' PMSSS Scholarship Portal. All rights reserved.<br>
                Government of India
            </p>
        </div>
    </div>
</body>
</html>';
}
?>
