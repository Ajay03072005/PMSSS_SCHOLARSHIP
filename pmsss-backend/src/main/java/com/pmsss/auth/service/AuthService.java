package com.pmsss.auth.service;

import com.pmsss.auth.request.LoginRequest;
import com.pmsss.auth.request.RegisterRequest;
import com.pmsss.auth.response.AuthResponse;
import com.pmsss.auth.entity.RefreshToken;
import com.pmsss.auth.repository.RefreshTokenRepository;
import com.pmsss.auth.security.JwtTokenProvider;
import com.pmsss.common.enums.RoleType;
import com.pmsss.common.exception.BusinessException;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.student.entity.StudentProfile;
import com.pmsss.student.repository.StudentProfileRepository;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("An account already exists with these details", "USER_ALREADY_EXISTS");
        }

        if (request.getAadhar() != null && userRepository.existsByAadhar(request.getAadhar())) {
            throw new BusinessException("User already exists with Aadhar: " + request.getAadhar(), "AADHAR_ALREADY_EXISTS");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .aadhar(request.getAadhar())
                .dateOfBirth(request.getDateOfBirth())
                .role(RoleType.ROLE_STUDENT)
                .isVerified(false)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        studentProfileRepository.save(StudentProfile.builder().user(user).build());

        String token = tokenProvider.generateTokenForUser(user);
        String refreshToken = issueRefreshToken(user);

        return buildAuthResponse(user, token, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String principal = request.getPrincipal();
        User user = userRepository.findByEmail(principal)
                .orElseGet(() -> userRepository.findByAadhar(principal)
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessException("Your account is deactivated. Please contact support.", "ACCOUNT_DEACTIVATED");
        }
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            throw new BusinessException("Your account is locked. Please contact support.", "ACCOUNT_LOCKED");
        }

        String token = tokenProvider.generateTokenForUser(user);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        String refreshToken = issueRefreshToken(user);

        return buildAuthResponse(user, token, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthResponse.UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserDto(user);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (!refreshToken.isUsable(LocalDateTime.now())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
        String accessToken = tokenProvider.generateTokenForUser(refreshToken.getUser());
        return buildAuthResponse(refreshToken.getUser(), accessToken, issueRefreshToken(refreshToken.getUser()));
    }

    @Transactional
    public void revokeRefreshToken(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken)).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    public AuthResponse.UserDto mapToUserDto(User user) {
        return AuthResponse.UserDto.builder()
                .id(user.getId())
                .uniqueId(user.getUniqueId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .aadhar(maskAadhar(user.getAadhar()))
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole().name().replace("ROLE_", "").toLowerCase())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String issueRefreshToken(User user) {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokenRepository.save(new RefreshToken(
                user,
                hashToken(rawToken),
                LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000L)));
        return rawToken;
    }

    private String hashToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte value : digest) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() <= 4) return aadhar;
        return "********" + aadhar.substring(aadhar.length() - 4);
    }

    private AuthResponse buildAuthResponse(User user, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserDto(user))
                .build();
    }
}
