package com.pmsss.ai.model;

import com.pmsss.common.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

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

    public AiReviewQueueItem() {}

    public AiReviewQueueItem(String applicationId, String studentName, String category, ApplicationStatus status, String attentionLevel, int priorityScore, long waitingDays, List<String> attentionReasons, LocalDateTime submittedAt) {
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.category = category;
        this.status = status;
        this.attentionLevel = attentionLevel;
        this.priorityScore = priorityScore;
        this.waitingDays = waitingDays;
        this.attentionReasons = attentionReasons;
        this.submittedAt = submittedAt;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getAttentionLevel() { return attentionLevel; }
    public void setAttentionLevel(String attentionLevel) { this.attentionLevel = attentionLevel; }

    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int priorityScore) { this.priorityScore = priorityScore; }

    public long getWaitingDays() { return waitingDays; }
    public void setWaitingDays(long waitingDays) { this.waitingDays = waitingDays; }

    public List<String> getAttentionReasons() { return attentionReasons; }
    public void setAttentionReasons(List<String> attentionReasons) { this.attentionReasons = attentionReasons; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public static AiReviewQueueItemBuilder builder() {
        return new AiReviewQueueItemBuilder();
    }

    public static class AiReviewQueueItemBuilder {
        private String applicationId;
        private String studentName;
        private String category;
        private ApplicationStatus status;
        private String attentionLevel;
        private int priorityScore;
        private long waitingDays;
        private List<String> attentionReasons;
        private LocalDateTime submittedAt;

        public AiReviewQueueItemBuilder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public AiReviewQueueItemBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public AiReviewQueueItemBuilder category(String category) { this.category = category; return this; }
        public AiReviewQueueItemBuilder status(ApplicationStatus status) { this.status = status; return this; }
        public AiReviewQueueItemBuilder attentionLevel(String attentionLevel) { this.attentionLevel = attentionLevel; return this; }
        public AiReviewQueueItemBuilder priorityScore(int priorityScore) { this.priorityScore = priorityScore; return this; }
        public AiReviewQueueItemBuilder waitingDays(long waitingDays) { this.waitingDays = waitingDays; return this; }
        public AiReviewQueueItemBuilder attentionReasons(List<String> attentionReasons) { this.attentionReasons = attentionReasons; return this; }
        public AiReviewQueueItemBuilder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }

        public AiReviewQueueItem build() {
            return new AiReviewQueueItem(applicationId, studentName, category, status, attentionLevel, priorityScore, waitingDays, attentionReasons, submittedAt);
        }
    }
}

