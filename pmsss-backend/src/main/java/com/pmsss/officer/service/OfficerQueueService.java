package com.pmsss.officer.service;

import com.pmsss.ai.model.AiConsistencyMatchResult;
import com.pmsss.ai.model.AiDuplicateResult;
import com.pmsss.ai.entity.AnomalyAlertEntity;
import com.pmsss.ai.repository.AnomalyAlertRepository;
import com.pmsss.ai.service.AiService;
import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.exception.BusinessException;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.document.entity.Document;
import com.pmsss.document.repository.DocumentRepository;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.officer.dto.AiReviewSummaryDto;
import com.pmsss.officer.dto.OfficerQueueResponse;
import com.pmsss.officer.entity.ApplicationAssignment;
import com.pmsss.officer.entity.ApplicationEscalation;
import com.pmsss.officer.entity.OfficerWorkload;
import com.pmsss.officer.repository.ApplicationAssignmentRepository;
import com.pmsss.officer.repository.ApplicationEscalationRepository;
import com.pmsss.officer.repository.OfficerWorkloadRepository;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import com.pmsss.verification.entity.CorrectionRequest;
import com.pmsss.verification.repository.CorrectionRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfficerQueueService {

    private final ApplicationAssignmentRepository assignmentRepository;
    private final OfficerWorkloadRepository workloadRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final AnomalyAlertRepository anomalyAlertRepository;
    private final CorrectionRequestRepository correctionRequestRepository;
    private final ApplicationEscalationRepository escalationRepository;
    private final AiService aiService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public OfficerQueueResponse getOfficerQueue(Long officerId) {
        List<ApplicationAssignment> activeAssignments = assignmentRepository.findActiveWorkQueueForOfficer(officerId);

        for (ApplicationAssignment a : activeAssignments) {
            applicationRepository.findByApplicationId(a.getApplicationId()).ifPresent(app -> {
                a.setApplicantName(app.getFirstName() + " " + app.getLastName());
                a.setAiScore(app.getAiPriorityScore() != null ? app.getAiPriorityScore() : 95);
                a.setAnomalyCount(Boolean.TRUE.equals(app.getHasAnomalies()) ? 2 : 0);
                if (app.getAcademicInfo() != null && app.getAcademicInfo().contains("stream")) {
                    if (app.getAcademicInfo().contains("ENGINEERING")) a.setStream("ENGINEERING");
                    else if (app.getAcademicInfo().contains("MEDICAL")) a.setStream("MEDICAL_NURSING");
                    else a.setStream("GENERAL");
                } else {
                    a.setStream("ENGINEERING");
                }
            });
            if (a.getTriageReason() != null) {
                a.setReasons(java.util.Arrays.asList(a.getTriageReason().split(";\\s*")));
            }
        }

        List<ApplicationAssignment> quickReview = activeAssignments.stream()
                .filter(a -> "QUICK_REVIEW".equalsIgnoreCase(a.getReviewCategory()))
                .collect(Collectors.toList());

        List<ApplicationAssignment> needsAttention = activeAssignments.stream()
                .filter(a -> !"QUICK_REVIEW".equalsIgnoreCase(a.getReviewCategory()))
                .collect(Collectors.toList());

        long highPriority = activeAssignments.stream()
                .filter(a -> "HIGH".equalsIgnoreCase(a.getPriority()))
                .count();

        long overdue = activeAssignments.stream()
                .filter(ApplicationAssignment::isOverdue)
                .count();

        ApplicationAssignment recommendedNext = activeAssignments.isEmpty() ? null : activeAssignments.get(0);

        return OfficerQueueResponse.builder()
                .totalActive(activeAssignments.size())
                .quickReviewCount(quickReview.size())
                .needsAttentionCount(needsAttention.size())
                .highPriorityCount((int) highPriority)
                .overdueCount((int) overdue)
                .quickReviewList(quickReview)
                .needsAttentionList(needsAttention)
                .recommendedNext(recommendedNext)
                .build();
    }

    public OfficerWorkload getOfficerWorkload(Long officerId) {
        return workloadRepository.findByOfficerId(officerId)
                .orElseGet(() -> {
                    User officer = userRepository.findById(officerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Officer not found: " + officerId));
                    return workloadRepository.save(OfficerWorkload.builder()
                            .officer(officer)
                            .officerName(officer.getFullName())
                            .officerEmail(officer.getEmail())
                            .currentWorkload(0)
                            .maxCapacity(100)
                            .isActive(true)
                            .build());
                });
    }

    public AiReviewSummaryDto getApplicationSummary(String applicationId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        List<Document> documents = documentRepository.findByApplicationId(app.getId());
        AiConsistencyMatchResult.Summary consistency = aiService.checkConsistency(applicationId);
        AiDuplicateResult duplicates = aiService.checkDuplicates(applicationId);
        List<AnomalyAlertEntity> anomalies = anomalyAlertRepository.findByApplicationId(applicationId);

        String recommendation;
        if (Boolean.TRUE.equals(app.getHasAnomalies()) || duplicates.isPossibleDuplicate() || !consistency.isOverallConsistent()) {
            recommendation = "DETAILED_INSPECTION_REQUIRED: Review highlighted exceptions and verify supporting documents before decision.";
        } else {
            recommendation = "QUICK_REVIEW_RECOMMENDED: All automated pre-validations and document extractions matched successfully.";
        }

        String candidateId = (duplicates.getDuplicateWithApplicationIds() != null && !duplicates.getDuplicateWithApplicationIds().isEmpty())
                ? duplicates.getDuplicateWithApplicationIds().get(0)
                : null;

        return AiReviewSummaryDto.builder()
                .applicationId(app.getApplicationId())
                .applicantName(app.getFirstName() + " " + app.getLastName())
                .annualIncome(app.getAnnualIncome())
                .status(app.getStatus().name())
                .documentsCount(documents.size())
                .overallMatchScore(consistency.getAverageConfidence() > 0 ? (consistency.getAverageConfidence() * 100) : 95.0)
                .comparisonItems(consistency.getFieldMatches())
                .duplicateSuspected(duplicates.isPossibleDuplicate())
                .duplicateMatchPercentage(duplicates.getConfidence() * 100)
                .duplicateCandidateId(candidateId)
                .activeAnomaliesCount(anomalies.size())
                .anomalyReasons(anomalies.stream().map(AnomalyAlertEntity::getReason).collect(Collectors.toList()))
                .recommendedAction(recommendation)
                .build();
    }

    @Transactional
    public Application approveApplication(String applicationId, Long officerId, String remarks) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        app.setStatus(ApplicationStatus.SAG_APPROVED);
        app.setReviewedBy(officerId);
        app.setReviewedAt(LocalDateTime.now());
        app.setReviewRemarks(remarks != null ? remarks : "Approved by SAG Officer");
        applicationRepository.save(app);

        completeAssignment(applicationId, officerId);

        auditLogService.logAction(
                getOfficerEmail(officerId),
                "SAG_OFFICER",
                "APPLICATION_APPROVED",
                "Application",
                applicationId,
                "ASSIGNED",
                "SAG_APPROVED"
        );

        if (app.getUser() != null) {
            notificationService.createNotification(
                    app.getUser(),
                    app,
                    "APPLICATION_APPROVED",
                    "Application Approved by SAG Officer",
                    "Your PMSSS application " + applicationId + " has been verified and approved. Sent for scholarship disbursement."
            );
        }

        return app;
    }

    @Transactional
    public Application rejectApplication(String applicationId, Long officerId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("A valid rejection reason is required.");
        }

        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        app.setStatus(ApplicationStatus.SAG_REJECTED);
        app.setReviewedBy(officerId);
        app.setReviewedAt(LocalDateTime.now());
        app.setReviewRemarks(reason);
        applicationRepository.save(app);

        completeAssignment(applicationId, officerId);

        auditLogService.logAction(
                getOfficerEmail(officerId),
                "SAG_OFFICER",
                "APPLICATION_REJECTED",
                "Application",
                applicationId,
                "ASSIGNED",
                "SAG_REJECTED"
        );

        if (app.getUser() != null) {
            notificationService.createNotification(
                    app.getUser(),
                    app,
                    "APPLICATION_REJECTED",
                    "Application Rejected",
                    "Your application " + applicationId + " was rejected: " + reason
            );
        }

        return app;
    }

    @Transactional
    public CorrectionRequest requestCorrection(String applicationId, Long officerId, String docType, String fieldKey, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("A reason for requesting correction is required.");
        }

        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        CorrectionRequest cr = CorrectionRequest.builder()
                .applicationId(applicationId)
                .documentType(docType)
                .fieldKey(fieldKey)
                .reason(reason)
                .requestedBy(getOfficerEmail(officerId))
                .isResolved(false)
                .build();
        CorrectionRequest saved = correctionRequestRepository.save(cr);

        app.setStatus(ApplicationStatus.NEEDS_CORRECTION);
        applicationRepository.save(app);

        auditLogService.logAction(
                getOfficerEmail(officerId),
                "SAG_OFFICER",
                "CORRECTION_REQUESTED",
                "CorrectionRequest",
                applicationId,
                null,
                reason
        );

        if (app.getUser() != null) {
            notificationService.createNotification(
                    app.getUser(),
                    app,
                    "CORRECTION_REQUIRED",
                    "Action Required: Application Correction Requested",
                    "Please update your " + (docType != null ? docType : fieldKey) + ": " + reason
            );
        }

        return saved;
    }

    @Transactional
    public ApplicationEscalation escalateApplication(String applicationId, Long officerId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("An escalation reason is required.");
        }

        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        app.setStatus(ApplicationStatus.ESCALATED);
        applicationRepository.save(app);

        Optional<ApplicationAssignment> assignOpt = assignmentRepository.findByApplicationIdAndStatus(applicationId, "ACTIVE");
        assignOpt.ifPresent(a -> {
            a.setStatus("ESCALATED");
            assignmentRepository.save(a);
        });

        ApplicationEscalation escalation = ApplicationEscalation.builder()
                .applicationId(applicationId)
                .officerId(officerId)
                .officerName(getOfficerEmail(officerId))
                .reason(reason)
                .status("OPEN")
                .build();
        ApplicationEscalation saved = escalationRepository.save(escalation);

        auditLogService.logAction(
                getOfficerEmail(officerId),
                "SAG_OFFICER",
                "APPLICATION_ESCALATED",
                "ApplicationEscalation",
                applicationId,
                null,
                reason
        );

        return saved;
    }

    private void completeAssignment(String applicationId, Long officerId) {
        Optional<ApplicationAssignment> assignOpt = assignmentRepository.findByApplicationIdAndStatus(applicationId, "ACTIVE");
        assignOpt.ifPresent(a -> {
            a.setStatus("COMPLETED");
            a.setCompletedAt(LocalDateTime.now());
            assignmentRepository.save(a);
        });

        workloadRepository.findByOfficerId(officerId).ifPresent(w -> {
            w.setCurrentWorkload(Math.max(0, w.getCurrentWorkload() - 1));
            w.setCompletedToday(w.getCompletedToday() + 1);
            workloadRepository.save(w);
        });
    }

    private String getOfficerEmail(Long officerId) {
        return userRepository.findById(officerId).map(User::getEmail).orElse("officer@" + officerId);
    }
}
