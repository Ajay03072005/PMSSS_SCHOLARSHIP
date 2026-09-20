package com.pmsss.verification.controller;

import com.pmsss.application.entity.Application;
import com.pmsss.application.response.ApplicationResponse;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.VerificationDecision;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.verification.entity.VerificationRecord;
import com.pmsss.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
@Tag(name = "Verification", description = "SAG Officer verification and review endpoints")
@PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
public class VerificationController {

    private final VerificationService verificationService;

    private final ApplicationService applicationService;

    @PostMapping("/document/{documentId}")
    @Operation(summary = "Verify or reject a specific document")
    public ResponseEntity<ApiResponse<VerificationRecord>> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam VerificationDecision decision,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserPrincipal officer) {

        VerificationRecord record = verificationService.verifyDocument(documentId, decision, rejectionReason, notes, officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Document verification recorded", record));
    }

    @PostMapping("/application/{applicationId}")
    @Operation(summary = "Perform final SAG review on an application (Approve or Reject)")
    public ResponseEntity<ApiResponse<com.pmsss.application.response.ApplicationResponse>> reviewApplication(
            @PathVariable String applicationId,
            @RequestParam VerificationDecision decision,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserPrincipal officer) {

        verificationService.reviewApplication(applicationId, decision, remarks, officer.getId());
        var response = applicationService.getApplicationById(applicationId, null);
        return ResponseEntity.ok(ApiResponse.ok("Application review completed", response));
    }

    @GetMapping("/application/{applicationId}/history")
    @Operation(summary = "Get verification history for an application")
    public ResponseEntity<ApiResponse<List<VerificationRecord>>> getHistory(@PathVariable Long applicationId) {
        List<VerificationRecord> list = verificationService.getHistoryForApplication(applicationId);
        return ResponseEntity.ok(ApiResponse.ok("Verification history retrieved", list));
    }
}
