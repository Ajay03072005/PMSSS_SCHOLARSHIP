package com.pmsss.officer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_assignments", indexes = {
        @Index(name = "idx_assign_app_id", columnList = "application_id"),
        @Index(name = "idx_assign_officer_id", columnList = "officer_id"),
        @Index(name = "idx_assign_status", columnList = "status"),
        @Index(name = "idx_assign_priority", columnList = "priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApplicationAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    @JsonIgnore
    private User officer;

    @Column(name = "officer_email", length = 100)
    private String officerEmail;

    @Column(name = "officer_name", length = 100)
    private String officerName;

    @Column(name = "assigned_by", length = 100)
    @Builder.Default
    private String assignedBy = "SYSTEM_AUTO_ASSIGN";

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "sla_due_at")
    private LocalDateTime slaDueAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 20, nullable = false)
    @Builder.Default
    private String priority = "NORMAL"; // HIGH, MEDIUM, NORMAL

    @Column(name = "review_category", length = 30, nullable = false)
    @Builder.Default
    private String reviewCategory = "QUICK_REVIEW"; // QUICK_REVIEW, NEEDS_ATTENTION

    @Column(length = 30, nullable = false)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, REASSIGNED, ESCALATED

    @Column(name = "triage_reason", columnDefinition = "TEXT")
    private String triageReason;

    @Transient
    private String applicantName;

    @Transient
    private String stream;

    @Transient
    private Integer aiScore;

    @Transient
    private Integer anomalyCount;

    @Transient
    private java.util.List<String> reasons;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public User getOfficer() { return officer; }
    public void setOfficer(User officer) { this.officer = officer; }

    public String getOfficerEmail() { return officerEmail; }
    public void setOfficerEmail(String officerEmail) { this.officerEmail = officerEmail; }

    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(LocalDateTime slaDueAt) { this.slaDueAt = slaDueAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getReviewCategory() { return reviewCategory; }
    public void setReviewCategory(String reviewCategory) { this.reviewCategory = reviewCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTriageReason() { return triageReason; }
    public void setTriageReason(String triageReason) { this.triageReason = triageReason; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public Integer getAiScore() { return aiScore; }
    public void setAiScore(Integer aiScore) { this.aiScore = aiScore; }

    public Integer getAnomalyCount() { return anomalyCount; }
    public void setAnomalyCount(Integer anomalyCount) { this.anomalyCount = anomalyCount; }

    public java.util.List<String> getReasons() { return reasons; }
    public void setReasons(java.util.List<String> reasons) { this.reasons = reasons; }

    public String getTriageCategory() {
        return reviewCategory;
    }

    public boolean isOverdue() {
        return slaDueAt != null && LocalDateTime.now().isAfter(slaDueAt) && "ACTIVE".equalsIgnoreCase(status);
    }

    public static ApplicationAssignmentBuilder builder() {
        return new ApplicationAssignmentBuilder();
    }

    public static class ApplicationAssignmentBuilder {
        private Long id;
        private String applicationId;
        private User officer;
        private String officerEmail;
        private String officerName;
        private String assignedBy = "SYSTEM_AUTO_ASSIGN";
        private LocalDateTime assignedAt;
        private LocalDateTime slaDueAt;
        private LocalDateTime completedAt;
        private String priority = "NORMAL";
        private String reviewCategory = "QUICK_REVIEW";
        private String status = "ACTIVE";
        private String triageReason;
        private String applicantName;
        private String stream;
        private Integer aiScore;
        private Integer anomalyCount;
        private java.util.List<String> reasons;

        public ApplicationAssignmentBuilder id(Long id) { this.id = id; return this; }
        public ApplicationAssignmentBuilder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public ApplicationAssignmentBuilder officer(User officer) { this.officer = officer; return this; }
        public ApplicationAssignmentBuilder officerEmail(String officerEmail) { this.officerEmail = officerEmail; return this; }
        public ApplicationAssignmentBuilder officerName(String officerName) { this.officerName = officerName; return this; }
        public ApplicationAssignmentBuilder assignedBy(String assignedBy) { this.assignedBy = assignedBy; return this; }
        public ApplicationAssignmentBuilder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public ApplicationAssignmentBuilder slaDueAt(LocalDateTime slaDueAt) { this.slaDueAt = slaDueAt; return this; }
        public ApplicationAssignmentBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public ApplicationAssignmentBuilder priority(String priority) { this.priority = priority; return this; }
        public ApplicationAssignmentBuilder reviewCategory(String reviewCategory) { this.reviewCategory = reviewCategory; return this; }
        public ApplicationAssignmentBuilder status(String status) { this.status = status; return this; }
        public ApplicationAssignmentBuilder triageReason(String triageReason) { this.triageReason = triageReason; return this; }
        public ApplicationAssignmentBuilder applicantName(String applicantName) { this.applicantName = applicantName; return this; }
        public ApplicationAssignmentBuilder stream(String stream) { this.stream = stream; return this; }
        public ApplicationAssignmentBuilder aiScore(Integer aiScore) { this.aiScore = aiScore; return this; }
        public ApplicationAssignmentBuilder anomalyCount(Integer anomalyCount) { this.anomalyCount = anomalyCount; return this; }
        public ApplicationAssignmentBuilder reasons(java.util.List<String> reasons) { this.reasons = reasons; return this; }

        public ApplicationAssignment build() {
            ApplicationAssignment aa = new ApplicationAssignment();
            aa.id = this.id;
            aa.applicationId = this.applicationId;
            aa.officer = this.officer;
            aa.officerEmail = this.officerEmail;
            aa.officerName = this.officerName;
            aa.assignedBy = this.assignedBy;
            aa.assignedAt = this.assignedAt;
            aa.slaDueAt = this.slaDueAt;
            aa.completedAt = this.completedAt;
            aa.priority = this.priority;
            aa.reviewCategory = this.reviewCategory;
            aa.status = this.status;
            aa.triageReason = this.triageReason;
            aa.applicantName = this.applicantName;
            aa.stream = this.stream;
            aa.aiScore = this.aiScore;
            aa.anomalyCount = this.anomalyCount;
            aa.reasons = this.reasons;
            return aa;
        }
    }
}

