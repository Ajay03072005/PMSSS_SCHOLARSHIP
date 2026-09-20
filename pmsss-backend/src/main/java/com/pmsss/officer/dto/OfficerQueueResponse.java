package com.pmsss.officer.dto;

import com.pmsss.officer.entity.ApplicationAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerQueueResponse {
    private int totalActive;
    private int quickReviewCount;
    private int needsAttentionCount;
    private int highPriorityCount;
    private int overdueCount;

    private List<ApplicationAssignment> quickReviewList;
    private List<ApplicationAssignment> needsAttentionList;
    private ApplicationAssignment recommendedNext;
}
