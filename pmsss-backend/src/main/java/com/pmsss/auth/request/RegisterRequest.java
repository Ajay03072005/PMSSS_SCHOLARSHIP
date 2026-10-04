package com.pmsss.auth.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$", message = "Password must contain upper, lower, numeric, and special characters")
    private String password;

    @JsonAlias("mobileNumber")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain 10 digits")
    private String mobile;
    private String phone;

    private String aadhar;

    private LocalDate dateOfBirth;

    // Optional role for creating admins or officers (defaults to student)
    private String role;

    @JsonSetter("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
        populateFirstAndLastNameFromFullName();
    }

    @JsonSetter("phone")
    public void setPhone(String phone) {
        this.phone = phone;
        if ((this.mobile == null || this.mobile.isBlank()) && phone != null) {
            this.mobile = phone;
        }
    }

    @JsonSetter("mobile")
    public void setMobile(String mobile) {
        this.mobile = mobile;
        if ((this.phone == null || this.phone.isBlank()) && mobile != null) {
            this.phone = mobile;
        }
    }

    public String getFirstName() {
        populateFirstAndLastNameFromFullName();
        if (firstName == null || firstName.isBlank()) {
            return "User";
        }
        return firstName;
    }

    public String getLastName() {
        populateFirstAndLastNameFromFullName();
        if (lastName == null || lastName.isBlank()) {
            return "Student";
        }
        return lastName;
    }

    public String getMobile() {
        if ((mobile == null || mobile.isBlank()) && phone != null && !phone.isBlank()) {
            mobile = phone;
        }
        return mobile;
    }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    private void populateFirstAndLastNameFromFullName() {
        if ((firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank())
                && fullName != null && !fullName.isBlank()) {
            String[] parts = fullName.trim().split("\\s+", 2);
            if (firstName == null || firstName.isBlank()) {
                firstName = parts[0];
            }
            if (lastName == null || lastName.isBlank()) {
                lastName = parts.length > 1 ? parts[1] : parts[0];
            }
        }
    }
}


