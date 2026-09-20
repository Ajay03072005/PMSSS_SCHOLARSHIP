package com.pmsss.ai.entity;

import com.pmsss.common.enums.AnomalySeverity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_alerts", indexes = {
        @Index(name = "idx_anom_app_id", columnList = "application_id"),
        @Index(name = "idx_anom_severity", columnList = "severity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnomalyAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @Column(name = "student_name", length = 100)
    private String studentName;

    @Column(name = "anomaly_type", nullable = false, length = 100)
    private String anomalyType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnomalySeverity severity;

    @Column(name = "is_resolved", nullable = false)
    @Builder.Default
    private Boolean isResolved = false;

    @CreationTimestamp
    @Column(name = "detected_at", updatable = false)
    private LocalDateTime detectedAt;
}
