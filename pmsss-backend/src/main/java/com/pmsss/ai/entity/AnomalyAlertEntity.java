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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public AnomalySeverity getSeverity() { return severity; }
    public void setSeverity(AnomalySeverity severity) { this.severity = severity; }

    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public static AnomalyAlertEntityBuilder builder() {
        return new AnomalyAlertEntityBuilder();
    }

    public static class AnomalyAlertEntityBuilder {
        private Long id;
        private String applicationId;
        private String studentName;
        private String anomalyType;
        private String reason;
        private AnomalySeverity severity;
        private Boolean isResolved = false;
        private LocalDateTime detectedAt;

        public AnomalyAlertEntityBuilder id(Long id) { this.id = id; return this; }
        public AnomalyAlertEntityBuilder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public AnomalyAlertEntityBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public AnomalyAlertEntityBuilder anomalyType(String anomalyType) { this.anomalyType = anomalyType; return this; }
        public AnomalyAlertEntityBuilder reason(String reason) { this.reason = reason; return this; }
        public AnomalyAlertEntityBuilder severity(AnomalySeverity severity) { this.severity = severity; return this; }
        public AnomalyAlertEntityBuilder isResolved(Boolean isResolved) { this.isResolved = isResolved; return this; }
        public AnomalyAlertEntityBuilder detectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; return this; }

        public AnomalyAlertEntity build() {
            AnomalyAlertEntity aae = new AnomalyAlertEntity();
            aae.id = this.id;
            aae.applicationId = this.applicationId;
            aae.studentName = this.studentName;
            aae.anomalyType = this.anomalyType;
            aae.reason = this.reason;
            aae.severity = this.severity;
            aae.isResolved = this.isResolved;
            aae.detectedAt = this.detectedAt;
            return aae;
        }
    }
}

