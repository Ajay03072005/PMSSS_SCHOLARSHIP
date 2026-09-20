package com.pmsss.ai.provider;

import com.pmsss.ai.model.*;
import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.AnomalySeverity;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtractedData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class LocalRuleBasedAiProvider implements AiProvider {

    private static final BigDecimal MAX_INCOME_LIMIT = new BigDecimal("800000.00"); // 8 Lakhs

    @Override
    public AiCompletenessResult checkCompleteness(Application application, List<Document> documents) {
        List<String> missing = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Check mandatory personal fields
        if (isEmpty(application.getFirstName())) missing.add("First Name");
        if (isEmpty(application.getLastName())) missing.add("Last Name");
        if (isEmpty(application.getAadhar()) || application.getAadhar().length() != 12) missing.add("Valid 12-digit Aadhar Number");
        if (isEmpty(application.getMobile()) || application.getMobile().length() != 10) missing.add("Valid 10-digit Mobile Number");
        if (isEmpty(application.getEmail())) missing.add("Email Address");
        if (isEmpty(application.getAddress())) missing.add("Residential Address");
        if (isEmpty(application.getDistrict())) missing.add("District");
        if (isEmpty(application.getState())) missing.add("State (J&K or Ladakh)");
        if (isEmpty(application.getPincode())) missing.add("Pincode");

        // Family details
        if (application.getAnnualIncome() == null) {
            missing.add("Family Annual Income");
        } else if (application.getAnnualIncome().compareTo(MAX_INCOME_LIMIT) > 0) {
            warnings.add("Annual income (" + application.getAnnualIncome() + ") exceeds standard PMSSS limit of ₹8,00,000.");
        }

        // Bank details
        if (isEmpty(application.getAccountNumber())) missing.add("Bank Account Number");
        if (isEmpty(application.getIfscCode())) missing.add("Bank IFSC Code");
        if (isEmpty(application.getBankName())) missing.add("Bank Name");

        // Mandatory Documents
        Set<DocumentType> uploadedTypes = new HashSet<>();
        if (documents != null) {
            for (Document doc : documents) {
                uploadedTypes.add(doc.getDocumentType());
            }
        }
        // Direct field checks
        if (application.getPhoto() != null) uploadedTypes.add(DocumentType.PHOTO);
        if (application.getAadharDoc() != null) uploadedTypes.add(DocumentType.AADHAR);
        if (application.getDomicile() != null) uploadedTypes.add(DocumentType.DOMICILE);
        if (application.getIncomeCert() != null) uploadedTypes.add(DocumentType.INCOME_CERT);
        if (application.getTenthMarksheet() != null) uploadedTypes.add(DocumentType.TENTH_MARKSHEET);
        if (application.getTwelfthMarksheet() != null) uploadedTypes.add(DocumentType.TWELFTH_MARKSHEET);
        if (application.getAdmissionLetter() != null) uploadedTypes.add(DocumentType.ADMISSION_LETTER);
        if (application.getBankPassbook() != null) uploadedTypes.add(DocumentType.BANK_PASSBOOK);

        DocumentType[] mandatory = {
                DocumentType.PHOTO,
                DocumentType.AADHAR,
                DocumentType.DOMICILE,
                DocumentType.INCOME_CERT,
                DocumentType.TENTH_MARKSHEET,
                DocumentType.TWELFTH_MARKSHEET,
                DocumentType.ADMISSION_LETTER,
                DocumentType.BANK_PASSBOOK
        };

        for (DocumentType type : mandatory) {
            if (!uploadedTypes.contains(type)) {
                missing.add("Document: " + formatDocName(type));
            }
        }

        if (!Boolean.TRUE.equals(application.getDeclaration())) {
            missing.add("Student Undertaking & Declaration");
        }

        int totalChecks = 20;
        int passed = Math.max(0, totalChecks - missing.size());
        int percentage = (passed * 100) / totalChecks;

        return AiCompletenessResult.builder()
                .complete(missing.isEmpty())
                .missingItems(missing)
                .warnings(warnings)
                .completenessPercentage(percentage)
                .build();
    }

    @Override
    public AiConsistencyMatchResult.Summary checkConsistency(Application app, List<DocumentExtractedData> extractedDataList) {
        List<AiConsistencyMatchResult> fieldMatches = new ArrayList<>();
        List<String> discrepancies = new ArrayList<>();

        String appFullName = (app.getFirstName() + " " + (app.getMiddleName() != null ? app.getMiddleName() + " " : "") + app.getLastName()).trim();

        if (extractedDataList != null) {
            for (DocumentExtractedData ext : extractedDataList) {
                // Name check
                if (ext.getExtractedName() != null && !ext.getExtractedName().isEmpty()) {
                    double similarity = calculateSimilarity(appFullName.toLowerCase(), ext.getExtractedName().toLowerCase());
                    String status = similarity >= 0.85 ? "MATCH" : (similarity >= 0.5 ? "POTENTIAL_MISMATCH" : "MISMATCH");
                    if (!"MATCH".equals(status)) {
                        discrepancies.add("Name mismatch: Application says '" + appFullName + "' but " + ext.getDocument().getDocumentType() + " reads '" + ext.getExtractedName() + "'");
                    }
                    fieldMatches.add(AiConsistencyMatchResult.builder()
                            .applicationField("Student Name (" + ext.getDocument().getDocumentType() + ")")
                            .applicationValue(appFullName)
                            .documentExtractedValue(ext.getExtractedName())
                            .matchStatus(status)
                            .confidence(similarity)
                            .notes(similarity >= 0.85 ? "High confidence name match" : "Potential name variance detected. Verification officer review required.")
                            .build());
                }

                // Income check
                if (ext.getExtractedIncome() != null && app.getAnnualIncome() != null) {
                    boolean matches = ext.getExtractedIncome().compareTo(app.getAnnualIncome()) == 0;
                    String status = matches ? "MATCH" : "POTENTIAL_MISMATCH";
                    if (!matches) {
                        discrepancies.add("Income mismatch: Stated ₹" + app.getAnnualIncome() + " but Income Certificate reads ₹" + ext.getExtractedIncome());
                    }
                    fieldMatches.add(AiConsistencyMatchResult.builder()
                            .applicationField("Annual Income")
                            .applicationValue("₹" + app.getAnnualIncome())
                            .documentExtractedValue("₹" + ext.getExtractedIncome())
                            .matchStatus(status)
                            .confidence(matches ? 0.98 : 0.65)
                            .notes(matches ? "Income matches certificate exact figure" : "Declared income differs from extracted certificate value.")
                            .build());
                }

                // Bank Account check
                if (ext.getBankAccountNumber() != null && app.getAccountNumber() != null) {
                    boolean matches = ext.getBankAccountNumber().equals(app.getAccountNumber());
                    String status = matches ? "MATCH" : "POTENTIAL_MISMATCH";
                    fieldMatches.add(AiConsistencyMatchResult.builder()
                            .applicationField("Bank Account Number")
                            .applicationValue(app.getAccountNumber())
                            .documentExtractedValue(ext.getBankAccountNumber())
                            .matchStatus(status)
                            .confidence(matches ? 0.99 : 0.60)
                            .notes(matches ? "Account number matches passbook" : "Bank account mismatch detected")
                            .build());
                }
            }
        }

        double avgConfidence = fieldMatches.stream().mapToDouble(AiConsistencyMatchResult::getConfidence).average().orElse(1.0);

        return AiConsistencyMatchResult.Summary.builder()
                .applicationId(app.getApplicationId())
                .overallConsistent(discrepancies.isEmpty())
                .averageConfidence(avgConfidence)
                .fieldMatches(fieldMatches)
                .flaggedDiscrepancies(discrepancies)
                .build();
    }

    @Override
    public AiDuplicateResult checkDuplicates(Application app, List<Application> allApplications) {
        List<String> matched = new ArrayList<>();
        List<String> dupIds = new ArrayList<>();
        double confidence = 0.0;

        for (Application other : allApplications) {
            if (other.getId().equals(app.getId())) continue;

            // Check Aadhaar match
            if (app.getAadhar() != null && app.getAadhar().equals(other.getAadhar())) {
                matched.add("Aadhaar Number (" + app.getAadhar() + ")");
                dupIds.add(other.getApplicationId());
                confidence = Math.max(confidence, 0.98);
            }

            // Check Bank Account match
            if (app.getAccountNumber() != null && app.getAccountNumber().equals(other.getAccountNumber())) {
                matched.add("Bank Account (" + app.getAccountNumber() + ")");
                dupIds.add(other.getApplicationId());
                confidence = Math.max(confidence, 0.92);
            }

            // Check Name + DOB
            if (app.getFirstName().equalsIgnoreCase(other.getFirstName()) &&
                    app.getLastName().equalsIgnoreCase(other.getLastName()) &&
                    app.getDateOfBirth().equals(other.getDateOfBirth())) {
                matched.add("Name and Date of Birth");
                dupIds.add(other.getApplicationId());
                confidence = Math.max(confidence, 0.88);
            }
        }

        boolean isDup = !dupIds.isEmpty();
        return AiDuplicateResult.builder()
                .possibleDuplicate(isDup)
                .confidence(isDup ? confidence : 0.0)
                .matchedFields(matched)
                .duplicateWithApplicationIds(dupIds)
                .details(isDup ? "Potential duplicate identified based on: " + String.join(", ", matched) : "No duplicate application found.")
                .build();
    }

    @Override
    public List<AiAnomalyAlert> detectAnomalies(Application app, List<Application> allApplications) {
        List<AiAnomalyAlert> alerts = new ArrayList<>();

        // 1. Same bank account across different users
        long bankCollisions = allApplications.stream()
                .filter(a -> !a.getId().equals(app.getId()) &&
                        a.getAccountNumber() != null &&
                        a.getAccountNumber().equals(app.getAccountNumber()))
                .count();

        if (bankCollisions > 0) {
            alerts.add(AiAnomalyAlert.builder()
                    .id(UUID.randomUUID().toString())
                    .applicationId(app.getApplicationId())
                    .studentName(app.getFirstName() + " " + app.getLastName())
                    .anomalyType("BANK_ACCOUNT_REUSE")
                    .reason("Same bank account (" + app.getAccountNumber() + ") is used across " + (bankCollisions + 1) + " applications.")
                    .severity(bankCollisions > 1 ? AnomalySeverity.HIGH : AnomalySeverity.MEDIUM)
                    .detectedAt(LocalDateTime.now())
                    .resolved(false)
                    .build());
        }

        // 2. High Income but General Category applying for need-based quota
        if (app.getAnnualIncome() != null && app.getAnnualIncome().compareTo(new BigDecimal("750000")) > 0) {
            alerts.add(AiAnomalyAlert.builder()
                    .id(UUID.randomUUID().toString())
                    .applicationId(app.getApplicationId())
                    .studentName(app.getFirstName() + " " + app.getLastName())
                    .anomalyType("BORDERLINE_INCOME")
                    .reason("Annual family income (₹" + app.getAnnualIncome() + ") is very close to the ₹8,00,000 threshold ceiling.")
                    .severity(AnomalySeverity.LOW)
                    .detectedAt(LocalDateTime.now())
                    .resolved(false)
                    .build());
        }

        return alerts;
    }

    @Override
    public AiEligibilityResult evaluateEligibility(String category, BigDecimal income, String course, String institution, String state, Double percentage12th) {
        List<String> satisfied = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> guidelines = new ArrayList<>();

        guidelines.add("Candidate must be a permanent resident (domicile) of UT of J&K or Ladakh.");
        guidelines.add("Family income from all sources must not exceed ₹8.00 Lakh per annum.");
        guidelines.add("Must have passed 10+2 examination with at least 60% aggregate marks.");
        guidelines.add("Admission must be secured in recognized AICTE / UGC approved colleges outside J&K / Ladakh.");

        // Domicile Check
        if (state == null || state.isEmpty()) {
            missing.add("Domicile State (must be J&K or Ladakh)");
        } else if (state.equalsIgnoreCase("jk") || state.equalsIgnoreCase("j&k") ||
                state.toLowerCase().contains("j&k") || state.equalsIgnoreCase("ladakh") ||
                state.toLowerCase().contains("jammu") || state.toLowerCase().contains("kashmir") ||
                state.toLowerCase().contains("ladakh") ||
                state.replaceAll("[^a-zA-Z]", "").equalsIgnoreCase("jk")) {
            satisfied.add("Permanent Domicile requirement met (" + state + ")");
        } else {
            failed.add("Applicant state '" + state + "' is not eligible. Only J&K and Ladakh domiciled students qualify.");
        }

        // Income Check
        if (income == null) {
            missing.add("Family Annual Income figure");
        } else if (income.compareTo(MAX_INCOME_LIMIT) <= 0) {
            satisfied.add("Family income (₹" + income + ") is within the ₹8,00,000 limit.");
        } else {
            failed.add("Family income (₹" + income + ") exceeds the maximum permissible limit of ₹8,00,000.");
        }

        // 12th percentage
        if (percentage12th != null) {
            if (percentage12th >= 60.0) {
                satisfied.add("12th aggregate score (" + percentage12th + "%) meets the minimum 60% requirement.");
            } else {
                failed.add("12th aggregate score (" + percentage12th + "%) is below the minimum required 60%.");
            }
        } else {
            missing.add("12th class aggregate percentage");
        }

        String status;
        String summary;
        if (!failed.isEmpty()) {
            status = "POTENTIALLY_NOT_ELIGIBLE";
            summary = "Based on official guidelines, you may not satisfy one or more mandatory PMSSS criteria.";
        } else if (!missing.isEmpty()) {
            status = "MISSING_INFORMATION";
            summary = "Additional information is required to fully assess your PMSSS eligibility.";
        } else {
            status = "POTENTIALLY_ELIGIBLE";
            summary = "You appear to fulfill the baseline PMSSS criteria for scholarship consideration.";
        }

        return AiEligibilityResult.builder()
                .status(status)
                .summary(summary)
                .satisfiedCriteria(satisfied)
                .failedCriteria(failed)
                .missingInformation(missing)
                .applicableGuidelines(guidelines)
                .disclaimer("IMPORTANT: This AI assessment is strictly advisory. Official scholarship awards are subject to physical/digital document verification and merit ranking by AICTE SAG Bureau.")
                .build();
    }

    @Override
    public AiChatbotResponse processChatbotQuery(String query, String studentContext) {
        String q = query.toLowerCase();
        String reply;
        String intent;
        List<String> suggestions = new ArrayList<>();

        if (q.contains("document") || q.contains("upload") || q.contains("file")) {
            intent = "DOCUMENTS_REQUIRED";
            reply = "📄 **Mandatory PMSSS Documents:**\n" +
                    "1. Passport Photo (Max 500KB, JPG/PNG)\n" +
                    "2. Aadhar Card (Both sides)\n" +
                    "3. Domicile Certificate of J&K / Ladakh\n" +
                    "4. Income Certificate (Issued by competent authority, annual income < ₹8 Lakh)\n" +
                    "5. 10th Class Marksheet\n" +
                    "6. 12th Class Marksheet (Min 60%)\n" +
                    "7. College Admission Allotment Letter\n" +
                    "8. Bank Passbook / Statement (Applicant's name & IFSC clearly visible)";
            suggestions.add("How do I upload documents?");
            suggestions.add("What is the income certificate validity?");
        } else if (q.contains("eligib") || q.contains("criteria") || q.contains("qualify")) {
            intent = "ELIGIBILITY";
            reply = "✅ **PMSSS 2026 Eligibility Criteria:**\n" +
                    "• Must be a bona fide resident (Domicile) of J&K or Ladakh.\n" +
                    "• Passed 10+2 examination with at least 60% marks.\n" +
                    "• Family annual income must not exceed ₹8.00 Lakhs.\n" +
                    "• Admission through AICTE centralized counselling or approved allotment.";
            suggestions.add("Check my eligibility");
            suggestions.add("What courses are covered?");
        } else if (q.contains("status") || q.contains("where is my") || q.contains("track")) {
            intent = "APPLICATION_STATUS";
            reply = (studentContext != null && !studentContext.isEmpty())
                    ? "🔍 " + studentContext
                    : "You can track your application status anytime using the 'Track Application' button on the navigation bar with your Application ID and Date of Birth.";
            suggestions.add("What does DOCUMENT_VERIFICATION mean?");
            suggestions.add("When will my payment be disbursed?");
        } else if (q.contains("amount") || q.contains("money") || q.contains("disburs") || q.contains("allowance")) {
            intent = "SCHOLARSHIP_SLABS";
            reply = "💰 **PMSSS Scholarship Slabs:**\n" +
                    "• **General Degree**: Academic fee up to ₹30,000/yr\n" +
                    "• **Engineering Degree**: Academic fee up to ₹1,25,000/yr\n" +
                    "• **Medical / BDS**: Academic fee up to ₹3,00,000/yr\n" +
                    "• **Maintenance Allowance**: Up to ₹1,00,000/yr in 10 equal installments directly via DBT.";
            suggestions.add("When does maintenance allowance come?");
            suggestions.add("How is fee paid to college?");
        } else {
            intent = "GENERAL_HELP";
            reply = "Hello! I am your AI PMSSS Assistant. I can help you with eligibility criteria, required documents, application status tracking, scholarship disbursement slabs, and portal guidance. What would you like to know?";
            suggestions.add("What documents are required?");
            suggestions.add("Check eligibility");
            suggestions.add("What is my application status?");
        }

        return AiChatbotResponse.builder()
                .reply(reply)
                .intent(intent)
                .suggestedFollowUps(suggestions)
                .applicationContext(studentContext)
                .build();
    }

    @Override
    public String explainStatus(ApplicationStatus status, String rejectionReason) {
        switch (status) {
            case DRAFT:
                return "Your application is saved as draft. You can still modify any fields or uploaded files before submitting.";
            case SUBMITTED:
                return "Your application has been received and entered into the central queue for document verification.";
            case DOCUMENT_VERIFICATION:
                return "The SAG Verification Officer is currently examining your uploaded certificates (Marksheets, Domicile, Income). No action needed unless a document is returned.";
            case SAG_REVIEW:
                return "Your documents have been verified. The SAG committee is conducting final eligibility review.";
            case SAG_APPROVED:
                return "Congratulations! Your scholarship has been approved by the SAG authority and forwarded to Finance.";
            case SAG_REJECTED:
                return "Your application was rejected. Official reason: " + (rejectionReason != null ? rejectionReason : "Criteria not fulfilled") + ". You may resubmit with corrected documents.";
            case SENT_TO_FINANCE:
                return "Your sanctioned grant is with the Finance Department for bank mandate creation.";
            case PAYMENT_PROCESSING:
                return "Direct Benefit Transfer (DBT) is processing through PFMS / Reserve Bank of India.";
            case PAYMENT_COMPLETED:
            case COMPLETED:
                return "Scholarship funds have been successfully credited to your registered bank account.";
            default:
                return "Application is under active administrative processing.";
        }
    }

    @Override
    public NaturalLanguageQueryResponse executeNaturalLanguageAnalytics(String query, Map<String, Object> systemStats) {
        String q = query.toLowerCase();
        String answer;
        String intent;
        Map<String, Object> metrics = new HashMap<>();

        if (q.contains("pending") && q.contains("verification")) {
            intent = "PENDING_VERIFICATION_COUNT";
            long count = ((Number) systemStats.getOrDefault("pending_applications", 0)).longValue();
            answer = "There are currently " + count + " applications pending document verification.";
            metrics.put("pendingVerificationCount", count);
        } else if (q.contains("approved")) {
            intent = "APPROVED_COUNT";
            long count = ((Number) systemStats.getOrDefault("approved_applications", 0)).longValue();
            answer = "A total of " + count + " applications have been approved.";
            metrics.put("approvedCount", count);
        } else if (q.contains("rejected")) {
            intent = "REJECTED_COUNT";
            long count = ((Number) systemStats.getOrDefault("rejected_applications", 0)).longValue();
            answer = "A total of " + count + " applications were rejected.";
            metrics.put("rejectedCount", count);
        } else if (q.contains("total") || q.contains("how many applications")) {
            intent = "TOTAL_APPLICATIONS";
            long total = ((Number) systemStats.getOrDefault("total_applications", 0)).longValue();
            answer = "There are " + total + " total scholarship applications registered in the PMSSS 2.0 system.";
            metrics.put("totalApplications", total);
        } else {
            intent = "SUMMARY_REPORT";
            answer = "System Summary: Total Applications: " + systemStats.getOrDefault("total_applications", 0) +
                    ", Pending: " + systemStats.getOrDefault("pending_applications", 0) +
                    ", Approved: " + systemStats.getOrDefault("approved_applications", 0) +
                    ", Rejected: " + systemStats.getOrDefault("rejected_applications", 0);
            metrics.putAll(systemStats);
        }

        return NaturalLanguageQueryResponse.builder()
                .query(query)
                .identifiedIntent(intent)
                .answer(answer)
                .metrics(metrics)
                .build();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String formatDocName(DocumentType type) {
        switch (type) {
            case PHOTO: return "Passport Photograph";
            case AADHAR: return "Aadhaar Card";
            case DOMICILE: return "Domicile Certificate of J&K / Ladakh";
            case INCOME_CERT: return "Income Certificate (< ₹8 Lakhs)";
            case TENTH_MARKSHEET: return "10th Class Marksheet";
            case TWELFTH_MARKSHEET: return "12th Class Marksheet";
            case ADMISSION_LETTER: return "College Admission Letter";
            case BANK_PASSBOOK: return "Bank Passbook / Statement";
            default: return type.name();
        }
    }

    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.contains(s2) || s2.contains(s1)) return 0.90;
        int longer = Math.max(s1.length(), s2.length());
        if (longer == 0) return 1.0;
        return (longer - computeLevenshteinDistance(s1, s2)) / (double) longer;
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
