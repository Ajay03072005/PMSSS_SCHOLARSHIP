package com.pmsss.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmsss.application.request.ApplicationSubmitRequest;
import com.pmsss.application.response.ApplicationResponse;
import com.pmsss.application.response.ApplicationTrackResponse;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.common.response.PageResponse;
import com.pmsss.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Applications", description = "Application lifecycle management, submission, and status tracking")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Submit or update scholarship application draft")
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "aadhar", required = false) MultipartFile aadhar,
            @RequestPart(value = "domicile", required = false) MultipartFile domicile,
            @RequestPart(value = "income", required = false) MultipartFile income,
            @RequestPart(value = "tenthMarksheet", required = false) MultipartFile tenthMarksheet,
            @RequestPart(value = "twelfthMarksheet", required = false) MultipartFile twelfthMarksheet,
            @RequestPart(value = "admissionLetter", required = false) MultipartFile admissionLetter,
            @RequestPart(value = "bankPassbook", required = false) MultipartFile bankPassbook) throws Exception {

        ApplicationSubmitRequest request = objectMapper.readValue(dataJson, ApplicationSubmitRequest.class);

        Map<String, String> uploadedFiles = new HashMap<>();
        if (photo != null && !photo.isEmpty()) uploadedFiles.put("photo", documentService.storeFile(photo, "photo"));
        if (aadhar != null && !aadhar.isEmpty()) uploadedFiles.put("aadhar", documentService.storeFile(aadhar, "aadhar"));
        if (domicile != null && !domicile.isEmpty()) uploadedFiles.put("domicile", documentService.storeFile(domicile, "domicile"));
        if (income != null && !income.isEmpty()) uploadedFiles.put("income", documentService.storeFile(income, "income"));
        if (tenthMarksheet != null && !tenthMarksheet.isEmpty()) uploadedFiles.put("tenthMarksheet", documentService.storeFile(tenthMarksheet, "tenth_marksheet"));
        if (twelfthMarksheet != null && !twelfthMarksheet.isEmpty()) uploadedFiles.put("twelfthMarksheet", documentService.storeFile(twelfthMarksheet, "twelfth_marksheet"));
        if (admissionLetter != null && !admissionLetter.isEmpty()) uploadedFiles.put("admissionLetter", documentService.storeFile(admissionLetter, "admission_letter"));
        if (bankPassbook != null && !bankPassbook.isEmpty()) uploadedFiles.put("bankPassbook", documentService.storeFile(bankPassbook, "bank_passbook"));

        ApplicationResponse response = applicationService.submitOrUpdateApplication(userPrincipal.getId(), request, uploadedFiles);
        return ResponseEntity.ok(ApiResponse.ok("Application processed successfully", response));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Submit or update scholarship application draft (JSON)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplicationJson(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ApplicationSubmitRequest request) {
        ApplicationResponse response = applicationService.submitOrUpdateApplication(userPrincipal.getId(), request, new HashMap<>());
        return ResponseEntity.ok(ApiResponse.ok("Application processed successfully", response));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current student's applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ApplicationResponse> list = applicationService.getMyApplications(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Applications retrieved", list));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable String applicationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("SAG_OFFICER"))
                ? null : userPrincipal.getId();
        ApplicationResponse response = applicationService.getApplicationById(applicationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Application details retrieved", response));
    }

    @PostMapping("/track")
    @Operation(summary = "Public tracking of application by Application ID and Date of Birth")
    public ResponseEntity<ApiResponse<ApplicationTrackResponse>> trackApplication(
            @RequestParam String applicationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth) {
        ApplicationTrackResponse response = applicationService.trackApplication(applicationId, dateOfBirth);
        return ResponseEntity.ok(ApiResponse.ok("Application status found", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SAG_OFFICER', 'FINANCE_OFFICER')")
    @Operation(summary = "Search and filter applications (Officers & Admins)")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> listApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        ApplicationStatus appStatus = status != null && !status.equalsIgnoreCase("ALL") ? ApplicationStatus.fromString(status) : null;

        Page<ApplicationResponse> pagedResult = applicationService.searchApplications(appStatus, search, pageRequest);

        PageResponse<ApplicationResponse> response = PageResponse.<ApplicationResponse>builder()
                .content(pagedResult.getContent())
                .pageNumber(pagedResult.getNumber())
                .pageSize(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .last(pagedResult.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Applications retrieved", response));
    }

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SAG_OFFICER')")
    @Operation(summary = "Update application status (Admin/Officer)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable String applicationId,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ApplicationStatus newStatus = ApplicationStatus.fromString(status);
        ApplicationResponse response = applicationService.updateStatus(applicationId, newStatus, remarks, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Application status updated successfully", response));
    }

    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete application (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable String applicationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        applicationService.deleteApplication(applicationId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Application deleted successfully", null));
    }
}
