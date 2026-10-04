package com.pmsss.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailNotificationService.class);

    /**
     * Dispatches transactional email.
     * In development/staging, logs structured email payload and returns success.
     * Can seamlessly integrate Spring JavaMailSender / SendGrid / AWS SES.
     */
    public boolean sendEmail(String toEmail, String subject, String content) {
        try {
            log.info("📧 [EMAIL OUTBOUND] To: {} | Subject: '{}' | Content: {}", toEmail, subject, content);
            // Simulated delivery verification
            if (toEmail == null || !toEmail.contains("@")) {
                throw new IllegalArgumentException("Invalid recipient email address: " + toEmail);
            }
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage());
            return false;
        }
    }
}
