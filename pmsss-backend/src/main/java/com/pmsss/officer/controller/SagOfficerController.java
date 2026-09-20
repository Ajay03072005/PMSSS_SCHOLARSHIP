package com.pmsss.officer.controller;

import com.pmsss.application.entity.Application;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.officer.dto.AiReviewSummaryDto;
import com.pmsss.officer.dto.OfficerQueueResponse;
import com.pmsss.officer.entity.ApplicationEscalation;
import com.pmsss.officer.entity.OfficerWorkload;
import com.pmsss.officer.service.OfficerQueueService;
import com.pmsss.verification.entity.CorrectionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sag")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
@Tag(name = "SAG Officer Portal", description = "Endpoints specifically for SAG Officer portal operations")
public class SagOfficerController {

    private final OfficerQueueService officerQueueService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get SAG Officer dashboard overview metrics")
    public ResponseEntity<ApiResponse<OfficerWorkload>> getDashboardStats(@AuthenticationPrincipal UserPrincipal officer) {
        OfficerWorkload workload = officerQueueService.getOfficerWorkload(officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("SAG Dashboard metrics retrieved", workload));
    }

    @GetMapping("/applications")
    @Operation(summary = "Get applications assigned to the SAG officer")
    public ResponseEntity<ApiResponse<OfficerQueueResponse>> getAssignedApplications(@AuthenticationPrincipal UserPrincipal officer) {
        OfficerQueueResponse queue = officerQueueService.getOfficerQueue(officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Assigned applications retrieved", queue));
    }

    @GetMapping("/applications/{applicationId}")
    @Operation(summary = "Get application review details for SAG Officer")
    public ResponseEntity<ApiResponse<AiReviewSummaryDto>> getApplicationDetail(@PathVariable String applicationId) {
        AiReviewSummaryDto summary = officerQueueService.getApplicationSummary(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Application detail retrieved", summary));
    }

    @PostMapping("/applications/{applicationId}/approve")
    @Operation(summary = "Approve application and route to finance")
    public ResponseEntity<ApiResponse<Application>> approve(
            @PathVariable String applicationId,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserPrincipal officer) {
        Application app = officerQueueService.approveApplication(applicationId, officer.getId(), remarks);
        return ResponseEntity.ok(ApiResponse.ok("Application approved", app));
    }

    @PostMapping("/applications/{applicationId}/reject")
    @Operation(summary = "Reject application with remarks")
    public ResponseEntity<ApiResponse<Application>> reject(
            @PathVariable String applicationId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        Application app = officerQueueService.rejectApplication(applicationId, officer.getId(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Application rejected", app));
    }

    @PostMapping("/applications/{applicationId}/correction")
    @Operation(summary = "Request correction from applicant")
    public ResponseEntity<ApiResponse<CorrectionRequest>> requestCorrection(
            @PathVariable String applicationId,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String fieldKey,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        CorrectionRequest cr = officerQueueService.requestCorrection(applicationId, officer.getId(), documentType, fieldKey, reason);
        return ResponseEntity.ok(ApiResponse.ok("Correction requested", cr));
    }

    @PostMapping("/applications/{applicationId}/escalate")
    @Operation(summary = "Escalate application to supervisor")
    public ResponseEntity<ApiResponse<ApplicationEscalation>> escalate(
            @PathVariable String applicationId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        ApplicationEscalation escalation = officerQueueService.escalateApplication(applicationId, officer.getId(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Application escalated", escalation));
    }
}
