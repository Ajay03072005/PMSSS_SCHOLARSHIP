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
public class MatchResultDto {
    private String uniqueId;
    private String fieldName;
    private String applicationValue;
    private String extractedValue;
    private String matchStatus; // MATCH, PARTIAL_MATCH, MISMATCH, NOT_AVAILABLE, NOT_APPLICABLE
    private Double similarityScore;
    private String mismatchReason;
    private LocalDateTime createdAt;
}
