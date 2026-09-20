package com.pmsss.notification.service;

import com.pmsss.application.entity.Application;
import com.pmsss.notification.entity.NotificationLog;
import com.pmsss.notification.entity.NotificationPreference;
import com.pmsss.notification.enums.NotificationChannel;
import com.pmsss.notification.enums.NotificationStatus;
import com.pmsss.notification.repository.NotificationLogRepository;
import com.pmsss.notification.repository.NotificationPreferenceRepository;
import com.pmsss.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final InAppNotificationService inAppService;
    private final NotificationLogRepository logRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    /**
     * Dispatches notification across configured channels (Email, SMS, In-App).
     * Respects user preferences for non-critical events.
     * Fault-tolerant: errors are captured in NotificationLog and never crash application workflow.
     */
    @Async
    public void dispatch(User recipient, Application application, String type, String title, String message, boolean isCritical) {
        if (recipient == null) return;

        // 1. Always create In-App Notification
        try {
            inAppService.createInAppNotification(recipient, application, type, title, message);
            createLog(recipient, recipient.getEmail(), type, NotificationChannel.IN_APP, title, message, NotificationStatus.SENT, null);
        } catch (Exception e) {
            log.error("Failed to generate in-app notification for {}: {}", recipient.getEmail(), e.getMessage());
            createLog(recipient, recipient.getEmail(), type, NotificationChannel.IN_APP, title, message, NotificationStatus.FAILED, e.getMessage());
        }

        // Retrieve user preferences
        NotificationPreference prefs = preferenceRepository.findByUserId(recipient.getId())
                .orElse(NotificationPreference.builder().user(recipient).build());

        // 2. Email Dispatch
        boolean shouldSendEmail = isCritical || shouldSendEmailForType(type, prefs);
        if (shouldSendEmail && recipient.getEmail() != null) {
            sendEmailWithRetry(recipient, recipient.getEmail(), type, title, message);
        }

        // 3. SMS Dispatch
        boolean shouldSendSms = isCritical || shouldSendSmsForType(type, prefs);
        if (shouldSendSms && recipient.getMobile() != null && !recipient.getMobile().isBlank()) {
            sendSmsWithRetry(recipient, recipient.getMobile(), type, title, message);
        }
    }

    private boolean shouldSendEmailForType(String type, NotificationPreference prefs) {
        if (type.contains("DOCUMENT")) return Boolean.TRUE.equals(prefs.getEmailDocumentUpdates());
        if (type.contains("PAYMENT") || type.contains("DISBURSE")) return Boolean.TRUE.equals(prefs.getEmailPaymentUpdates());
        return Boolean.TRUE.equals(prefs.getEmailApplicationUpdates());
    }

    private boolean shouldSendSmsForType(String type, NotificationPreference prefs) {
        if (type.contains("PAYMENT") || type.contains("DISBURSE")) return Boolean.TRUE.equals(prefs.getSmsPaymentUpdates());
        return Boolean.TRUE.equals(prefs.getSmsApplicationUpdates());
    }

    public void sendEmailWithRetry(User user, String email, String type, String title, String message) {
        NotificationLog logEntry = createLog(user, email, type, NotificationChannel.EMAIL, title, message, NotificationStatus.PROCESSING, null);
        executeWithRetry(logEntry, () -> emailService.sendEmail(email, title, message));
    }

    public void sendSmsWithRetry(User user, String phone, String type, String title, String message) {
        NotificationLog logEntry = createLog(user, phone, type, NotificationChannel.SMS, title, message, NotificationStatus.PROCESSING, null);
        executeWithRetry(logEntry, () -> smsService.sendSms(phone, title + ": " + message));
    }

    private void executeWithRetry(NotificationLog logEntry, DeliveryAction action) {
        int maxAttempts = 3;
        boolean success = false;
        String lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            logEntry.setRetryCount(attempt);
            logEntry.setLastAttemptAt(LocalDateTime.now());
            try {
                success = action.execute();
                if (success) {
                    logEntry.setStatus(NotificationStatus.SENT);
                    logEntry.setSentAt(LocalDateTime.now());
                    logEntry.setFailureReason(null);
                    break;
                } else {
                    lastError = "Provider indicated delivery failure on attempt " + attempt;
                }
            } catch (Exception ex) {
                lastError = ex.getMessage();
            }
        }

        if (!success) {
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setFailureReason(lastError != null ? lastError : "Exceeded maximum retry attempts");
        }

        logRepository.save(logEntry);
    }

    @Transactional
    public boolean retryNotification(Long logId) {
        return logRepository.findById(logId).map(logEntry -> {
            if (logEntry.getStatus() == NotificationStatus.SENT) return true;

            boolean success = false;
            try {
                if (logEntry.getChannel() == NotificationChannel.EMAIL) {
                    success = emailService.sendEmail(logEntry.getRecipient(), logEntry.getTitle(), logEntry.getMessage());
                } else if (logEntry.getChannel() == NotificationChannel.SMS) {
                    success = smsService.sendSms(logEntry.getRecipient(), logEntry.getTitle() + ": " + logEntry.getMessage());
                } else {
                    success = true;
                }
            } catch (Exception ex) {
                logEntry.setFailureReason(ex.getMessage());
            }

            logEntry.setLastAttemptAt(LocalDateTime.now());
            logEntry.setRetryCount(logEntry.getRetryCount() + 1);
            if (success) {
                logEntry.setStatus(NotificationStatus.SENT);
                logEntry.setSentAt(LocalDateTime.now());
                logEntry.setFailureReason(null);
            } else {
                logEntry.setStatus(NotificationStatus.FAILED);
            }
            logRepository.save(logEntry);
            return success;
        }).orElse(false);
    }

    private NotificationLog createLog(User user, String recipient, String type, NotificationChannel channel, String title, String message, NotificationStatus status, String error) {
        NotificationLog notifLog = NotificationLog.builder()
                .user(user)
                .recipient(recipient != null ? recipient : "UNKNOWN")
                .notificationType(type)
                .channel(channel)
                .title(title)
                .message(message)
                .status(status)
                .retryCount(status == NotificationStatus.SENT ? 1 : 0)
                .failureReason(error)
                .sentAt(status == NotificationStatus.SENT ? LocalDateTime.now() : null)
                .build();
        return logRepository.save(notifLog);
    }

    @FunctionalInterface
    private interface DeliveryAction {
        boolean execute() throws Exception;
    }
}
