package com.pmsss.ai.service;

import com.pmsss.ai.entity.AnomalyAlertEntity;
import com.pmsss.ai.model.*;
import com.pmsss.ai.provider.AiProvider;
import com.pmsss.ai.repository.AnomalyAlertRepository;
import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtractedData;
import com.pmsss.document.repository.DocumentExtractedDataRepository;
import com.pmsss.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final AiProvider aiProvider;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final DocumentExtractedDataRepository extractedDataRepository;
    private final AnomalyAlertRepository anomalyAlertRepository;

    @Transactional(readOnly = true)
    public AiCompletenessResult checkCompleteness(String applicationId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        List<Document> docs = documentRepository.findByApplicationId(app.getId());
        return aiProvider.checkCompleteness(app, docs);
    }

    @Transactional
    public AiConsistencyMatchResult.Summary checkConsistency(String applicationId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        List<DocumentExtractedData> extracted = extractedDataRepository.findByApplicationId(app.getId());
        AiConsistencyMatchResult.Summary summary = aiProvider.checkConsistency(app, extracted);

        // Update application anomaly flag if consistency issues found
        if (!summary.isOverallConsistent()) {
            app.setHasAnomalies(true);
            applicationRepository.save(app);
        }
        return summary;
    }

    @Transactional(readOnly = true)
    public AiDuplicateResult checkDuplicates(String applicationId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        List<Application> all = applicationRepository.findAll();
        return aiProvider.checkDuplicates(app, all);
    }

    @Transactional
    public List<AiAnomalyAlert> scanForAnomalies(String applicationId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        List<Application> all = applicationRepository.findAll();

        List<AiAnomalyAlert> alerts = aiProvider.detectAnomalies(app, all);

        // Persist alerts to database
        for (AiAnomalyAlert alert : alerts) {
            AnomalyAlertEntity entity = AnomalyAlertEntity.builder()
                    .applicationId(alert.getApplicationId())
                    .studentName(alert.getStudentName())
                    .anomalyType(alert.getAnomalyType())
                    .reason(alert.getReason())
                    .severity(alert.getSeverity())
                    .isResolved(false)
                    .build();
            anomalyAlertRepository.save(entity);
        }

        if (!alerts.isEmpty()) {
            app.setHasAnomalies(true);
            applicationRepository.save(app);
        }

        return alerts;
    }

    @Transactional(readOnly = true)
    public AiEligibilityResult evaluateEligibility(String category, BigDecimal income, String course, String institution, String state, Double percentage12th) {
        return aiProvider.evaluateEligibility(category, income, course, institution, state, percentage12th);
    }

    @Transactional(readOnly = true)
    public AiChatbotResponse chat(String message, Long userId) {
        String studentContext = "";
        if (userId != null) {
            var myApps = applicationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            if (!myApps.isEmpty()) {
                Application latest = myApps.get(0);
                studentContext = "Your latest application (" + latest.getApplicationId() + ") is currently in status: " +
                        latest.getStatus().name() + ". " + aiProvider.explainStatus(latest.getStatus(), latest.getReviewRemarks());
            }
        }
        return aiProvider.processChatbotQuery(message, studentContext);
    }

    @Transactional(readOnly = true)
    public String explainStatus(ApplicationStatus status, String rejectionReason) {
        return aiProvider.explainStatus(status, rejectionReason);
    }

    @Transactional(readOnly = true)
    public List<AiReviewQueueItem> getSmartReviewQueue() {
        List<Application> pending = applicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SUBMITTED || a.getStatus() == ApplicationStatus.DOCUMENT_VERIFICATION)
                .collect(Collectors.toList());

        List<AiReviewQueueItem> items = new ArrayList<>();

        for (Application app : pending) {
            List<String> attentionReasons = new ArrayList<>();
            int score = 40; // baseline

            // Waiting time calculation
            long waitingDays = 0;
            if (app.getSubmittedAt() != null) {
                waitingDays = Duration.between(app.getSubmittedAt(), LocalDateTime.now()).toDays();
                if (waitingDays > 7) {
                    score += 25;
                    attentionReasons.add("Waiting over " + waitingDays + " days for verification");
                }
            }

            // Inconsistency / Anomaly flag
            if (Boolean.TRUE.equals(app.getHasAnomalies())) {
                score += 30;
                attentionReasons.add("Flagged for data inconsistency or duplicate pattern");
            }

            // Completeness check
            List<Document> docs = documentRepository.findByApplicationId(app.getId());
            AiCompletenessResult comp = aiProvider.checkCompleteness(app, docs);
            if (!comp.isComplete()) {
                score += 15;
                attentionReasons.add("Incomplete application: " + comp.getMissingItems().size() + " required items missing");
            }

            score = Math.min(100, score);
            String attentionLevel = score >= 75 ? "HIGH_ATTENTION" : (score >= 50 ? "NORMAL" : "LOW_ATTENTION");

            items.add(AiReviewQueueItem.builder()
                    .applicationId(app.getApplicationId())
                    .studentName(app.getFirstName() + " " + app.getLastName())
                    .category(app.getCategory())
                    .status(app.getStatus())
                    .attentionLevel(attentionLevel)
                    .priorityScore(score)
                    .waitingDays(waitingDays)
                    .attentionReasons(attentionReasons)
                    .submittedAt(app.getSubmittedAt())
                    .build());
        }

        // Sort queue by priority score descending
        items.sort((a, b) -> Integer.compare(b.getPriorityScore(), a.getPriorityScore()));
        return items;
    }

    @Transactional(readOnly = true)
    public NaturalLanguageQueryResponse runNaturalLanguageAnalytics(String query) {
        long totalApps = applicationRepository.count();
        long pending = applicationRepository.countByStatus(ApplicationStatus.SUBMITTED) + applicationRepository.countByStatus(ApplicationStatus.DOCUMENT_VERIFICATION);
        long approved = applicationRepository.countByStatus(ApplicationStatus.SAG_APPROVED) + applicationRepository.countByStatus(ApplicationStatus.COMPLETED);
        long rejected = applicationRepository.countByStatus(ApplicationStatus.SAG_REJECTED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_applications", totalApps);
        stats.put("pending_applications", pending);
        stats.put("approved_applications", approved);
        stats.put("rejected_applications", rejected);

        return aiProvider.executeNaturalLanguageAnalytics(query, stats);
    }
}
