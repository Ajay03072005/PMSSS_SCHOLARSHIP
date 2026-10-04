package com.pmsss.application.entity;

import com.pmsss.common.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_history", indexes = {
        @Index(name = "idx_history_app_id", columnList = "application_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status;

    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "updated_by")
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ApplicationStatusHistoryBuilder builder() {
        return new ApplicationStatusHistoryBuilder();
    }

    public static class ApplicationStatusHistoryBuilder {
        private Long id;
        private Application application;
        private ApplicationStatus status;
        private String oldStatus;
        private String remarks;
        private Long updatedBy;
        private LocalDateTime createdAt;

        public ApplicationStatusHistoryBuilder id(Long id) { this.id = id; return this; }
        public ApplicationStatusHistoryBuilder application(Application application) { this.application = application; return this; }
        public ApplicationStatusHistoryBuilder status(ApplicationStatus status) { this.status = status; return this; }
        public ApplicationStatusHistoryBuilder oldStatus(String oldStatus) { this.oldStatus = oldStatus; return this; }
        public ApplicationStatusHistoryBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public ApplicationStatusHistoryBuilder updatedBy(Long updatedBy) { this.updatedBy = updatedBy; return this; }
        public ApplicationStatusHistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ApplicationStatusHistory build() {
            ApplicationStatusHistory ash = new ApplicationStatusHistory();
            ash.id = this.id;
            ash.application = this.application;
            ash.status = this.status;
            ash.oldStatus = this.oldStatus;
            ash.remarks = this.remarks;
            ash.updatedBy = this.updatedBy;
            ash.createdAt = this.createdAt;
            return ash;
        }
    }
}

