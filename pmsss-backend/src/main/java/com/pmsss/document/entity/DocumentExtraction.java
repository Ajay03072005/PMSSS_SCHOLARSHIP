package com.pmsss.document.entity;

import com.pmsss.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_extractions", indexes = {
        @Index(name = "idx_de_doc_id", columnList = "document_id"),
        @Index(name = "idx_de_unique_id", columnList = "unique_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExtraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true, length = 64)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "extraction_status", nullable = false, length = 30)
    @Builder.Default
    private String extractionStatus = "SUCCESS"; // SUCCESS, FAILED, PARTIAL

    @Column(name = "extracted_name", length = 255)
    private String extractedName;

    @Column(name = "extracted_dob")
    private LocalDate extractedDob;

    @Column(name = "extracted_document_number", length = 100)
    private String extractedDocumentNumber;

    @Column(name = "extracted_address", columnDefinition = "TEXT")
    private String extractedAddress;

    @Column(name = "extracted_income", precision = 15, scale = 2)
    private BigDecimal extractedIncome;

    @Column(name = "extracted_issue_date")
    private LocalDate extractedIssueDate;

    @Column(name = "extracted_expiry_date")
    private LocalDate extractedExpiryDate;

    @Column(name = "extracted_data", columnDefinition = "LONGTEXT")
    private String extractedData; // Structured JSON

    @Column(name = "ocr_confidence")
    private Double ocrConfidence;

    @Column(name = "extracted_at")
    private LocalDateTime extractedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.uniqueId == null || this.uniqueId.isBlank()) {
            this.uniqueId = "EXT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.extractedAt == null) {
            this.extractedAt = LocalDateTime.now();
        }
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public String getExtractionStatus() { return extractionStatus; }
    public void setExtractionStatus(String extractionStatus) { this.extractionStatus = extractionStatus; }

    public String getExtractedName() { return extractedName; }
    public void setExtractedName(String extractedName) { this.extractedName = extractedName; }

    public LocalDate getExtractedDob() { return extractedDob; }
    public void setExtractedDob(LocalDate extractedDob) { this.extractedDob = extractedDob; }

    public String getExtractedDocumentNumber() { return extractedDocumentNumber; }
    public void setExtractedDocumentNumber(String extractedDocumentNumber) { this.extractedDocumentNumber = extractedDocumentNumber; }

    public String getExtractedAddress() { return extractedAddress; }
    public void setExtractedAddress(String extractedAddress) { this.extractedAddress = extractedAddress; }

    public BigDecimal getExtractedIncome() { return extractedIncome; }
    public void setExtractedIncome(BigDecimal extractedIncome) { this.extractedIncome = extractedIncome; }

    public LocalDate getExtractedIssueDate() { return extractedIssueDate; }
    public void setExtractedIssueDate(LocalDate extractedIssueDate) { this.extractedIssueDate = extractedIssueDate; }

    public LocalDate getExtractedExpiryDate() { return extractedExpiryDate; }
    public void setExtractedExpiryDate(LocalDate extractedExpiryDate) { this.extractedExpiryDate = extractedExpiryDate; }

    public String getExtractedData() { return extractedData; }
    public void setExtractedData(String extractedData) { this.extractedData = extractedData; }

    public Double getOcrConfidence() { return ocrConfidence; }
    public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }

    public LocalDateTime getExtractedAt() { return extractedAt; }
    public void setExtractedAt(LocalDateTime extractedAt) { this.extractedAt = extractedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DocumentExtractionBuilder builder() {
        return new DocumentExtractionBuilder();
    }

    public static class DocumentExtractionBuilder {
        private Long id;
        private String uniqueId;
        private Document document;
        private String extractionStatus = "SUCCESS";
        private String extractedName;
        private LocalDate extractedDob;
        private String extractedDocumentNumber;
        private String extractedAddress;
        private BigDecimal extractedIncome;
        private LocalDate extractedIssueDate;
        private LocalDate extractedExpiryDate;
        private String extractedData;
        private Double ocrConfidence;
        private LocalDateTime extractedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DocumentExtractionBuilder id(Long id) { this.id = id; return this; }
        public DocumentExtractionBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
        public DocumentExtractionBuilder document(Document document) { this.document = document; return this; }
        public DocumentExtractionBuilder extractionStatus(String extractionStatus) { this.extractionStatus = extractionStatus; return this; }
        public DocumentExtractionBuilder extractedName(String extractedName) { this.extractedName = extractedName; return this; }
        public DocumentExtractionBuilder extractedDob(LocalDate extractedDob) { this.extractedDob = extractedDob; return this; }
        public DocumentExtractionBuilder extractedDocumentNumber(String extractedDocumentNumber) { this.extractedDocumentNumber = extractedDocumentNumber; return this; }
        public DocumentExtractionBuilder extractedAddress(String extractedAddress) { this.extractedAddress = extractedAddress; return this; }
        public DocumentExtractionBuilder extractedIncome(BigDecimal extractedIncome) { this.extractedIncome = extractedIncome; return this; }
        public DocumentExtractionBuilder extractedIssueDate(LocalDate extractedIssueDate) { this.extractedIssueDate = extractedIssueDate; return this; }
        public DocumentExtractionBuilder extractedExpiryDate(LocalDate extractedExpiryDate) { this.extractedExpiryDate = extractedExpiryDate; return this; }
        public DocumentExtractionBuilder extractedData(String extractedData) { this.extractedData = extractedData; return this; }
        public DocumentExtractionBuilder ocrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; return this; }
        public DocumentExtractionBuilder extractedAt(LocalDateTime extractedAt) { this.extractedAt = extractedAt; return this; }
        public DocumentExtractionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DocumentExtractionBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public DocumentExtraction build() {
            DocumentExtraction de = new DocumentExtraction();
            de.id = this.id;
            de.uniqueId = this.uniqueId;
            de.document = this.document;
            de.extractionStatus = this.extractionStatus;
            de.extractedName = this.extractedName;
            de.extractedDob = this.extractedDob;
            de.extractedDocumentNumber = this.extractedDocumentNumber;
            de.extractedAddress = this.extractedAddress;
            de.extractedIncome = this.extractedIncome;
            de.extractedIssueDate = this.extractedIssueDate;
            de.extractedExpiryDate = this.extractedExpiryDate;
            de.extractedData = this.extractedData;
            de.ocrConfidence = this.ocrConfidence;
            de.extractedAt = this.extractedAt;
            de.createdAt = this.createdAt;
            de.updatedAt = this.updatedAt;
            return de;
        }
    }
}

