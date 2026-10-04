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

    public Boolean getSubmit() { return submit; }
    public void setSubmit(Boolean submit) { this.submit = submit; }

    public Boolean getDeclaration() { return declaration; }
    public void setDeclaration(Boolean declaration) { this.declaration = declaration; }

    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

    public Object getAcademicInfo() { return academicInfo; }
    public void setAcademicInfo(Object academicInfo) { this.academicInfo = academicInfo; }

    public FamilyInfo getFamilyInfo() { return familyInfo; }
    public void setFamilyInfo(FamilyInfo familyInfo) { this.familyInfo = familyInfo; }

    public BankDetails getBankDetails() { return bankDetails; }
    public void setBankDetails(BankDetails bankDetails) { this.bankDetails = bankDetails; }

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
    }

    public static class FamilyInfo {
        private ParentInfo father;
        private ParentInfo mother;
        private BigDecimal annualIncome;
        private String incomeSource;

        public ParentInfo getFather() { return father; }
        public void setFather(ParentInfo father) { this.father = father; }

        public ParentInfo getMother() { return mother; }
        public void setMother(ParentInfo mother) { this.mother = mother; }

        public BigDecimal getAnnualIncome() { return annualIncome; }
        public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }

        public String getIncomeSource() { return incomeSource; }
        public void setIncomeSource(String incomeSource) { this.incomeSource = incomeSource; }
    }

    public static class ParentInfo {
        private String name;
        private String occupation;
        private String mobile;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
    }

    public static class BankDetails {
        private String accountHolderName;
        private String accountNumber;
        private String ifscCode;
        private String bankName;
        private String branchName;

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
    }
}

