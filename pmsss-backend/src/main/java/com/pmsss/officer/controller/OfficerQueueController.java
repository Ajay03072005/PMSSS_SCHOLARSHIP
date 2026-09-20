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
@RequestMapping("/api/v1/officer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Officer Queue & Review", description = "Personalized work queues, AI review summaries, decisions, corrections, and escalations")
public class OfficerQueueController {

    private final OfficerQueueService officerQueueService;

    @GetMapping("/queue")
    @Operation(summary = "Get current officer's prioritized work queue segregated by Quick Review and Needs Attention")
    public ResponseEntity<ApiResponse<OfficerQueueResponse>> getMyQueue(@AuthenticationPrincipal UserPrincipal officer) {
        OfficerQueueResponse queue = officerQueueService.getOfficerQueue(officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Officer queue retrieved", queue));
    }

    @GetMapping("/workload")
    @Operation(summary = "Get current officer's active workload capacity and utilization metrics")
    public ResponseEntity<ApiResponse<OfficerWorkload>> getMyWorkload(@AuthenticationPrincipal UserPrincipal officer) {
        OfficerWorkload workload = officerQueueService.getOfficerWorkload(officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Officer workload retrieved", workload));
    }

    @GetMapping("/applications/{applicationId}/summary")
    @Operation(summary = "Get aggregated AI review summary for an application (OCR, comparisons, anomalies, duplicates)")
    public ResponseEntity<ApiResponse<AiReviewSummaryDto>> getApplicationSummary(@PathVariable String applicationId) {
        AiReviewSummaryDto summary = officerQueueService.getApplicationSummary(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("AI Review Summary retrieved", summary));
    }

    @PostMapping("/applications/{applicationId}/approve")
    @Operation(summary = "Approve application and transition to SAG_APPROVED")
    public ResponseEntity<ApiResponse<Application>> approveApplication(
            @PathVariable String applicationId,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserPrincipal officer) {
        Application app = officerQueueService.approveApplication(applicationId, officer.getId(), remarks);
        return ResponseEntity.ok(ApiResponse.ok("Application approved successfully", app));
    }

    @PostMapping("/applications/{applicationId}/reject")
    @Operation(summary = "Reject application with required reason and transition to SAG_REJECTED")
    public ResponseEntity<ApiResponse<Application>> rejectApplication(
            @PathVariable String applicationId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        Application app = officerQueueService.rejectApplication(applicationId, officer.getId(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Application rejected", app));
    }

    @PostMapping("/applications/{applicationId}/correction-request")
    @Operation(summary = "Request targeted student correction for document or field")
    public ResponseEntity<ApiResponse<CorrectionRequest>> requestCorrection(
            @PathVariable String applicationId,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String fieldKey,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        CorrectionRequest cr = officerQueueService.requestCorrection(applicationId, officer.getId(), documentType, fieldKey, reason);
        return ResponseEntity.ok(ApiResponse.ok("Correction request submitted to student", cr));
    }

    @PostMapping("/applications/{applicationId}/escalate")
    @Operation(summary = "Escalate application to supervisor due to SLA breach or complex exception")
    public ResponseEntity<ApiResponse<ApplicationEscalation>> escalateApplication(
            @PathVariable String applicationId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {
        ApplicationEscalation escalation = officerQueueService.escalateApplication(applicationId, officer.getId(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Application successfully escalated to supervisor", escalation));
    }
}
