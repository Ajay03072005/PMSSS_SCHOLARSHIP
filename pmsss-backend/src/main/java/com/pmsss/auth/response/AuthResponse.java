package com.pmsss.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserDto user;

    public AuthResponse() {}

    public AuthResponse(String token, String refreshToken, String tokenType, UserDto user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String refreshToken;
        private String tokenType = "Bearer";
        private UserDto user;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public AuthResponseBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseBuilder user(UserDto user) { this.user = user; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, refreshToken, tokenType, user);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserDto {
        private Long id;
        private String uniqueId;
        private String firstName;
        private String middleName;
        private String lastName;
        private String fullName;
        private String email;
        private String mobile;
        private String aadhar;
        private LocalDate dateOfBirth;
        private String role;
        private Boolean isVerified;
        private Boolean isActive;
        private LocalDateTime createdAt;

        public UserDto() {}

        public UserDto(Long id, String uniqueId, String firstName, String middleName, String lastName, String fullName, String email, String mobile, String aadhar, LocalDate dateOfBirth, String role, Boolean isVerified, Boolean isActive, LocalDateTime createdAt) {
            this.id = id;
            this.uniqueId = uniqueId;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.fullName = fullName;
            this.email = email;
            this.mobile = mobile;
            this.aadhar = aadhar;
            this.dateOfBirth = dateOfBirth;
            this.role = role;
            this.isVerified = isVerified;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUniqueId() { return uniqueId; }
        public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getAadhar() { return aadhar; }
        public void setAadhar(String aadhar) { this.aadhar = aadhar; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public static UserDtoBuilder builder() {
            return new UserDtoBuilder();
        }

        public static class UserDtoBuilder {
            private Long id;
            private String uniqueId;
            private String firstName;
            private String middleName;
            private String lastName;
            private String fullName;
            private String email;
            private String mobile;
            private String aadhar;
            private LocalDate dateOfBirth;
            private String role;
            private Boolean isVerified;
            private Boolean isActive;
            private LocalDateTime createdAt;

            public UserDtoBuilder id(Long id) { this.id = id; return this; }
            public UserDtoBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
            public UserDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
            public UserDtoBuilder middleName(String middleName) { this.middleName = middleName; return this; }
            public UserDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
            public UserDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
            public UserDtoBuilder email(String email) { this.email = email; return this; }
            public UserDtoBuilder mobile(String mobile) { this.mobile = mobile; return this; }
            public UserDtoBuilder aadhar(String aadhar) { this.aadhar = aadhar; return this; }
            public UserDtoBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
            public UserDtoBuilder role(String role) { this.role = role; return this; }
            public UserDtoBuilder isVerified(Boolean isVerified) { this.isVerified = isVerified; return this; }
            public UserDtoBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
            public UserDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public UserDto build() {
                return new UserDto(id, uniqueId, firstName, middleName, lastName, fullName, email, mobile, aadhar, dateOfBirth, role, isVerified, isActive, createdAt);
            }
        }
    }
}

