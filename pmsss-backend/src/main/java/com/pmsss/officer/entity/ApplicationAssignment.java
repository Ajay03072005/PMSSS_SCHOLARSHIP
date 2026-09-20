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

    public String getTriageCategory() {
        return reviewCategory;
    }

    public boolean isOverdue() {
        return slaDueAt != null && LocalDateTime.now().isAfter(slaDueAt) && "ACTIVE".equalsIgnoreCase(status);
    }
}
