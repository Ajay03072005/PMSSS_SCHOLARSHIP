package com.pmsss.auth.request;

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

    @Size(max = 100)
    private String firstName;

    private String middleName;

    @Size(max = 100)
    private String lastName;

    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

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

