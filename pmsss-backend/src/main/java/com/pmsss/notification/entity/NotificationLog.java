package com.pmsss.notification.entity;

import com.pmsss.notification.enums.NotificationChannel;
import com.pmsss.notification.enums.NotificationStatus;
import com.pmsss.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs", indexes = {
        @Index(name = "idx_notif_log_status", columnList = "status"),
        @Index(name = "idx_notif_log_recipient", columnList = "recipient"),
        @Index(name = "idx_notif_log_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String recipient;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notification_type", nullable = false, length = 60)
    private String notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Explicit Getters and Setters for robust javac compilation
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getLastAttemptAt() { return lastAttemptAt; }
    public void setLastAttemptAt(LocalDateTime lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public static NotificationLogBuilder builder() {
        return new NotificationLogBuilder();
    }

    public static class NotificationLogBuilder {
        private Long id;
        private String recipient;
        private User user;
        private String notificationType;
        private NotificationChannel channel;
        private String title;
        private String message;
        private NotificationStatus status = NotificationStatus.PENDING;
        private Integer retryCount = 0;
        private Integer maxRetries = 3;
        private String failureReason;
        private LocalDateTime lastAttemptAt;
        private LocalDateTime sentAt;
        private LocalDateTime createdAt;

        public NotificationLogBuilder id(Long id) { this.id = id; return this; }
        public NotificationLogBuilder recipient(String recipient) { this.recipient = recipient; return this; }
        public NotificationLogBuilder user(User user) { this.user = user; return this; }
        public NotificationLogBuilder notificationType(String notificationType) { this.notificationType = notificationType; return this; }
        public NotificationLogBuilder channel(NotificationChannel channel) { this.channel = channel; return this; }
        public NotificationLogBuilder title(String title) { this.title = title; return this; }
        public NotificationLogBuilder message(String message) { this.message = message; return this; }
        public NotificationLogBuilder status(NotificationStatus status) { this.status = status; return this; }
        public NotificationLogBuilder retryCount(Integer retryCount) { this.retryCount = retryCount; return this; }
        public NotificationLogBuilder maxRetries(Integer maxRetries) { this.maxRetries = maxRetries; return this; }
        public NotificationLogBuilder failureReason(String failureReason) { this.failureReason = failureReason; return this; }
        public NotificationLogBuilder lastAttemptAt(LocalDateTime lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; return this; }
        public NotificationLogBuilder sentAt(LocalDateTime sentAt) { this.sentAt = sentAt; return this; }
        public NotificationLogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationLog build() {
            NotificationLog nl = new NotificationLog();
            nl.id = this.id;
            nl.recipient = this.recipient;
            nl.user = this.user;
            nl.notificationType = this.notificationType;
            nl.channel = this.channel;
            nl.title = this.title;
            nl.message = this.message;
            nl.status = this.status;
            nl.retryCount = this.retryCount;
            nl.maxRetries = this.maxRetries;
            nl.failureReason = this.failureReason;
            nl.lastAttemptAt = this.lastAttemptAt;
            nl.sentAt = this.sentAt;
            nl.createdAt = this.createdAt;
            return nl;
        }
    }
}

