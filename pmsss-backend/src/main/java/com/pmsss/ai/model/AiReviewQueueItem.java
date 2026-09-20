package com.pmsss.ai.model;

import com.pmsss.common.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewQueueItem {
    private String applicationId;
    private String studentName;
    private String category;
    private ApplicationStatus status;
    private String attentionLevel; // HIGH_ATTENTION, NORMAL, LOW_ATTENTION
    private int priorityScore; // 0 to 100
    private long waitingDays;
    private List<String> attentionReasons;
    private LocalDateTime submittedAt;
}
