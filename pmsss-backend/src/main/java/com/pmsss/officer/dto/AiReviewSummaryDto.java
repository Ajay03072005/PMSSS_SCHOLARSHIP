package com.pmsss.officer.dto;

import com.pmsss.ai.model.AiConsistencyMatchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewSummaryDto {
    private String applicationId;
    private String applicantName;
    private BigDecimal annualIncome;
    private String status;
    private int documentsCount;
    private double overallMatchScore;
    private List<AiConsistencyMatchResult> comparisonItems;
    private boolean duplicateSuspected;
    private double duplicateMatchPercentage;
    private String duplicateCandidateId;
    private int activeAnomaliesCount;
    private List<String> anomalyReasons;
    private String recommendedAction;
}
