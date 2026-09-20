package com.pmsss.workflow.service;

import com.pmsss.application.entity.Application;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final ApplicationService applicationService;

    // Defines valid next states for each ApplicationStatus
    private static final Map<ApplicationStatus, Set<ApplicationStatus>> VALID_TRANSITIONS = new EnumMap<>(ApplicationStatus.class);

    static {
        VALID_TRANSITIONS.put(ApplicationStatus.DRAFT, EnumSet.of(ApplicationStatus.SUBMITTED));
        VALID_TRANSITIONS.put(ApplicationStatus.SUBMITTED, EnumSet.of(ApplicationStatus.DOCUMENT_VERIFICATION, ApplicationStatus.SAG_REJECTED));
        VALID_TRANSITIONS.put(ApplicationStatus.DOCUMENT_VERIFICATION, EnumSet.of(ApplicationStatus.SAG_REVIEW, ApplicationStatus.SAG_REJECTED));
        VALID_TRANSITIONS.put(ApplicationStatus.SAG_REVIEW, EnumSet.of(ApplicationStatus.SAG_APPROVED, ApplicationStatus.SAG_REJECTED));
        VALID_TRANSITIONS.put(ApplicationStatus.SAG_APPROVED, EnumSet.of(ApplicationStatus.SENT_TO_FINANCE));
        VALID_TRANSITIONS.put(ApplicationStatus.SENT_TO_FINANCE, EnumSet.of(ApplicationStatus.PAYMENT_PROCESSING));
        VALID_TRANSITIONS.put(ApplicationStatus.PAYMENT_PROCESSING, EnumSet.of(ApplicationStatus.PAYMENT_COMPLETED, ApplicationStatus.SENT_TO_FINANCE));
        VALID_TRANSITIONS.put(ApplicationStatus.PAYMENT_COMPLETED, EnumSet.of(ApplicationStatus.COMPLETED));
        VALID_TRANSITIONS.put(ApplicationStatus.COMPLETED, EnumSet.noneOf(ApplicationStatus.class));
        VALID_TRANSITIONS.put(ApplicationStatus.SAG_REJECTED, EnumSet.of(ApplicationStatus.SUBMITTED)); // can resubmit
    }

    public boolean canTransition(ApplicationStatus from, ApplicationStatus to) {
        Set<ApplicationStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public Application transition(String applicationId, ApplicationStatus targetStatus, String remarks, Long actorId) {
        var currentApp = applicationService.getApplicationById(applicationId, null);
        ApplicationStatus currentStatus = currentApp.getStatus();

        if (!canTransition(currentStatus, targetStatus)) {
            throw new BusinessException("Invalid workflow state transition from " + currentStatus + " to " + targetStatus, "INVALID_STATE_TRANSITION");
        }

        applicationService.updateStatus(applicationId, targetStatus, remarks, actorId);
        log.info("Application {} transitioned from {} to {}", applicationId, currentStatus, targetStatus);
        return null;
    }
}
