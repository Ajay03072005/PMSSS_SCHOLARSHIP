package com.pmsss.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.document.repository.DocumentExtractionRepository;
import com.pmsss.ocr.model.OcrExtractedData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentExtractionServiceImpl implements DocumentExtractionService {

    private final DocumentExtractionRepository extractionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public DocumentExtraction processAndSaveExtraction(Document document, OcrExtractedData ocrData) {
        log.info("Saving OCR document extraction for document uniqueId: {}", document.getUniqueId());

        // Cleanup any existing extraction for this document
        extractionRepository.deleteByDocumentId(document.getId());

        // Build document-specific JSON payload
        Map<String, Object> jsonMap = new HashMap<>();
        if (ocrData != null) {
            if (ocrData.getName() != null) jsonMap.put("name", ocrData.getName());
            if (ocrData.getDateOfBirth() != null) jsonMap.put("dob", ocrData.getDateOfBirth().toString());
            if (ocrData.getCertificateNumber() != null) jsonMap.put("documentNumber", ocrData.getCertificateNumber());
            if (ocrData.getIncome() != null) jsonMap.put("annualIncome", ocrData.getIncome());
            if (ocrData.getDocumentDate() != null) jsonMap.put("issueDate", ocrData.getDocumentDate().toString());
            if (ocrData.getIfscCode() != null) jsonMap.put("ifscCode", ocrData.getIfscCode());
            if (ocrData.getBankAccountNumber() != null) jsonMap.put("accountNumber", ocrData.getBankAccountNumber());
            if (ocrData.getInstitution() != null) jsonMap.put("institution", ocrData.getInstitution());
            if (ocrData.getCourse() != null) jsonMap.put("course", ocrData.getCourse());
            jsonMap.put("documentType", document.getDocumentType().name());
        }

        String jsonText = "{}";
        try {
            jsonText = objectMapper.writeValueAsString(jsonMap);
        } catch (Exception e) {
            log.warn("Failed to serialize extracted data JSON: {}", e.getMessage());
        }

        double confidence = (ocrData != null && ocrData.getConfidence() != null) ? ocrData.getConfidence() : 0.80;
        String status = confidence >= 0.50 ? "SUCCESS" : "LOW_CONFIDENCE";

        DocumentExtraction extraction = DocumentExtraction.builder()
                .uniqueId("EXT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .document(document)
                .extractionStatus(status)
                .extractedName(ocrData != null ? ocrData.getName() : null)
                .extractedDob(ocrData != null ? ocrData.getDateOfBirth() : null)
                .extractedDocumentNumber(ocrData != null ? ocrData.getCertificateNumber() : null)
                .extractedIncome(ocrData != null ? ocrData.getIncome() : null)
                .extractedIssueDate(ocrData != null ? ocrData.getDocumentDate() : null)
                .extractedData(jsonText)
                .ocrConfidence(confidence)
                .extractedAt(LocalDateTime.now())
                .build();

        return extractionRepository.save(extraction);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentExtraction getExtractionForDocument(Long documentId) {
        return extractionRepository.findByDocumentId(documentId).orElse(null);
    }
}
