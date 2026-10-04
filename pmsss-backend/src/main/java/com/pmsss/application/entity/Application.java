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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAcademicInfo() { return academicInfo; }
    public void setAcademicInfo(String academicInfo) { this.academicInfo = academicInfo; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getFatherOccupation() { return fatherOccupation; }
    public void setFatherOccupation(String fatherOccupation) { this.fatherOccupation = fatherOccupation; }

    public String getFatherMobile() { return fatherMobile; }
    public void setFatherMobile(String fatherMobile) { this.fatherMobile = fatherMobile; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getMotherOccupation() { return motherOccupation; }
    public void setMotherOccupation(String motherOccupation) { this.motherOccupation = motherOccupation; }

    public String getMotherMobile() { return motherMobile; }
    public void setMotherMobile(String motherMobile) { this.motherMobile = motherMobile; }

    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }

    public String getIncomeSource() { return incomeSource; }
    public void setIncomeSource(String incomeSource) { this.incomeSource = incomeSource; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getAadharDoc() { return aadharDoc; }
    public void setAadharDoc(String aadharDoc) { this.aadharDoc = aadharDoc; }

    public String getDomicile() { return domicile; }
    public void setDomicile(String domicile) { this.domicile = domicile; }

    public String getIncomeCert() { return incomeCert; }
    public void setIncomeCert(String incomeCert) { this.incomeCert = incomeCert; }

    public String getTenthMarksheet() { return tenthMarksheet; }
    public void setTenthMarksheet(String tenthMarksheet) { this.tenthMarksheet = tenthMarksheet; }

    public String getTwelfthMarksheet() { return twelfthMarksheet; }
    public void setTwelfthMarksheet(String twelfthMarksheet) { this.twelfthMarksheet = twelfthMarksheet; }

    public String getAdmissionLetter() { return admissionLetter; }
    public void setAdmissionLetter(String admissionLetter) { this.admissionLetter = admissionLetter; }

    public String getBankPassbook() { return bankPassbook; }
    public void setBankPassbook(String bankPassbook) { this.bankPassbook = bankPassbook; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public Boolean getDeclaration() { return declaration; }
    public void setDeclaration(Boolean declaration) { this.declaration = declaration; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewRemarks() { return reviewRemarks; }
    public void setReviewRemarks(String reviewRemarks) { this.reviewRemarks = reviewRemarks; }

    public Integer getAiPriorityScore() { return aiPriorityScore; }
    public void setAiPriorityScore(Integer aiPriorityScore) { this.aiPriorityScore = aiPriorityScore; }

    public Boolean getHasAnomalies() { return hasAnomalies; }
    public void setHasAnomalies(Boolean hasAnomalies) { this.hasAnomalies = hasAnomalies; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

