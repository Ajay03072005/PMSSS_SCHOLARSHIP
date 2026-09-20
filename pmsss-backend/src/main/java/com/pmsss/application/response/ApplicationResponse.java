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
}
