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
public class AiEligibilityResult {
    private String status; // POTENTIALLY_ELIGIBLE, POTENTIALLY_NOT_ELIGIBLE, MISSING_INFORMATION
    private String summary;
    private List<String> satisfiedCriteria;
    private List<String> failedCriteria;
    private List<String> missingInformation;
    private List<String> applicableGuidelines;
    private String disclaimer;
}
