package com.pmsss.notification.controller;

import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.notification.entity.Notification;
import com.pmsss.notification.entity.NotificationPreference;
import com.pmsss.notification.repository.NotificationPreferenceRepository;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user notifications, alerts, and preferences")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get current user notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved", notifications));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.markAsRead(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notifications count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Unread count retrieved", count));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences for current user")
    public ResponseEntity<ApiResponse<NotificationPreference>> getPreferences(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        NotificationPreference pref = preferenceRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUser.getId()).orElse(null);
                    return preferenceRepository.save(NotificationPreference.builder().user(user).build());
                });
        return ResponseEntity.ok(ApiResponse.ok("Preferences retrieved", pref));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update notification preferences for current user")
    public ResponseEntity<ApiResponse<NotificationPreference>> updatePreferences(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody NotificationPreference updated) {
        NotificationPreference pref = preferenceRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUser.getId()).orElse(null);
                    return NotificationPreference.builder().user(user).build();
                });

        if (updated.getEmailApplicationUpdates() != null) pref.setEmailApplicationUpdates(updated.getEmailApplicationUpdates());
        if (updated.getEmailDocumentUpdates() != null) pref.setEmailDocumentUpdates(updated.getEmailDocumentUpdates());
        if (updated.getEmailPaymentUpdates() != null) pref.setEmailPaymentUpdates(updated.getEmailPaymentUpdates());
        if (updated.getSmsApplicationUpdates() != null) pref.setSmsApplicationUpdates(updated.getSmsApplicationUpdates());
        if (updated.getSmsPaymentUpdates() != null) pref.setSmsPaymentUpdates(updated.getSmsPaymentUpdates());

        NotificationPreference saved = preferenceRepository.save(pref);
        return ResponseEntity.ok(ApiResponse.ok("Preferences updated", saved));
    }
}
