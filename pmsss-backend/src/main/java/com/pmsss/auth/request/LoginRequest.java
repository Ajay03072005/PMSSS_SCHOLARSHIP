package com.pmsss.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String email;

    // Optional username field for admin logins
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public String getPrincipal() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        return username != null ? username.trim() : "";
    }
}
