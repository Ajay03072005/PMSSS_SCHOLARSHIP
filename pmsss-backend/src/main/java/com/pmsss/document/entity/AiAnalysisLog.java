package com.pmsss.document.entity;

import com.pmsss.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analysis_logs", indexes = {
        @Index(name = "idx_aal_doc_id", columnList = "document_id"),
        @Index(name = "idx_aal_app_id", columnList = "application_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnalysisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true, length = 64)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType; // OCR_EXTRACTION, DOCUMENT_MATCHING, INTELLIGENCE_CHECK

    @Column(name = "provider", length = 100)
    private String provider;

    @Column(name = "processing_status", nullable = false, length = 30)
    private String processingStatus; // SUCCESS, FAILED, LOW_CONFIDENCE

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (this.uniqueId == null || this.uniqueId.isBlank()) {
            this.uniqueId = "AILOG-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
}
