package com.pmsss.application.service;

import com.pmsss.ai.entity.EligibilityRule;
import com.pmsss.ai.repository.EligibilityRuleRepository;
import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.verification.entity.CorrectionRequest;
import com.pmsss.verification.repository.CorrectionRequestRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationValidationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationValidationService.class);

    private final ApplicationRepository applicationRepository;
    private final CorrectionRequestRepository correctionRequestRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final NotificationService notificationService;

    public static class PreValidationResult {
        private boolean passed;
        private List<String> issues;
        private String evaluatedRuleCode;
        private boolean eligibilityPassed;

        public PreValidationResult() {}

        public PreValidationResult(boolean passed, List<String> issues, String evaluatedRuleCode, boolean eligibilityPassed) {
            this.passed = passed;
            this.issues = issues;
            this.evaluatedRuleCode = evaluatedRuleCode;
            this.eligibilityPassed = eligibilityPassed;
        }

        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }

        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }

        public String getEvaluatedRuleCode() { return evaluatedRuleCode; }
        public void setEvaluatedRuleCode(String evaluatedRuleCode) { this.evaluatedRuleCode = evaluatedRuleCode; }

        public boolean isEligibilityPassed() { return eligibilityPassed; }
        public void setEligibilityPassed(boolean eligibilityPassed) { this.eligibilityPassed = eligibilityPassed; }

        public static PreValidationResultBuilder builder() {
            return new PreValidationResultBuilder();
        }

        public static class PreValidationResultBuilder {
            private boolean passed;
            private List<String> issues;
            private String evaluatedRuleCode;
            private boolean eligibilityPassed;

            public PreValidationResultBuilder passed(boolean passed) { this.passed = passed; return this; }
            public PreValidationResultBuilder issues(List<String> issues) { this.issues = issues; return this; }
            public PreValidationResultBuilder evaluatedRuleCode(String evaluatedRuleCode) { this.evaluatedRuleCode = evaluatedRuleCode; return this; }
            public PreValidationResultBuilder eligibilityPassed(boolean eligibilityPassed) { this.eligibilityPassed = eligibilityPassed; return this; }

            public PreValidationResult build() {
                return new PreValidationResult(passed, issues, evaluatedRuleCode, eligibilityPassed);
            }
        }
    }


    @Transactional
    public PreValidationResult performPreValidation(Application app) {
        log.info("Starting automated pre-validation for application {}", app.getApplicationId());
        List<String> issues = new ArrayList<>();

        // 1. Mandatory personal information
        if (app.getFirstName() == null || app.getFirstName().trim().isEmpty()) {
            issues.add("First name is required.");
        }
        if (app.getLastName() == null || app.getLastName().trim().isEmpty()) {
            issues.add("Last name is required.");
        }
        if (app.getDateOfBirth() == null) {
            issues.add("Date of birth is required.");
        }

        // 2. Aadhaar format (12 digits)
        if (app.getAadhar() == null || !app.getAadhar().matches("^[0-9]{12}$")) {
            issues.add("Aadhaar number must be exactly 12 digits.");
        }

        // 3. Mobile format (10 digits)
        if (app.getMobile() == null || !app.getMobile().matches("^[0-9]{10}$")) {
            issues.add("Mobile number must be a valid 10-digit number.");
        }

        // 4. Bank details
        if (app.getAccountNumber() == null || app.getAccountNumber().trim().length() < 9) {
            issues.add("Bank account number must be valid.");
        }
        if (app.getIfscCode() == null || !app.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
            issues.add("Bank IFSC code format is invalid (e.g., SBIN0001234).");
        }

        // 5. Mandatory documents
        if (app.getIncomeCert() == null || app.getIncomeCert().trim().isEmpty()) {
            issues.add("Income certificate document is mandatory.");
        }
        if (app.getAadharDoc() == null || app.getAadharDoc().trim().isEmpty()) {
            issues.add("Aadhaar document copy is mandatory.");
        }
        if (app.getTenthMarksheet() == null || app.getTenthMarksheet().trim().isEmpty()) {
            issues.add("10th Marksheet document is mandatory.");
        }

        // 6. Annual income check
        if (app.getAnnualIncome() == null || app.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {
            issues.add("Valid annual family income is required.");
        }

        // 7. Declaration
        if (app.getDeclaration() == null || !app.getDeclaration()) {
            issues.add("Application declaration must be accepted.");
        }

        // 8. Eligibility Rule Validation against database
        boolean eligibilityPassed = true;
        String evaluatedRule = "DEFAULT_PMSSS_RULE";
        List<EligibilityRule> activeRules = eligibilityRuleRepository.findByIsActiveTrue();
        if (!activeRules.isEmpty()) {
            EligibilityRule rule = activeRules.get(0);
            evaluatedRule = rule.getRuleCode();
            if (rule.getMaxAnnualIncome() != null && app.getAnnualIncome() != null) {
                if (app.getAnnualIncome().compareTo(rule.getMaxAnnualIncome()) > 0) {
                    issues.add("Annual income ₹" + app.getAnnualIncome() + " exceeds maximum ceiling of ₹" + rule.getMaxAnnualIncome());
                    eligibilityPassed = false;
                }
            }
            if (rule.getDomicileRequired() != null && rule.getDomicileRequired()) {
                if (app.getState() == null || (!app.getState().equalsIgnoreCase("jk") && !app.getState().equalsIgnoreCase("ladakh") && !app.getState().equalsIgnoreCase("jammu and kashmir"))) {
                    issues.add("Applicant state must be Jammu & Kashmir or Ladakh under PMSSS domicile guidelines.");
                    eligibilityPassed = false;
                }
            }
        }

        boolean passed = issues.isEmpty();

        if (!passed) {
            log.warn("Pre-validation failed for application {}: {} issues", app.getApplicationId(), issues.size());
            app.setStatus(ApplicationStatus.VALIDATION_FAILED);
            applicationRepository.save(app);

            // Create individual correction tickets for each issue
            for (String issue : issues) {
                CorrectionRequest cr = CorrectionRequest.builder()
                        .applicationId(app.getApplicationId())
                        .fieldKey("PRE_VALIDATION_ERROR")
                        .reason(issue)
                        .requestedBy("SYSTEM_VALIDATION_ENGINE")
                        .isResolved(false)
                        .build();
                correctionRequestRepository.save(cr);
            }

            if (app.getUser() != null) {
                notificationService.createNotification(
                        app.getUser(),
                        app,
                        "VALIDATION_FAILED",
                        "Application Pre-Validation Incomplete",
                        "Your application requires corrections before officer review: " + String.join(", ", issues)
                );
            }
        } else {
            log.info("Pre-validation passed for application {}", app.getApplicationId());
        }

        return PreValidationResult.builder()
                .passed(passed)
                .issues(issues)
                .evaluatedRuleCode(evaluatedRule)
                .eligibilityPassed(eligibilityPassed)
                .build();
    }
}
