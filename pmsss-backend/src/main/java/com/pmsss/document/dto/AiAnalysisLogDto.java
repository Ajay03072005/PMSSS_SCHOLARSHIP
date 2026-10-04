package com.pmsss.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisLogDto {
    private String uniqueId;
    private String operationType;
    private String provider;
    private String processingStatus;
    private Double confidenceScore;
    private Long processingTimeMs;
    private String errorMessage;
    private LocalDateTime processedAt;
}
