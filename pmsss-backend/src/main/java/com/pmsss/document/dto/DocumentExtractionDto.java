package com.pmsss.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentExtractionDto {
    private String uniqueId;
    private String extractionStatus;
    private String extractedName;
    private LocalDate extractedDob;
    private String extractedDocumentNumber;
    private String extractedAddress;
    private BigDecimal extractedIncome;
    private LocalDate extractedIssueDate;
    private LocalDate extractedExpiryDate;
    private String extractedDataJson;
    private Double ocrConfidence;
    private LocalDateTime extractedAt;
}
