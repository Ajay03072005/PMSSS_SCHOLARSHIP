package com.pmsss.document.entity;

import com.pmsss.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_match_results", indexes = {
        @Index(name = "idx_dmr_doc_id", columnList = "document_id"),
        @Index(name = "idx_dmr_app_id", columnList = "application_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true, length = 64)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "application_value", columnDefinition = "TEXT")
    private String applicationValue;

    @Column(name = "extracted_value", columnDefinition = "TEXT")
    private String extractedValue;

    @Column(name = "match_status", nullable = false, length = 30)
    private String matchStatus; // MATCH, PARTIAL_MATCH, MISMATCH, NOT_AVAILABLE, NOT_APPLICABLE

    @Column(name = "similarity_score")
    private Double similarityScore;

    @Column(name = "mismatch_reason", columnDefinition = "TEXT")
    private String mismatchReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.uniqueId == null || this.uniqueId.isBlank()) {
            this.uniqueId = "DMR-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getApplicationValue() { return applicationValue; }
    public void setApplicationValue(String applicationValue) { this.applicationValue = applicationValue; }

    public String getExtractedValue() { return extractedValue; }
    public void setExtractedValue(String extractedValue) { this.extractedValue = extractedValue; }

    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public String getMismatchReason() { return mismatchReason; }
    public void setMismatchReason(String mismatchReason) { this.mismatchReason = mismatchReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static DocumentMatchResultBuilder builder() {
        return new DocumentMatchResultBuilder();
    }

    public static class DocumentMatchResultBuilder {
        private Long id;
        private String uniqueId;
        private Document document;
        private Application application;
        private String fieldName;
        private String applicationValue;
        private String extractedValue;
        private String matchStatus;
        private Double similarityScore;
        private String mismatchReason;
        private LocalDateTime createdAt;

        public DocumentMatchResultBuilder id(Long id) { this.id = id; return this; }
        public DocumentMatchResultBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
        public DocumentMatchResultBuilder document(Document document) { this.document = document; return this; }
        public DocumentMatchResultBuilder application(Application application) { this.application = application; return this; }
        public DocumentMatchResultBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public DocumentMatchResultBuilder applicationValue(String applicationValue) { this.applicationValue = applicationValue; return this; }
        public DocumentMatchResultBuilder extractedValue(String extractedValue) { this.extractedValue = extractedValue; return this; }
        public DocumentMatchResultBuilder matchStatus(String matchStatus) { this.matchStatus = matchStatus; return this; }
        public DocumentMatchResultBuilder similarityScore(Double similarityScore) { this.similarityScore = similarityScore; return this; }
        public DocumentMatchResultBuilder mismatchReason(String mismatchReason) { this.mismatchReason = mismatchReason; return this; }
        public DocumentMatchResultBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DocumentMatchResult build() {
            DocumentMatchResult dmr = new DocumentMatchResult();
            dmr.id = this.id;
            dmr.uniqueId = this.uniqueId;
            dmr.document = this.document;
            dmr.application = this.application;
            dmr.fieldName = this.fieldName;
            dmr.applicationValue = this.applicationValue;
            dmr.extractedValue = this.extractedValue;
            dmr.matchStatus = this.matchStatus;
            dmr.similarityScore = this.similarityScore;
            dmr.mismatchReason = this.mismatchReason;
            dmr.createdAt = this.createdAt;
            return dmr;
        }
    }
}

