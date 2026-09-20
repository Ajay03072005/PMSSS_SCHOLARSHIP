package com.pmsss.application.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationSubmitRequest {

    private Boolean submit;
    private Boolean declaration;

    private PersonalInfo personalInfo;
    private Object academicInfo; // Flexible structure (Map or Object)
    private FamilyInfo familyInfo;
    private BankDetails bankDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalInfo {
        private String firstName;
        private String middleName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String gender;
        private String category;
        private String aadhar;
        private String mobile;
        private String email;
        private String address;
        private String district;
        private String state;
        private String pincode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FamilyInfo {
        private ParentInfo father;
        private ParentInfo mother;
        private BigDecimal annualIncome;
        private String incomeSource;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParentInfo {
        private String name;
        private String occupation;
        private String mobile;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BankDetails {
        private String accountHolderName;
        private String accountNumber;
        private String ifscCode;
        private String bankName;
        private String branchName;
    }
}
