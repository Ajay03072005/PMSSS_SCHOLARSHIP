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
}
