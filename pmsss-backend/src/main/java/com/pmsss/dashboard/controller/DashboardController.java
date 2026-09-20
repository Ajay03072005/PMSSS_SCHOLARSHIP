package com.pmsss.dashboard.controller;

import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.dashboard.model.AdminDashboardDto;
import com.pmsss.dashboard.model.FinanceDashboardDto;
import com.pmsss.dashboard.model.OfficerDashboardDto;
import com.pmsss.dashboard.model.StudentDashboardDto;
import com.pmsss.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboards", description = "Role-based dashboards for Students, SAG Officers, Finance, and Administrators")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @Operation(summary = "Get student dashboard metrics, timeline, and AI status advice")
    public ResponseEntity<ApiResponse<StudentDashboardDto>> getStudentDashboard(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        StudentDashboardDto dto = dashboardService.getStudentDashboard(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Student dashboard retrieved", dto));
    }

    @GetMapping("/officer")
    @PreAuthorize("hasAnyRole('SAG_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get SAG Verification Officer dashboard metrics and prioritized review queue")
    public ResponseEntity<ApiResponse<OfficerDashboardDto>> getOfficerDashboard() {
        OfficerDashboardDto dto = dashboardService.getOfficerDashboard();
        return ResponseEntity.ok(ApiResponse.ok("Officer dashboard retrieved", dto));
    }

    @GetMapping("/finance")
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get Finance Officer dashboard metrics and disbursement statuses")
    public ResponseEntity<ApiResponse<FinanceDashboardDto>> getFinanceDashboard() {
        FinanceDashboardDto dto = dashboardService.getFinanceDashboard();
        return ResponseEntity.ok(ApiResponse.ok("Finance dashboard retrieved", dto));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get Executive Administrator dashboard statistics and trends")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getAdminDashboard() {
        AdminDashboardDto dto = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.ok("Admin dashboard retrieved", dto));
    }
}
