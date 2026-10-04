package com.pmsss.document.service;

import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.document.entity.DocumentMatchResult;
import com.pmsss.document.repository.DocumentMatchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentMatchingServiceImpl implements DocumentMatchingService {

    private final DocumentMatchResultRepository matchResultRepository;

    @Override
    @Transactional
    public List<DocumentMatchResult> matchDocumentWithApplication(Document document, Application application, DocumentExtraction extraction) {
        log.info("Performing OCR matching for document {} on application {}", document.getUniqueId(), application.getUniqueId());

        // Cleanup existing match results for this document
        matchResultRepository.deleteByDocumentId(document.getId());

        List<DocumentMatchResult> matchResults = new ArrayList<>();

        // 1. Candidate Name Comparison
        String appName = getFullName(application);
        String extName = extraction != null ? extraction.getExtractedName() : null;
        matchResults.add(compareField(document, application, "CANDIDATE_NAME", appName, extName));

        // 2. Date of Birth Comparison
        String appDob = application.getDateOfBirth() != null ? application.getDateOfBirth().toString() : null;
        String extDob = (extraction != null && extraction.getExtractedDob() != null) ? extraction.getExtractedDob().toString() : null;
        matchResults.add(compareField(document, application, "DATE_OF_BIRTH", appDob, extDob));

        // 3. Document Type Specific Field Comparisons
        DocumentType docType = document.getDocumentType();

        if (docType == DocumentType.INCOME_CERT) {
            String appIncome = application.getAnnualIncome() != null ? application.getAnnualIncome().toString() : null;
            String extIncome = (extraction != null && extraction.getExtractedIncome() != null) ? extraction.getExtractedIncome().toString() : null;
            matchResults.add(compareField(document, application, "FAMILY_INCOME", appIncome, extIncome));

            String extCertNo = extraction != null ? extraction.getExtractedDocumentNumber() : null;
            matchResults.add(compareField(document, application, "INCOME_CERTIFICATE_NO", "REQUIRED_VALID_NO", extCertNo));
        } else if (docType == DocumentType.AADHAR) {
            String appAadhaar = application.getAadhar();
            String extAadhaar = extraction != null ? extraction.getExtractedDocumentNumber() : null;
            matchResults.add(compareField(document, application, "AADHAAR_NUMBER", appAadhaar, extAadhaar));
        } else if (docType == DocumentType.BANK_PASSBOOK) {
            String appAccount = application.getAccountNumber();
            String extAccount = extraction != null ? extraction.getExtractedData() : null;
            String appIfsc = application.getIfscCode();
            matchResults.add(compareField(document, application, "BANK_ACCOUNT_NO", appAccount, extAccount));
            matchResults.add(compareField(document, application, "IFSC_CODE", appIfsc, extAccount));
        } else if (docType == DocumentType.TWELFTH_MARKSHEET || docType == DocumentType.TENTH_MARKSHEET) {
            String appRoll = application.getAcademicInfo();
            String extRoll = extraction != null ? extraction.getExtractedDocumentNumber() : null;
            matchResults.add(compareField(document, application, "MARKSHEET_ROLL_NO", appRoll, extRoll));
        }

        return matchResultRepository.saveAll(matchResults);
    }

    private DocumentMatchResult compareField(Document document, Application application, String fieldName, String appVal, String extVal) {
        String matchStatus;
        double similarityScore = 0.0;
        String mismatchReason = null;

        if (extVal == null || extVal.isBlank()) {
            matchStatus = "NOT_AVAILABLE";
            similarityScore = 0.0;
            mismatchReason = "Field not extracted by OCR engine";
        } else if (appVal == null || appVal.isBlank()) {
            matchStatus = "NOT_APPLICABLE";
            similarityScore = 100.0;
        } else {
            similarityScore = calculateSimilarity(appVal, extVal);
            if (similarityScore >= 95.0) {
                matchStatus = "MATCH";
            } else if (similarityScore >= 65.0) {
                matchStatus = "PARTIAL_MATCH";
                mismatchReason = "Minor character or formatting difference (" + String.format("%.1f", similarityScore) + "% match)";
            } else {
                matchStatus = "MISMATCH";
                mismatchReason = "Value does not match application record (" + String.format("%.1f", similarityScore) + "% match)";
            }
        }

        return DocumentMatchResult.builder()
                .uniqueId("DMR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .document(document)
                .application(application)
                .fieldName(fieldName)
                .applicationValue(appVal)
                .extractedValue(extVal)
                .matchStatus(matchStatus)
                .similarityScore(similarityScore)
                .mismatchReason(mismatchReason)
                .build();
    }

    private String getFullName(Application application) {
        if (application == null) return "";
        String first = application.getFirstName() != null ? application.getFirstName().trim() : "";
        String last = application.getLastName() != null ? application.getLastName().trim() : "";
        return (first + " " + last).trim();
    }

    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String str1 = s1.trim().toLowerCase();
        String str2 = s2.trim().toLowerCase();

        if (str1.equals(str2)) return 100.0;
        if (str1.contains(str2) || str2.contains(str1)) return 85.0;

        int distance = levenshteinDistance(str1, str2);
        int maxLen = Math.max(str1.length(), str2.length());
        if (maxLen == 0) return 100.0;

        double similarity = (1.0 - ((double) distance / maxLen)) * 100.0;
        return Math.max(0.0, Math.min(100.0, similarity));
    }

    private int levenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentMatchResult> getMatchResultsForDocument(Long documentId) {
        return matchResultRepository.findByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentMatchResult> getMatchResultsForApplication(Long applicationId) {
        return matchResultRepository.findByApplicationId(applicationId);
    }
}
