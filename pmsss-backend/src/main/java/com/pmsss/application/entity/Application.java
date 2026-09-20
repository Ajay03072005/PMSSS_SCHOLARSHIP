package com.pmsss.application.entity;

import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_app_application_id", columnList = "application_id"),
        @Index(name = "idx_app_user_id", columnList = "user_id"),
        @Index(name = "idx_app_status", columnList = "status"),
        @Index(name = "idx_app_aadhar", columnList = "aadhar")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "application_id", nullable = false, unique = true, length = 50)
    private String applicationId;

    // Personal Information
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 50)
    private String category; // general, obc, sc, st

    @Column(nullable = false, length = 12)
    private String aadhar;

    @Column(nullable = false, length = 15)
    private String mobile;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String district;

    @Column(length = 50)
    private String state; // jk, ladakh

    @Column(length = 10)
    private String pincode;

    // Academic Information (JSON formatted or text)
    @Column(name = "academic_info", columnDefinition = "TEXT")
    private String academicInfo;

    // Family Details
    @Column(name = "father_name", length = 200)
    private String fatherName;

    @Column(name = "father_occupation", length = 200)
    private String fatherOccupation;

    @Column(name = "father_mobile", length = 15)
    private String fatherMobile;

    @Column(name = "mother_name", length = 200)
    private String motherName;

    @Column(name = "mother_occupation", length = 200)
    private String motherOccupation;

    @Column(name = "mother_mobile", length = 15)
    private String motherMobile;

    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @Column(name = "income_source", length = 200)
    private String incomeSource;

    // Bank Details
    @Column(name = "account_holder_name", length = 200)
    private String accountHolderName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "bank_name", length = 200)
    private String bankName;

    @Column(name = "branch_name", length = 200)
    private String branchName;

    // Document File Paths (for legacy compatibility and direct access)
    private String photo;

    @Column(name = "aadhar_doc")
    private String aadharDoc;

    private String domicile;

    @Column(name = "income_cert")
    private String incomeCert;

    @Column(name = "tenth_marksheet")
    private String tenthMarksheet;

    @Column(name = "twelfth_marksheet")
    private String twelfthMarksheet;

    @Column(name = "admission_letter")
    private String admissionLetter;

    @Column(name = "bank_passbook")
    private String bankPassbook;

    // Status & Workflow
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Builder.Default
    private Boolean declaration = false;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "review_remarks", columnDefinition = "TEXT")
    private String reviewRemarks;

    // AI Prioritization & Anomaly flags
    @Column(name = "ai_priority_score")
    @Builder.Default
    private Integer aiPriorityScore = 50; // 0 - 100

    @Column(name = "has_anomalies")
    @Builder.Default
    private Boolean hasAnomalies = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getUniqueId() {
        return applicationId;
    }
}
