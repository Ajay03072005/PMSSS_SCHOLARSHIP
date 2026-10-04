package com.pmsss.document.service;

import com.pmsss.common.enums.DocumentStatus;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.document.entity.DocumentMatchResult;
import com.pmsss.document.entity.DocumentVerification;
import com.pmsss.document.repository.DocumentRepository;
import com.pmsss.document.repository.DocumentVerificationRepository;
import com.pmsss.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DocumentVerificationServiceImpl.class);


    private static final double CONFIDENCE_THRESHOLD = 0.75; // Configurable OCR confidence threshold (75%)
    private final DocumentVerificationRepository verificationRepository;
    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public DocumentVerification evaluateAndCreateVerification(Document document, DocumentExtraction extraction, List<DocumentMatchResult> matchResults) {
        log.info("Evaluating AI-assisted verification for document uniqueId: {}", document.getUniqueId());

        double confidence = extraction != null && extraction.getOcrConfidence() != null ? extraction.getOcrConfidence() : 0.70;
        boolean hasMismatch = matchResults != null && matchResults.stream().anyMatch(m -> "MISMATCH".equalsIgnoreCase(m.getMatchStatus()));

        String verificationStatus;
        String notes;

        if (confidence >= CONFIDENCE_THRESHOLD && !hasMismatch) {
            verificationStatus = "VERIFIED";
            notes = "AI-assisted verification passed. High OCR confidence (" + String.format("%.0f", confidence * 100) + "%) and data consistency confirmed.";
            document.setVerificationStatus(DocumentStatus.VERIFIED);
        } else {
            verificationStatus = "NEEDS_REVIEW";
            if (hasMismatch) {
                notes = "Field mismatch detected during document comparison. Routed to SAG Officer for manual review.";
            } else {
                notes = "Low OCR confidence (" + String.format("%.0f", confidence * 100) + "% < " + String.format("%.0f", CONFIDENCE_THRESHOLD * 100) + "% threshold). Routed to SAG Officer for manual review.";
            }
            document.setVerificationStatus(DocumentStatus.PROCESSING);
        }

        documentRepository.save(document);

        DocumentVerification verification = DocumentVerification.builder()
                .uniqueId("VER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .document(document)
                .verificationStatus(verificationStatus)
                .verificationMethod("AI_ASSISTED")
                .confidenceScore(confidence)
                .verificationNotes(notes)
                .verifiedAt(LocalDateTime.now())
                .build();

        return verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public DocumentVerification manualVerification(Long documentId, String status, String notes, User officer) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        DocumentStatus docStatus = "VERIFIED".equalsIgnoreCase(status) ? DocumentStatus.VERIFIED : DocumentStatus.REJECTED;
        document.setVerificationStatus(docStatus);
        if ("REJECTED".equalsIgnoreCase(status)) {
            document.setRejectionReason(notes);
        }
        documentRepository.save(document);

        DocumentVerification verification = DocumentVerification.builder()
                .uniqueId("VER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .document(document)
                .verificationStatus(status.toUpperCase())
                .verificationMethod("MANUAL")
                .confidenceScore(1.0)
                .verifiedBy(officer)
                .verificationNotes(notes)
                .verifiedAt(LocalDateTime.now())
                .build();

        return verificationRepository.save(verification);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVerification getLatestVerificationForDocument(Long documentId) {
        return verificationRepository.findTopByDocumentIdOrderByVerifiedAtDesc(documentId).orElse(null);
    }
}
