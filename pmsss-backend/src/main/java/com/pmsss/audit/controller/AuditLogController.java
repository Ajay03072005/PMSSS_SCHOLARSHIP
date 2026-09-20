package com.pmsss.audit.controller;

import com.pmsss.audit.entity.AuditLog;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit logging for sensitive administrative and financial actions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/recent")
    @Operation(summary = "Get 50 most recent audit log entries")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentLogs() {
        List<AuditLog> list = auditLogService.getRecentLogs();
        return ResponseEntity.ok(ApiResponse.ok("Recent audit logs retrieved", list));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @Operation(summary = "Get audit logs for a specific entity (e.g. Application or Document)")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getLogsForEntity(
            @PathVariable String entityName,
            @PathVariable String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AuditLog> pageResult = auditLogService.getLogsForEntity(entityName, entityId, PageRequest.of(page, size));

        PageResponse<AuditLog> response = PageResponse.<AuditLog>builder()
                .content(pageResult.getContent())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Entity audit logs retrieved", response));
    }
}
