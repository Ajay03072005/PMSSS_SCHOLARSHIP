package com.pmsss.student.controller;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.student.entity.StudentProfile;
import com.pmsss.student.service.StudentService;
import com.pmsss.verification.entity.CorrectionRequest;
import com.pmsss.verification.repository.CorrectionRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student profile and dashboard endpoints")
public class StudentController {

    private final StudentService studentService;
    private final ApplicationRepository applicationRepository;
    private final CorrectionRequestRepository correctionRequestRepository;

    @GetMapping("/profile")
    @Operation(summary = "Get student profile details")
    public ResponseEntity<ApiResponse<StudentProfile>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudentProfile profile = studentService.getProfileByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update student profile")
    public ResponseEntity<ApiResponse<StudentProfile>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody StudentProfile updated) {
        StudentProfile profile = studentService.updateProfile(userPrincipal.getId(), updated);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", profile));
    }

    @GetMapping("/corrections")
    @Operation(summary = "Get correction requests for the logged-in student's applications")
    public ResponseEntity<ApiResponse<List<CorrectionRequest>>> getMyCorrections(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Application> apps = applicationRepository.findByUserIdOrderByCreatedAtDesc(userPrincipal.getId());
        if (apps.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok("No corrections found", Collections.emptyList()));
        }
        // Get corrections for all of the student's applications
        List<CorrectionRequest> allCorrections = apps.stream()
                .flatMap(app -> correctionRequestRepository.findByApplicationId(app.getApplicationId()).stream())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Correction requests retrieved", allCorrections));
    }
}
