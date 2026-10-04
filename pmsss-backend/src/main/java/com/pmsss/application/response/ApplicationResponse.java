package com.pmsss.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pmsss.common.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {

    private Long id;
    private Long userId;
    private String applicationId;
    private String applicantName;
    private String email;
    private String mobile;
    private String aadhar;
    private LocalDate dateOfBirth;
    private String gender;
    private String category;
    private String district;
    private String state;
    private String pincode;
    private String address;

    private String academicInfo;
    private String fatherName;
    private String motherName;
    private BigDecimal annualIncome;
    private String incomeSource;

    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String branchName;

    private String photo;
    private String aadharDoc;
    private String domicile;
    private String incomeCert;
    private String tenthMarksheet;
    private String twelfthMarksheet;
    private String admissionLetter;
    private String bankPassbook;

    private ApplicationStatus status;
    private Boolean declaration;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewRemarks;

    private Integer aiPriorityScore;
    private Boolean hasAnomalies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAcademicInfo() { return academicInfo; }
    public void setAcademicInfo(String academicInfo) { this.academicInfo = academicInfo; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

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

    public static ApplicationResponseBuilder builder() {
        return new ApplicationResponseBuilder();
    }

    public static class ApplicationResponseBuilder {
        private Long id;
        private Long userId;
        private String applicationId;
        private String applicantName;
        private String email;
        private String mobile;
        private String aadhar;
        private LocalDate dateOfBirth;
        private String gender;
        private String category;
        private String district;
        private String state;
        private String pincode;
        private String address;
        private String academicInfo;
        private String fatherName;
        private String motherName;
        private BigDecimal annualIncome;
        private String incomeSource;
        private String accountHolderName;
        private String accountNumber;
        private String ifscCode;
        private String bankName;
        private String branchName;
        private String photo;
        private String aadharDoc;
        private String domicile;
        private String incomeCert;
        private String tenthMarksheet;
        private String twelfthMarksheet;
        private String admissionLetter;
        private String bankPassbook;
        private ApplicationStatus status;
        private Boolean declaration;
        private LocalDateTime submittedAt;
        private LocalDateTime reviewedAt;
        private String reviewRemarks;
        private Integer aiPriorityScore;
        private Boolean hasAnomalies;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ApplicationResponseBuilder id(Long id) { this.id = id; return this; }
        public ApplicationResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public ApplicationResponseBuilder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public ApplicationResponseBuilder applicantName(String applicantName) { this.applicantName = applicantName; return this; }
        public ApplicationResponseBuilder email(String email) { this.email = email; return this; }
        public ApplicationResponseBuilder mobile(String mobile) { this.mobile = mobile; return this; }
        public ApplicationResponseBuilder aadhar(String aadhar) { this.aadhar = aadhar; return this; }
        public ApplicationResponseBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public ApplicationResponseBuilder gender(String gender) { this.gender = gender; return this; }
        public ApplicationResponseBuilder category(String category) { this.category = category; return this; }
        public ApplicationResponseBuilder district(String district) { this.district = district; return this; }
        public ApplicationResponseBuilder state(String state) { this.state = state; return this; }
        public ApplicationResponseBuilder pincode(String pincode) { this.pincode = pincode; return this; }
        public ApplicationResponseBuilder address(String address) { this.address = address; return this; }
        public ApplicationResponseBuilder academicInfo(String academicInfo) { this.academicInfo = academicInfo; return this; }
        public ApplicationResponseBuilder fatherName(String fatherName) { this.fatherName = fatherName; return this; }
        public ApplicationResponseBuilder motherName(String motherName) { this.motherName = motherName; return this; }
        public ApplicationResponseBuilder annualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; return this; }
        public ApplicationResponseBuilder incomeSource(String incomeSource) { this.incomeSource = incomeSource; return this; }
        public ApplicationResponseBuilder accountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; return this; }
        public ApplicationResponseBuilder accountNumber(String accountNumber) { this.accountNumber = accountNumber; return this; }
        public ApplicationResponseBuilder ifscCode(String ifscCode) { this.ifscCode = ifscCode; return this; }
        public ApplicationResponseBuilder bankName(String bankName) { this.bankName = bankName; return this; }
        public ApplicationResponseBuilder branchName(String branchName) { this.branchName = branchName; return this; }
        public ApplicationResponseBuilder photo(String photo) { this.photo = photo; return this; }
        public ApplicationResponseBuilder aadharDoc(String aadharDoc) { this.aadharDoc = aadharDoc; return this; }
        public ApplicationResponseBuilder domicile(String domicile) { this.domicile = domicile; return this; }
        public ApplicationResponseBuilder incomeCert(String incomeCert) { this.incomeCert = incomeCert; return this; }
        public ApplicationResponseBuilder tenthMarksheet(String tenthMarksheet) { this.tenthMarksheet = tenthMarksheet; return this; }
        public ApplicationResponseBuilder twelfthMarksheet(String twelfthMarksheet) { this.twelfthMarksheet = twelfthMarksheet; return this; }
        public ApplicationResponseBuilder admissionLetter(String admissionLetter) { this.admissionLetter = admissionLetter; return this; }
        public ApplicationResponseBuilder bankPassbook(String bankPassbook) { this.bankPassbook = bankPassbook; return this; }
        public ApplicationResponseBuilder status(ApplicationStatus status) { this.status = status; return this; }
        public ApplicationResponseBuilder declaration(Boolean declaration) { this.declaration = declaration; return this; }
        public ApplicationResponseBuilder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public ApplicationResponseBuilder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }
        public ApplicationResponseBuilder reviewRemarks(String reviewRemarks) { this.reviewRemarks = reviewRemarks; return this; }
        public ApplicationResponseBuilder aiPriorityScore(Integer aiPriorityScore) { this.aiPriorityScore = aiPriorityScore; return this; }
        public ApplicationResponseBuilder hasAnomalies(Boolean hasAnomalies) { this.hasAnomalies = hasAnomalies; return this; }
        public ApplicationResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ApplicationResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ApplicationResponse build() {
            ApplicationResponse ar = new ApplicationResponse();
            ar.id = this.id;
            ar.userId = this.userId;
            ar.applicationId = this.applicationId;
            ar.applicantName = this.applicantName;
            ar.email = this.email;
            ar.mobile = this.mobile;
            ar.aadhar = this.aadhar;
            ar.dateOfBirth = this.dateOfBirth;
            ar.gender = this.gender;
            ar.category = this.category;
            ar.district = this.district;
            ar.state = this.state;
            ar.pincode = this.pincode;
            ar.address = this.address;
            ar.academicInfo = this.academicInfo;
            ar.fatherName = this.fatherName;
            ar.motherName = this.motherName;
            ar.annualIncome = this.annualIncome;
            ar.incomeSource = this.incomeSource;
            ar.accountHolderName = this.accountHolderName;
            ar.accountNumber = this.accountNumber;
            ar.ifscCode = this.ifscCode;
            ar.bankName = this.bankName;
            ar.branchName = this.branchName;
            ar.photo = this.photo;
            ar.aadharDoc = this.aadharDoc;
            ar.domicile = this.domicile;
            ar.incomeCert = this.incomeCert;
            ar.tenthMarksheet = this.tenthMarksheet;
            ar.twelfthMarksheet = this.twelfthMarksheet;
            ar.admissionLetter = this.admissionLetter;
            ar.bankPassbook = this.bankPassbook;
            ar.status = this.status;
            ar.declaration = this.declaration;
            ar.submittedAt = this.submittedAt;
            ar.reviewedAt = this.reviewedAt;
            ar.reviewRemarks = this.reviewRemarks;
            ar.aiPriorityScore = this.aiPriorityScore;
            ar.hasAnomalies = this.hasAnomalies;
            ar.createdAt = this.createdAt;
            ar.updatedAt = this.updatedAt;
            return ar;
        }
    }
}

