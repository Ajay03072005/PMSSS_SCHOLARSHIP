package com.pmsss.ai.controller;

import com.pmsss.ai.entity.AnomalyAlertEntity;
import com.pmsss.ai.model.*;
import com.pmsss.ai.repository.AnomalyAlertRepository;
import com.pmsss.ai.service.AiService;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Capabilities", description = "AI document intelligence, completeness checker, anomaly detection, chatbot, and analytics")
public class AiController {

    private final AiService aiService;
    private final AnomalyAlertRepository anomalyAlertRepository;

    @GetMapping("/completeness/{applicationId}")
    @Operation(summary = "Check application completeness and missing mandatory requirements")
    public ResponseEntity<ApiResponse<AiCompletenessResult>> checkCompleteness(@PathVariable String applicationId) {
        AiCompletenessResult result = aiService.checkCompleteness(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Completeness check completed", result));
    }

    @GetMapping("/consistency/{applicationId}")
    @Operation(summary = "AI Document vs Application consistency check and match analysis")
    public ResponseEntity<ApiResponse<AiConsistencyMatchResult.Summary>> checkConsistency(@PathVariable String applicationId) {
        AiConsistencyMatchResult.Summary result = aiService.checkConsistency(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Consistency match evaluation completed", result));
    }

    @GetMapping("/duplicate-check/{applicationId}")
    @Operation(summary = "AI duplicate application detection")
    public ResponseEntity<ApiResponse<AiDuplicateResult>> checkDuplicates(@PathVariable String applicationId) {
        AiDuplicateResult result = aiService.checkDuplicates(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Duplicate check completed", result));
    }

    @GetMapping("/anomalies/{applicationId}")
    @Operation(summary = "Scan application for suspicious patterns and anomalies")
    public ResponseEntity<ApiResponse<List<AiAnomalyAlert>>> scanAnomalies(@PathVariable String applicationId) {
        List<AiAnomalyAlert> alerts = aiService.scanForAnomalies(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Anomaly scan completed", alerts));
    }

    @GetMapping("/anomalies/active")
    @PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "List all active unresolved anomaly alerts")
    public ResponseEntity<ApiResponse<List<AnomalyAlertEntity>>> getActiveAnomalies() {
        List<AnomalyAlertEntity> list = anomalyAlertRepository.findByIsResolvedFalseOrderByDetectedAtDesc();
        return ResponseEntity.ok(ApiResponse.ok("Active anomaly alerts retrieved", list));
    }

    @PostMapping("/eligibility/check")
    @Operation(summary = "AI Eligibility Assistant based strictly on official PMSSS rules")
    public ResponseEntity<ApiResponse<AiEligibilityResult>> checkEligibility(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal income,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String institution,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Double percentage12th) {

        AiEligibilityResult result = aiService.evaluateEligibility(category, income, course, institution, state, percentage12th);
        return ResponseEntity.ok(ApiResponse.ok("Eligibility assessment completed", result));
    }

    @PostMapping("/chatbot/query")
    @Operation(summary = "PMSSS AI Chatbot Assistant")
    public ResponseEntity<ApiResponse<AiChatbotResponse>> askChatbot(
            @RequestParam String query,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        AiChatbotResponse response = aiService.chat(query, userId);
        return ResponseEntity.ok(ApiResponse.ok("Chat response generated", response));
    }

    @GetMapping("/status-explanation")
    @Operation(summary = "AI student-friendly explanation of internal application workflow status")
    public ResponseEntity<ApiResponse<String>> explainStatus(
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason) {

        ApplicationStatus appStatus = ApplicationStatus.fromString(status);
        String explanation = aiService.explainStatus(appStatus, rejectionReason);
        return ResponseEntity.ok(ApiResponse.ok("Status explanation generated", explanation));
    }

    @GetMapping("/review-queue")
    @PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "AI Smart Prioritized Review Queue for Officers (HIGH, NORMAL, LOW attention)")
    public ResponseEntity<ApiResponse<List<AiReviewQueueItem>>> getSmartReviewQueue() {
        List<AiReviewQueueItem> queue = aiService.getSmartReviewQueue();
        return ResponseEntity.ok(ApiResponse.ok("Smart review queue retrieved", queue));
    }

    @PostMapping("/analytics/query")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Safe Natural Language Analytics for Administrators")
    public ResponseEntity<ApiResponse<NaturalLanguageQueryResponse>> runNaturalLanguageAnalytics(@RequestParam String query) {
        NaturalLanguageQueryResponse response = aiService.runNaturalLanguageAnalytics(query);
        return ResponseEntity.ok(ApiResponse.ok("Analytics query executed", response));
    }
}
