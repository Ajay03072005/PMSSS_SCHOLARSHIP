package com.pmsss.verification.service;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.DocumentStatus;
import com.pmsss.common.enums.VerificationDecision;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.document.entity.Document;
import com.pmsss.document.repository.DocumentRepository;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import com.pmsss.verification.entity.VerificationRecord;
import com.pmsss.verification.repository.VerificationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final VerificationRecordRepository verificationRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public VerificationRecord verifyDocument(Long documentId, VerificationDecision decision, String rejectionReason, String notes, Long officerId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", officerId));

        Application application = document.getApplication();

        if (decision == VerificationDecision.VERIFIED) {
            document.setVerificationStatus(DocumentStatus.VERIFIED);
            document.setRejectionReason(null);
        } else if (decision == VerificationDecision.REJECTED) {
            document.setVerificationStatus(DocumentStatus.REJECTED);
            document.setRejectionReason(rejectionReason);

            notificationService.sendNotification(application.getUser(), application, "DOCUMENT_REJECTED",
                    "Document Rejected: " + document.getDocumentType(),
                    "Your " + document.getDocumentType() + " was rejected. Reason: " + rejectionReason + ". Please re-upload.");
        }

        documentRepository.save(document);

        VerificationRecord record = VerificationRecord.builder()
                .application(application)
                .document(document)
                .officer(officer)
                .decision(decision)
                .rejectionReason(rejectionReason)
                .officerNotes(notes)
                .build();

        record = verificationRepository.save(record);

        auditLogService.logAction(officer.getEmail(), officer.getRole().name(), "DOCUMENT_VERIFIED",
                "Document", document.getId().toString(), null, decision.name());

        return record;
    }

    @Transactional
    public Application reviewApplication(String applicationId, VerificationDecision decision, String remarks, Long officerId) {
        Application application = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", officerId));

        ApplicationStatus nextStatus = (decision == VerificationDecision.VERIFIED)
                ? ApplicationStatus.SAG_APPROVED
                : ApplicationStatus.SAG_REJECTED;

        application = applicationRepository.save(application);
        applicationService.updateStatus(applicationId, nextStatus, remarks, officerId);

        VerificationRecord record = VerificationRecord.builder()
                .application(application)
                .officer(officer)
                .decision(decision)
                .rejectionReason(decision == VerificationDecision.REJECTED ? remarks : null)
                .officerNotes(remarks)
                .build();
        verificationRepository.save(record);

        auditLogService.logAction(officer.getEmail(), officer.getRole().name(),
                decision == VerificationDecision.VERIFIED ? "APPLICATION_APPROVED" : "APPLICATION_REJECTED",
                "Application", application.getId().toString(), null, nextStatus.name());

        return application;
    }

    @Transactional(readOnly = true)
    public List<VerificationRecord> getHistoryForApplication(Long applicationId) {
        return verificationRepository.findByApplicationIdOrderByVerifiedAtDesc(applicationId);
    }
}
