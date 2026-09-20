package com.pmsss.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tokenType;
    private UserDto user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    }
}
