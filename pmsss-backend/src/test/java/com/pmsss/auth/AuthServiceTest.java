package com.pmsss.auth;

import com.pmsss.auth.request.LoginRequest;
import com.pmsss.auth.request.RegisterRequest;
import com.pmsss.auth.response.AuthResponse;
import com.pmsss.auth.repository.RefreshTokenRepository;
import com.pmsss.auth.security.JwtTokenProvider;
import com.pmsss.auth.service.AuthService;
import com.pmsss.common.enums.RoleType;
import com.pmsss.common.exception.BusinessException;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import com.pmsss.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .firstName("Ajay")
                .lastName("Kumar")
                .email("ajay@pmsss.gov.in")
                .password("encoded_pass")
                .mobile("9876543210")
                .aadhar("123456789012")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .role(RoleType.ROLE_STUDENT)
                .isActive(true)
                .isVerified(false)
                .build();
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("Ajay")
                .lastName("Kumar")
                .email("ajay@pmsss.gov.in")
                .password("Password@123")
                .mobile("9876543210")
                .aadhar("123456789012")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .build();

        when(userRepository.existsByEmail("ajay@pmsss.gov.in")).thenReturn(false);
        when(userRepository.existsByAadhar("123456789012")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateTokenForUser(any(User.class))).thenReturn("jwt.token.mock");

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("jwt.token.mock", response.getToken());
        assertEquals("ajay@pmsss.gov.in", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequest req = RegisterRequest.builder()
                .email("ajay@pmsss.gov.in")
                .build();

        when(userRepository.existsByEmail("ajay@pmsss.gov.in")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest req = LoginRequest.builder()
                .email("ajay@pmsss.gov.in")
                .password("Password@123")
                .build();

        when(userRepository.findByEmail("ajay@pmsss.gov.in")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password@123", "encoded_pass")).thenReturn(true);
        when(tokenProvider.generateTokenForUser(sampleUser)).thenReturn("jwt.token.mock");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("jwt.token.mock", response.getToken());
        assertEquals("Ajay Kumar", response.getUser().getFullName());
    }
}
