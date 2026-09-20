package com.pmsss.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConsistencyMatchResult {
    private String applicationField;
    private String applicationValue;
    private String documentExtractedValue;
    private String matchStatus; // MATCH, POTENTIAL_MISMATCH, MISSING_INFORMATION
    private double confidence;
    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private String applicationId;
        private boolean overallConsistent;
        private double averageConfidence;
        private List<AiConsistencyMatchResult> fieldMatches;
        private List<String> flaggedDiscrepancies;
    }
}
