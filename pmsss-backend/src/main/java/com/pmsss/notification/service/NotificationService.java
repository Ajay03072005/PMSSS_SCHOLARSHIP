package com.pmsss.notification.service;

import com.pmsss.application.entity.Application;
import com.pmsss.notification.entity.Notification;
import com.pmsss.notification.repository.NotificationRepository;
import com.pmsss.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcherService;

    @Transactional
    public Notification sendNotification(User user, Application application, String type, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .application(application)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        log.info("Notification sent to user {}: [{}] {}", user != null ? user.getEmail() : "UNKNOWN", type, title);

        // Multi-channel dispatch (Email, SMS, In-App logging and retries)
        if (user != null) {
            dispatcherService.dispatch(user, application, type, title, message, false);
        }

        return notification;
    }

    @Transactional
    public Notification createNotification(User user, Application application, String type, String title, String message) {
        return sendNotification(user, application, type, title, message);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getId().equals(userId)) {
                n.setIsRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
