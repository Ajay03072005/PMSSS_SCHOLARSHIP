package com.pmsss.notification.entity;

import com.pmsss.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences", indexes = {
        @Index(name = "idx_notif_pref_user", columnList = "user_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "email_application_updates", nullable = false)
    @Builder.Default
    private Boolean emailApplicationUpdates = true;

    @Column(name = "email_document_updates", nullable = false)
    @Builder.Default
    private Boolean emailDocumentUpdates = true;

    @Column(name = "email_payment_updates", nullable = false)
    @Builder.Default
    private Boolean emailPaymentUpdates = true;

    @Column(name = "sms_application_updates", nullable = false)
    @Builder.Default
    private Boolean smsApplicationUpdates = true;

    @Column(name = "sms_payment_updates", nullable = false)
    @Builder.Default
    private Boolean smsPaymentUpdates = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Boolean getEmailApplicationUpdates() { return emailApplicationUpdates; }
    public void setEmailApplicationUpdates(Boolean emailApplicationUpdates) { this.emailApplicationUpdates = emailApplicationUpdates; }

    public Boolean getEmailDocumentUpdates() { return emailDocumentUpdates; }
    public void setEmailDocumentUpdates(Boolean emailDocumentUpdates) { this.emailDocumentUpdates = emailDocumentUpdates; }

    public Boolean getEmailPaymentUpdates() { return emailPaymentUpdates; }
    public void setEmailPaymentUpdates(Boolean emailPaymentUpdates) { this.emailPaymentUpdates = emailPaymentUpdates; }

    public Boolean getSmsApplicationUpdates() { return smsApplicationUpdates; }
    public void setSmsApplicationUpdates(Boolean smsApplicationUpdates) { this.smsApplicationUpdates = smsApplicationUpdates; }

    public Boolean getSmsPaymentUpdates() { return smsPaymentUpdates; }
    public void setSmsPaymentUpdates(Boolean smsPaymentUpdates) { this.smsPaymentUpdates = smsPaymentUpdates; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static NotificationPreferenceBuilder builder() {
        return new NotificationPreferenceBuilder();
    }

    public static class NotificationPreferenceBuilder {
        private Long id;
        private User user;
        private Boolean emailApplicationUpdates = true;
        private Boolean emailDocumentUpdates = true;
        private Boolean emailPaymentUpdates = true;
        private Boolean smsApplicationUpdates = true;
        private Boolean smsPaymentUpdates = true;
        private LocalDateTime updatedAt;

        public NotificationPreferenceBuilder id(Long id) { this.id = id; return this; }
        public NotificationPreferenceBuilder user(User user) { this.user = user; return this; }
        public NotificationPreferenceBuilder emailApplicationUpdates(Boolean emailApplicationUpdates) { this.emailApplicationUpdates = emailApplicationUpdates; return this; }
        public NotificationPreferenceBuilder emailDocumentUpdates(Boolean emailDocumentUpdates) { this.emailDocumentUpdates = emailDocumentUpdates; return this; }
        public NotificationPreferenceBuilder emailPaymentUpdates(Boolean emailPaymentUpdates) { this.emailPaymentUpdates = emailPaymentUpdates; return this; }
        public NotificationPreferenceBuilder smsApplicationUpdates(Boolean smsApplicationUpdates) { this.smsApplicationUpdates = smsApplicationUpdates; return this; }
        public NotificationPreferenceBuilder smsPaymentUpdates(Boolean smsPaymentUpdates) { this.smsPaymentUpdates = smsPaymentUpdates; return this; }
        public NotificationPreferenceBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public NotificationPreference build() {
            NotificationPreference np = new NotificationPreference();
            np.id = this.id;
            np.user = this.user;
            np.emailApplicationUpdates = this.emailApplicationUpdates;
            np.emailDocumentUpdates = this.emailDocumentUpdates;
            np.emailPaymentUpdates = this.emailPaymentUpdates;
            np.smsApplicationUpdates = this.smsApplicationUpdates;
            np.smsPaymentUpdates = this.smsPaymentUpdates;
            np.updatedAt = this.updatedAt;
            return np;
        }
    }
}

