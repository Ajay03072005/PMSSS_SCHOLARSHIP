package com.pmsss.document.service;

import com.pmsss.application.entity.Application;
import com.pmsss.document.entity.AiAnalysisLog;
import com.pmsss.document.entity.Document;

import java.util.List;

public interface AIAnalysisLogService {
    AiAnalysisLog logOperation(Document document, Application application, String operationType, String provider, String status, Double confidence, Long durationMs, String errorMsg);
    List<AiAnalysisLog> getLogsForDocument(Long documentId);
    List<AiAnalysisLog> getLogsForApplication(Long applicationId);
}
