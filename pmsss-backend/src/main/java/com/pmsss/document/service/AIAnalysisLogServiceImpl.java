package com.pmsss.document.service;

import com.pmsss.application.entity.Application;
import com.pmsss.document.entity.AiAnalysisLog;
import com.pmsss.document.entity.Document;
import com.pmsss.document.repository.AiAnalysisLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisLogServiceImpl implements AIAnalysisLogService {

    private final AiAnalysisLogRepository logRepository;

    @Override
    @Transactional
    public AiAnalysisLog logOperation(Document document, Application application, String operationType, String provider, String status, Double confidence, Long durationMs, String errorMsg) {
        log.info("Logging AI analysis operation: {} status: {} for document: {}", operationType, status, document != null ? document.getUniqueId() : "N/A");

        AiAnalysisLog logEntry = AiAnalysisLog.builder()
                .uniqueId("AILOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .document(document)
                .application(application != null ? application : (document != null ? document.getApplication() : null))
                .operationType(operationType)
                .provider(provider)
                .processingStatus(status)
                .confidenceScore(confidence)
                .processingTimeMs(durationMs)
                .errorMessage(errorMsg)
                .processedAt(LocalDateTime.now())
                .build();

        return logRepository.save(logEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiAnalysisLog> getLogsForDocument(Long documentId) {
        return logRepository.findByDocumentIdOrderByProcessedAtDesc(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiAnalysisLog> getLogsForApplication(Long applicationId) {
        return logRepository.findByApplicationIdOrderByProcessedAtDesc(applicationId);
    }
}
