package com.pmsss.notification.controller;

import com.pmsss.common.response.ApiResponse;
import com.pmsss.notification.entity.NotificationLog;
import com.pmsss.notification.enums.NotificationChannel;
import com.pmsss.notification.enums.NotificationStatus;
import com.pmsss.notification.repository.NotificationLogRepository;
import com.pmsss.notification.service.NotificationDispatcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin Notifications", description = "Endpoints for monitoring notification history and triggering retries")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminNotificationController {

    private final NotificationLogRepository logRepository;
    private final NotificationDispatcherService dispatcherService;

    @GetMapping
    @Operation(summary = "Get notification logs history")
    public ResponseEntity<ApiResponse<Page<NotificationLog>>> getNotificationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationChannel channel) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationLog> logs;

        if (status != null) {
            logs = logRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (channel != null) {
            logs = logRepository.findByChannelOrderByCreatedAtDesc(channel, pageable);
        } else {
            logs = logRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return ResponseEntity.ok(ApiResponse.ok("Notification history retrieved", logs));
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Manually retry a failed notification")
    public ResponseEntity<ApiResponse<Boolean>> retryNotification(@PathVariable Long id) {
        boolean success = dispatcherService.retryNotification(id);
        return ResponseEntity.ok(ApiResponse.ok(
                success ? "Notification retry succeeded" : "Notification retry failed",
                success
        ));
    }
}
