package com.pmsss.officer.service;

import com.pmsss.ai.model.AiConsistencyMatchResult;
import com.pmsss.ai.model.AiDuplicateResult;
import com.pmsss.ai.service.AiService;
import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.RoleType;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.officer.entity.ApplicationAssignment;
import com.pmsss.officer.entity.OfficerWorkload;
import com.pmsss.officer.repository.ApplicationAssignmentRepository;
import com.pmsss.officer.repository.OfficerWorkloadRepository;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficerAssignmentService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OfficerAssignmentService.class);


    private final OfficerWorkloadRepository officerWorkloadRepository;
    private final ApplicationAssignmentRepository assignmentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final NotificationService notificationService;

    @Transactional
    public ApplicationAssignment assignApplication(Application app) {
        log.info("Starting smart officer assignment for application {}", app.getApplicationId());

        // 1. Find best available officer with spare capacity
        List<OfficerWorkload> available = officerWorkloadRepository.findAvailableOfficersByUtilization();
        OfficerWorkload targetWorkload = null;

        if (!available.isEmpty()) {
            targetWorkload = available.get(0);
        } else {
            // Fallback: any active officer with lowest workload
            List<OfficerWorkload> allActive = officerWorkloadRepository.findByIsActiveTrueOrderByCurrentWorkloadAsc();
            if (!allActive.isEmpty()) {
                targetWorkload = allActive.get(0);
            }
        }

        // If no officer workloads created yet, try to find an officer user and initialize their workload
        if (targetWorkload == null) {
            List<User> officers = userRepository.findByRole(RoleType.ROLE_SAG_OFFICER);
            if (!officers.isEmpty()) {
                User defaultOfficer = officers.get(0);
                targetWorkload = officerWorkloadRepository.save(OfficerWorkload.builder()
                        .officer(defaultOfficer)
                        .officerName(defaultOfficer.getFullName())
                        .officerEmail(defaultOfficer.getEmail())
                        .currentWorkload(0)
                        .maxCapacity(100)
                        .isActive(true)
                        .build());
            } else {
                log.warn("No SAG Officers found in system. Unable to assign application {}", app.getApplicationId());
                return null;
            }
        }

        // 2. Perform AI evaluation for triage category & priority
        AiConsistencyMatchResult.Summary consistency = aiService.checkConsistency(app.getApplicationId());
        AiDuplicateResult duplicates = aiService.checkDuplicates(app.getApplicationId());

        boolean hasAnomalies = Boolean.TRUE.equals(app.getHasAnomalies());
        boolean hasDuplicateConcern = duplicates.isPossibleDuplicate();
        boolean hasDocumentIssues = !consistency.isOverallConsistent() || consistency.getAverageConfidence() < 0.8;

        String reviewCategory = "QUICK_REVIEW";
        String triageReason = "All pre-checks passed cleanly. Standard review.";

        if (hasAnomalies || hasDuplicateConcern || hasDocumentIssues) {
            reviewCategory = "NEEDS_ATTENTION";
            StringBuilder reasonBuilder = new StringBuilder("Flags detected: ");
            if (hasAnomalies) reasonBuilder.append("Active anomaly alert; ");
            if (hasDuplicateConcern) reasonBuilder.append("Possible duplicate candidate (").append((int)(duplicates.getConfidence() * 100)).append("%); ");
            if (hasDocumentIssues) reasonBuilder.append("Document match average confidence is ").append((int)(consistency.getAverageConfidence() * 100)).append("%; ");
            triageReason = reasonBuilder.toString();
        }

        // 3. Priority calculation
        String priority = "NORMAL";
        if (app.getAnnualIncome() != null && app.getAnnualIncome().compareTo(new BigDecimal("250000.00")) <= 0) {
            priority = "HIGH"; // Economically weaker section priority
        } else if (hasAnomalies || hasDuplicateConcern) {
            priority = "HIGH"; // Requires urgent human scrutiny
        } else if (hasDocumentIssues) {
            priority = "MEDIUM";
        }

        // 4. SLA calculation
        int slaHours = "HIGH".equalsIgnoreCase(priority) ? 24 : 48;
        LocalDateTime slaDueAt = LocalDateTime.now().plusHours(slaHours);

        // 5. Create assignment record
        ApplicationAssignment assignment = ApplicationAssignment.builder()
                .applicationId(app.getApplicationId())
                .officer(targetWorkload.getOfficer())
                .officerName(targetWorkload.getOfficerName())
                .officerEmail(targetWorkload.getOfficerEmail())
                .assignedBy("SYSTEM_SMART_ASSIGNER")
                .priority(priority)
                .reviewCategory(reviewCategory)
                .status("ACTIVE")
                .slaDueAt(slaDueAt)
                .triageReason(triageReason)
                .build();

        ApplicationAssignment savedAssignment = assignmentRepository.save(assignment);

        // 6. Update officer workload metrics
        targetWorkload.setCurrentWorkload(targetWorkload.getCurrentWorkload() + 1);
        targetWorkload.setLastAssignedAt(LocalDateTime.now());
        officerWorkloadRepository.save(targetWorkload);

        // 7. Transition Application status
        app.setStatus(ApplicationStatus.ASSIGNED);
        app.setReviewedBy(targetWorkload.getOfficer().getId());
        applicationRepository.save(app);

        // 8. Notify assigned officer
        notificationService.createNotification(
                targetWorkload.getOfficer(),
                app,
                "ASSIGNMENT",
                "New Application Assigned: " + app.getApplicationId(),
                "Priority: " + priority + " | Category: " + reviewCategory + " | SLA Due: " + slaDueAt
        );

        log.info("Application {} assigned to officer {} ({}) under category {} with priority {}",
                app.getApplicationId(), targetWorkload.getOfficerName(), targetWorkload.getOfficerEmail(), reviewCategory, priority);

        return savedAssignment;
    }
}
