package com.pmsss.document.entity;

import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_verifications", indexes = {
        @Index(name = "idx_dv_doc_id", columnList = "document_id"),
        @Index(name = "idx_dv_status", columnList = "verification_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true, length = 64)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "verification_status", nullable = false, length = 30)
    private String verificationStatus; // PENDING, VERIFIED, REJECTED, NEEDS_REVIEW

    @Column(name = "verification_method", nullable = false, length = 30)
    private String verificationMethod; // MANUAL, AI_ASSISTED, QR_VERIFICATION, OFFICIAL_SOURCE, DIGITAL_SIGNATURE

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @PrePersist
    public void prePersist() {
        if (this.uniqueId == null || this.uniqueId.isBlank()) {
            this.uniqueId = "VER-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.verifiedAt == null) {
            this.verifiedAt = LocalDateTime.now();
        }
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public User getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public static DocumentVerificationBuilder builder() {
        return new DocumentVerificationBuilder();
    }

    public static class DocumentVerificationBuilder {
        private Long id;
        private String uniqueId;
        private Document document;
        private String verificationStatus;
        private String verificationMethod;
        private Double confidenceScore;
        private User verifiedBy;
        private String verificationNotes;
        private LocalDateTime verifiedAt;

        public DocumentVerificationBuilder id(Long id) { this.id = id; return this; }
        public DocumentVerificationBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
        public DocumentVerificationBuilder document(Document document) { this.document = document; return this; }
        public DocumentVerificationBuilder verificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public DocumentVerificationBuilder verificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; return this; }
        public DocumentVerificationBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public DocumentVerificationBuilder verifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public DocumentVerificationBuilder verificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; return this; }
        public DocumentVerificationBuilder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }

        public DocumentVerification build() {
            DocumentVerification dv = new DocumentVerification();
            dv.id = this.id;
            dv.uniqueId = this.uniqueId;
            dv.document = this.document;
            dv.verificationStatus = this.verificationStatus;
            dv.verificationMethod = this.verificationMethod;
            dv.confidenceScore = this.confidenceScore;
            dv.verifiedBy = this.verifiedBy;
            dv.verificationNotes = this.verificationNotes;
            dv.verifiedAt = this.verifiedAt;
            return dv;
        }
    }
}

