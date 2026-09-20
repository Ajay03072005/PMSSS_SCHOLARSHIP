package com.pmsss.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmsss.auth.request.LoginRequest;
import com.pmsss.auth.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAdminLoginSuccess() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .email("admin@pmsss.gov.in")
                .password("Admin@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("admin@pmsss.gov.in"));
    }

    @Test
    void testStudentRegistrationSuccess() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("Test")
                .lastName("Student")
                .fullName("Test Student")
                .email("test.student." + System.currentTimeMillis() + "@pmsss.gov.in")
                .mobile("9876543210")
                .phone("9876543210")
                .password("Password@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.uniqueId").isNotEmpty());
    }

    @Test
    void testPublicAiEligibilityEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/ai/eligibility/check")
                        .param("category", "General")
                        .param("income", "350000")
                        .param("state", "J&K")
                        .param("percentage12th", "75.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("POTENTIALLY_ELIGIBLE"));
    }
}
