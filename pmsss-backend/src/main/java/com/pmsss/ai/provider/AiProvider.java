package com.pmsss.ai.provider;

import com.pmsss.ai.model.*;
import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtractedData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AiProvider {
    AiCompletenessResult checkCompleteness(Application application, List<Document> documents);
    AiConsistencyMatchResult.Summary checkConsistency(Application application, List<DocumentExtractedData> extractedData);
    AiDuplicateResult checkDuplicates(Application application, List<Application> allApplications);
    List<AiAnomalyAlert> detectAnomalies(Application application, List<Application> allApplications);
    AiEligibilityResult evaluateEligibility(String category, BigDecimal income, String course, String institution, String state, Double percentage12th);
    AiChatbotResponse processChatbotQuery(String query, String studentContext);
    String explainStatus(ApplicationStatus status, String rejectionReason);
    NaturalLanguageQueryResponse executeNaturalLanguageAnalytics(String query, Map<String, Object> systemStats);
}
