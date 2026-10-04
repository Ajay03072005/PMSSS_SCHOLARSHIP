package com.pmsss.document.entity;

import com.pmsss.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_extracted_data", indexes = {
        @Index(name = "idx_ext_doc_id", columnList = "document_id"),
        @Index(name = "idx_ext_app_id", columnList = "application_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExtractedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Extracted Fields
    private String extractedName;
    private LocalDate extractedDob;
    private String certificateNumber;
    private String institutionName;
    private String courseName;
    private BigDecimal extractedIncome;
    private LocalDate documentDate;
    private String ifscCode;
    private String bankAccountNumber;

    // Full structured text or JSON representation
    @Column(name = "raw_extracted_text", columnDefinition = "LONGTEXT")
    private String rawExtractedText;

    // Intelligence metadata
    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "extraction_source", length = 100)
    private String extractionSource; // PDFBOX_PARSER, OCR_ENGINE, AI_VISION

    // Consistency Match Result (from AI comparison)
    @Column(name = "consistency_status", length = 50)
    private String consistencyStatus; // MATCH, POTENTIAL_MISMATCH, MISSING_INFORMATION

    @Column(name = "consistency_notes", columnDefinition = "TEXT")
    private String consistencyNotes;

    @CreationTimestamp
    @Column(name = "extraction_timestamp", updatable = false)
    private LocalDateTime extractionTimestamp;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getExtractedName() { return extractedName; }
    public void setExtractedName(String extractedName) { this.extractedName = extractedName; }

    public LocalDate getExtractedDob() { return extractedDob; }
    public void setExtractedDob(LocalDate extractedDob) { this.extractedDob = extractedDob; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public BigDecimal getExtractedIncome() { return extractedIncome; }
    public void setExtractedIncome(BigDecimal extractedIncome) { this.extractedIncome = extractedIncome; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getRawExtractedText() { return rawExtractedText; }
    public void setRawExtractedText(String rawExtractedText) { this.rawExtractedText = rawExtractedText; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getExtractionSource() { return extractionSource; }
    public void setExtractionSource(String extractionSource) { this.extractionSource = extractionSource; }

    public String getConsistencyStatus() { return consistencyStatus; }
    public void setConsistencyStatus(String consistencyStatus) { this.consistencyStatus = consistencyStatus; }

    public String getConsistencyNotes() { return consistencyNotes; }
    public void setConsistencyNotes(String consistencyNotes) { this.consistencyNotes = consistencyNotes; }

    public static DocumentExtractedDataBuilder builder() {
        return new DocumentExtractedDataBuilder();
    }

    public static class DocumentExtractedDataBuilder {
        private Long id;
        private Document document;
        private Application application;
        private String extractedName;
        private LocalDate extractedDob;
        private String certificateNumber;
        private String institutionName;
        private String courseName;
        private BigDecimal extractedIncome;
        private LocalDate documentDate;
        private String ifscCode;
        private String bankAccountNumber;
        private String rawExtractedText;
        private Double confidenceScore;
        private String extractionSource;
        private String consistencyStatus;
        private String consistencyNotes;

        public DocumentExtractedDataBuilder id(Long id) { this.id = id; return this; }
        public DocumentExtractedDataBuilder document(Document document) { this.document = document; return this; }
        public DocumentExtractedDataBuilder application(Application application) { this.application = application; return this; }
        public DocumentExtractedDataBuilder extractedName(String extractedName) { this.extractedName = extractedName; return this; }
        public DocumentExtractedDataBuilder extractedDob(LocalDate extractedDob) { this.extractedDob = extractedDob; return this; }
        public DocumentExtractedDataBuilder certificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; return this; }
        public DocumentExtractedDataBuilder institutionName(String institutionName) { this.institutionName = institutionName; return this; }
        public DocumentExtractedDataBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public DocumentExtractedDataBuilder extractedIncome(BigDecimal extractedIncome) { this.extractedIncome = extractedIncome; return this; }
        public DocumentExtractedDataBuilder documentDate(LocalDate documentDate) { this.documentDate = documentDate; return this; }
        public DocumentExtractedDataBuilder ifscCode(String ifscCode) { this.ifscCode = ifscCode; return this; }
        public DocumentExtractedDataBuilder bankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; return this; }
        public DocumentExtractedDataBuilder rawExtractedText(String rawExtractedText) { this.rawExtractedText = rawExtractedText; return this; }
        public DocumentExtractedDataBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public DocumentExtractedDataBuilder extractionSource(String extractionSource) { this.extractionSource = extractionSource; return this; }
        public DocumentExtractedDataBuilder consistencyStatus(String consistencyStatus) { this.consistencyStatus = consistencyStatus; return this; }
        public DocumentExtractedDataBuilder consistencyNotes(String consistencyNotes) { this.consistencyNotes = consistencyNotes; return this; }

        public DocumentExtractedData build() {
            DocumentExtractedData d = new DocumentExtractedData();
            d.id = this.id;
            d.document = this.document;
            d.application = this.application;
            d.extractedName = this.extractedName;
            d.extractedDob = this.extractedDob;
            d.certificateNumber = this.certificateNumber;
            d.institutionName = this.institutionName;
            d.courseName = this.courseName;
            d.extractedIncome = this.extractedIncome;
            d.documentDate = this.documentDate;
            d.ifscCode = this.ifscCode;
            d.bankAccountNumber = this.bankAccountNumber;
            d.rawExtractedText = this.rawExtractedText;
            d.confidenceScore = this.confidenceScore;
            d.extractionSource = this.extractionSource;
            d.consistencyStatus = this.consistencyStatus;
            d.consistencyNotes = this.consistencyNotes;
            return d;
        }
    }
}


