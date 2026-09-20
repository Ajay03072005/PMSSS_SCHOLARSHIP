package com.pmsss.dashboard.model;

import com.pmsss.application.response.ApplicationResponse;
import com.pmsss.application.response.ApplicationTrackResponse;
import com.pmsss.document.entity.Document;
import com.pmsss.notification.entity.Notification;
import com.pmsss.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDto {
    private ApplicationResponse latestApplication;
    private ApplicationTrackResponse timeline;
    private List<Document> documents;
    private List<Notification> recentNotifications;
    private List<Payment> payments;
    private long unreadNotificationCount;
    private String aiStatusAdvice;
}
