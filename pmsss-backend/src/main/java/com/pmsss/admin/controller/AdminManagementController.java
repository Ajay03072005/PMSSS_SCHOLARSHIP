package com.pmsss.admin.controller;

import com.pmsss.common.enums.RoleType;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin User & System Management", description = "User management, officer assignments, role updates, and system configuration")
public class AdminManagementController {

    private final UserRepository userRepository;

    // In-memory system configuration settings
    private static final Map<String, Object> SYSTEM_SETTINGS = new ConcurrentHashMap<>(Map.of(
            "portalMaintenanceMode", false,
            "autoAssignmentEnabled", true,
            "ocrAutoApprovalThreshold", 85,
            "slaHoursForReview", 48,
            "maxAssignedPerOfficer", 50,
            "emailNotificationsEnabled", true,
            "smsNotificationsEnabled", true,
            "activeAcademicYear", "2025-2026",
            "maxScholarshipAmount", 30000
    ));

    @GetMapping("/users")
    @Operation(summary = "Get paginated users list")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) RoleType role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        if (role != null) {
            users = userRepository.findByRole(role, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", users));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable Long id,
            @RequestParam RoleType role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(role);
            User saved = userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Toggle user active status")
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(active);
            User saved = userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("User status updated", saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/officers")
    @Operation(summary = "Get list of officers for assignment")
    public ResponseEntity<ApiResponse<List<User>>> getOfficers() {
        List<User> officers = userRepository.findByRoleIn(List.of(RoleType.ROLE_SAG_OFFICER, RoleType.ROLE_FINANCE_OFFICER));
        return ResponseEntity.ok(ApiResponse.ok("Officers list retrieved", officers));
    }

    @GetMapping("/settings")
    @Operation(summary = "Get system settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok("System settings retrieved", new HashMap<>(SYSTEM_SETTINGS)));
    }

    @PutMapping("/settings")
    @Operation(summary = "Update system settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSettings(@RequestBody Map<String, Object> newSettings) {
        SYSTEM_SETTINGS.putAll(newSettings);
        return ResponseEntity.ok(ApiResponse.ok("System settings updated successfully", new HashMap<>(SYSTEM_SETTINGS)));
    }
}
