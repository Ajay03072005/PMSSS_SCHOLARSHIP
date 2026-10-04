package com.pmsss.officer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "officer_workloads", indexes = {
        @Index(name = "idx_workload_officer", columnList = "officer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OfficerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false, unique = true)
    @JsonIgnore
    private User officer;

    @Column(name = "officer_name", length = 100)
    private String officerName;

    @Column(name = "officer_email", length = 100)
    private String officerEmail;

    @Column(name = "current_workload", nullable = false)
    @Builder.Default
    private Integer currentWorkload = 0;

    @Column(name = "max_capacity", nullable = false)
    @Builder.Default
    private Integer maxCapacity = 100;

    @Column(name = "overdue_count", nullable = false)
    @Builder.Default
    private Integer overdueCount = 0;

    @Column(name = "completed_today", nullable = false)
    @Builder.Default
    private Integer completedToday = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "assigned_district", length = 100)
    private String assignedDistrict;

    @Column(name = "last_assigned_at")
    private LocalDateTime lastAssignedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOfficer() { return officer; }
    public void setOfficer(User officer) { this.officer = officer; }

    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }

    public String getOfficerEmail() { return officerEmail; }
    public void setOfficerEmail(String officerEmail) { this.officerEmail = officerEmail; }

    public Integer getCurrentWorkload() { return currentWorkload != null ? currentWorkload : 0; }
    public void setCurrentWorkload(Integer currentWorkload) { this.currentWorkload = currentWorkload; }

    public Integer getMaxCapacity() { return maxCapacity != null ? maxCapacity : 100; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getOverdueCount() { return overdueCount != null ? overdueCount : 0; }
    public void setOverdueCount(Integer overdueCount) { this.overdueCount = overdueCount; }

    public Integer getCompletedToday() { return completedToday != null ? completedToday : 0; }
    public void setCompletedToday(Integer completedToday) { this.completedToday = completedToday; }

    public Boolean getIsActive() { return isActive != null ? isActive : true; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getAssignedDistrict() { return assignedDistrict; }
    public void setAssignedDistrict(String assignedDistrict) { this.assignedDistrict = assignedDistrict; }

    public LocalDateTime getLastAssignedAt() { return lastAssignedAt; }
    public void setLastAssignedAt(LocalDateTime lastAssignedAt) { this.lastAssignedAt = lastAssignedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public double getUtilizationPercentage() {
        if (maxCapacity == null || maxCapacity == 0) return 0.0;
        return Math.round(((double) currentWorkload / maxCapacity) * 1000.0) / 10.0;
    }

    public static OfficerWorkloadBuilder builder() {
        return new OfficerWorkloadBuilder();
    }

    public static class OfficerWorkloadBuilder {
        private Long id;
        private User officer;
        private String officerName;
        private String officerEmail;
        private Integer currentWorkload = 0;
        private Integer maxCapacity = 100;
        private Integer overdueCount = 0;
        private Integer completedToday = 0;
        private Boolean isActive = true;
        private String assignedDistrict;
        private LocalDateTime lastAssignedAt;
        private LocalDateTime updatedAt;

        public OfficerWorkloadBuilder id(Long id) { this.id = id; return this; }
        public OfficerWorkloadBuilder officer(User officer) { this.officer = officer; return this; }
        public OfficerWorkloadBuilder officerName(String officerName) { this.officerName = officerName; return this; }
        public OfficerWorkloadBuilder officerEmail(String officerEmail) { this.officerEmail = officerEmail; return this; }
        public OfficerWorkloadBuilder currentWorkload(Integer currentWorkload) { this.currentWorkload = currentWorkload; return this; }
        public OfficerWorkloadBuilder maxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; return this; }
        public OfficerWorkloadBuilder overdueCount(Integer overdueCount) { this.overdueCount = overdueCount; return this; }
        public OfficerWorkloadBuilder completedToday(Integer completedToday) { this.completedToday = completedToday; return this; }
        public OfficerWorkloadBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public OfficerWorkloadBuilder assignedDistrict(String assignedDistrict) { this.assignedDistrict = assignedDistrict; return this; }
        public OfficerWorkloadBuilder lastAssignedAt(LocalDateTime lastAssignedAt) { this.lastAssignedAt = lastAssignedAt; return this; }
        public OfficerWorkloadBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public OfficerWorkload build() {
            OfficerWorkload ow = new OfficerWorkload();
            ow.id = this.id;
            ow.officer = this.officer;
            ow.officerName = this.officerName;
            ow.officerEmail = this.officerEmail;
            ow.currentWorkload = this.currentWorkload;
            ow.maxCapacity = this.maxCapacity;
            ow.overdueCount = this.overdueCount;
            ow.completedToday = this.completedToday;
            ow.isActive = this.isActive;
            ow.assignedDistrict = this.assignedDistrict;
            ow.lastAssignedAt = this.lastAssignedAt;
            ow.updatedAt = this.updatedAt;
            return ow;
        }
    }
}

