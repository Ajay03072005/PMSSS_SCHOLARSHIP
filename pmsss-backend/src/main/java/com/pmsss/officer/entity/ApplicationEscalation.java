package com.pmsss.officer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_escalations", indexes = {
        @Index(name = "idx_esc_app_id", columnList = "application_id"),
        @Index(name = "idx_esc_officer_id", columnList = "officer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApplicationEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @Column(name = "officer_id", nullable = false)
    private Long officerId;

    @Column(name = "officer_name", length = 100)
    private String officerName;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "sla_hours_exceeded")
    private Integer slaHoursExceeded;

    @Column(length = 50)
    @Builder.Default
    private String status = "OPEN"; // OPEN, UNDER_SUPERVISOR_REVIEW, RESOLVED

    @Column(name = "action_taken", columnDefinition = "TEXT")
    private String actionTaken;

    @CreationTimestamp
    @Column(name = "escalated_at", updatable = false)
    private LocalDateTime escalatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
