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
}
