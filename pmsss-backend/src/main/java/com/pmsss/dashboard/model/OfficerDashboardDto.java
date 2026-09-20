package com.pmsss.dashboard.model;

import com.pmsss.ai.entity.AnomalyAlertEntity;
import com.pmsss.ai.model.AiReviewQueueItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerDashboardDto {
    private long pendingApplications;
    private long pendingDocuments;
    private long applicationsRequiringAttention;
    private long activeAnomaliesCount;
    private List<AiReviewQueueItem> prioritizedQueue;
    private List<AnomalyAlertEntity> recentAnomalies;
    private Map<String, Long> processingStats;
}
