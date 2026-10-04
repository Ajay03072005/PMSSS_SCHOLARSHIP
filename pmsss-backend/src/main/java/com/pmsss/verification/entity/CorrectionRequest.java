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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public static CorrectionRequestBuilder builder() {
        return new CorrectionRequestBuilder();
    }

    public static class CorrectionRequestBuilder {
        private Long id;
        private String applicationId;
        private String documentType;
        private String fieldKey;
        private String reason;
        private String requestedBy;
        private LocalDateTime requestedAt;
        private Boolean isResolved = false;
        private LocalDateTime resolvedAt;

        public CorrectionRequestBuilder id(Long id) { this.id = id; return this; }
        public CorrectionRequestBuilder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public CorrectionRequestBuilder documentType(String documentType) { this.documentType = documentType; return this; }
        public CorrectionRequestBuilder fieldKey(String fieldKey) { this.fieldKey = fieldKey; return this; }
        public CorrectionRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public CorrectionRequestBuilder requestedBy(String requestedBy) { this.requestedBy = requestedBy; return this; }
        public CorrectionRequestBuilder requestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; return this; }
        public CorrectionRequestBuilder isResolved(Boolean isResolved) { this.isResolved = isResolved; return this; }
        public CorrectionRequestBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public CorrectionRequest build() {
            CorrectionRequest cr = new CorrectionRequest();
            cr.id = this.id;
            cr.applicationId = this.applicationId;
            cr.documentType = this.documentType;
            cr.fieldKey = this.fieldKey;
            cr.reason = this.reason;
            cr.requestedBy = this.requestedBy;
            cr.requestedAt = this.requestedAt;
            cr.isResolved = this.isResolved;
            cr.resolvedAt = this.resolvedAt;
            return cr;
        }
    }
}

