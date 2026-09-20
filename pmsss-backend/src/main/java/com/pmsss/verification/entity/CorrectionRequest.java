package com.pmsss.verification.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_correction_requests", indexes = {
        @Index(name = "idx_corr_app_id", columnList = "application_id"),
        @Index(name = "idx_corr_resolved", columnList = "is_resolved")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CorrectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @Column(name = "document_type", length = 50)
    private String documentType; // AADHAAR, INCOME_CERTIFICATE, etc., or null if personal info

    @Column(name = "field_key", length = 50)
    private String fieldKey; // e.g. "annualIncome", "aadharNumber", "bankAccount"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "requested_by", length = 100)
    private String requestedBy;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "is_resolved", nullable = false)
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
