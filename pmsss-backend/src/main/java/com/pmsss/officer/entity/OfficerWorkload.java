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

    public double getUtilizationPercentage() {
        if (maxCapacity == null || maxCapacity == 0) return 0.0;
        return Math.round(((double) currentWorkload / maxCapacity) * 1000.0) / 10.0;
    }
}
