package com.pmsss.notification.entity;

import com.pmsss.application.entity.Application;
import com.pmsss.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user_id", columnList = "user_id"),
        @Index(name = "idx_notif_is_read", columnList = "is_read")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", length = 50, nullable = false, unique = true)
    private String uniqueId;

    @PrePersist
    public void ensureUniqueId() {
        if (uniqueId == null || uniqueId.isBlank()) {
            uniqueId = "NOTIF-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(nullable = false, length = 50)
    private String type; // APPLICATION_SUBMITTED, DOCUMENT_VERIFIED, DOCUMENT_REJECTED, etc.

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(Long id, String uniqueId, User user, Application application, String type, String title, String message, Boolean isRead, LocalDateTime readAt, LocalDateTime createdAt) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.user = user;
        this.application = application;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead != null ? isRead : false;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long id;
        private String uniqueId;
        private User user;
        private Application application;
        private String type;
        private String title;
        private String message;
        private Boolean isRead = false;
        private LocalDateTime readAt;
        private LocalDateTime createdAt;

        public NotificationBuilder id(Long id) { this.id = id; return this; }
        public NotificationBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
        public NotificationBuilder user(User user) { this.user = user; return this; }
        public NotificationBuilder application(Application application) { this.application = application; return this; }
        public NotificationBuilder type(String type) { this.type = type; return this; }
        public NotificationBuilder title(String title) { this.title = title; return this; }
        public NotificationBuilder message(String message) { this.message = message; return this; }
        public NotificationBuilder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public NotificationBuilder readAt(LocalDateTime readAt) { this.readAt = readAt; return this; }
        public NotificationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notification build() {
            return new Notification(id, uniqueId, user, application, type, title, message, isRead, readAt, createdAt);
        }
    }
}

