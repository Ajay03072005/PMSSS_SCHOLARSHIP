package com.pmsss.ai;

import com.pmsss.ai.model.AiCompletenessResult;
import com.pmsss.ai.model.AiDuplicateResult;
import com.pmsss.ai.model.AiEligibilityResult;
import com.pmsss.ai.model.NaturalLanguageQueryResponse;
import com.pmsss.ai.provider.LocalRuleBasedAiProvider;
import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.document.entity.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AiServiceTest {

    private LocalRuleBasedAiProvider aiProvider;

    @BeforeEach
    void setUp() {
        aiProvider = new LocalRuleBasedAiProvider();
    }

    @Test
    void testEligibilityEligibleStudent() {
        // J&K domicile, income <= 8 LPA, 12th marks >= 60%
        AiEligibilityResult result = aiProvider.evaluateEligibility(
                "OBC",
                new BigDecimal("250000.00"),
                "B.Tech Computer Science",
                "IIT Delhi",
                "J&K",
                82.5
        );

        assertEquals("POTENTIALLY_ELIGIBLE", result.getStatus());
        assertTrue(result.getFailedCriteria().isEmpty());
        assertTrue(result.getSatisfiedCriteria().size() >= 3);
        assertNotNull(result.getDisclaimer());
    }

    @Test
    void testEligibilityIneligibleExcessiveIncome() {
        // Income > 8 LPA -> Not eligible
        AiEligibilityResult result = aiProvider.evaluateEligibility(
                "GENERAL",
                new BigDecimal("950000.00"),
                "B.Tech",
                "NIT Srinagar",
                "J&K",
                75.0
        );

        assertEquals("POTENTIALLY_NOT_ELIGIBLE", result.getStatus());
        assertFalse(result.getFailedCriteria().isEmpty());
        assertTrue(result.getFailedCriteria().get(0).contains("exceeds"));
    }

    @Test
    void testCompletenessMissingDocuments() {
        Application app = Application.builder()
                .firstName("Ajay")
                .lastName("Kumar")
                .aadhar("123456789012")
                .mobile("9876543210")
                .email("ajay@pmsss.gov.in")
                .address("Srinagar")
                .district("Srinagar")
                .state("J&K")
                .pincode("190001")
                .annualIncome(new BigDecimal("300000"))
                .accountNumber("1234567890")
                .ifscCode("SBIN0001234")
                .bankName("State Bank of India")
                .declaration(true)
                .build();

        // Only upload photo
        List<Document> docs = new ArrayList<>();
        docs.add(Document.builder().documentType(DocumentType.PHOTO).build());

        AiCompletenessResult completeness = aiProvider.checkCompleteness(app, docs);

        assertFalse(completeness.isComplete());
        assertTrue(completeness.getMissingItems().stream().anyMatch(m -> m.contains("Income Certificate")));
        assertTrue(completeness.getMissingItems().stream().anyMatch(m -> m.contains("Domicile")));
    }

    @Test
    void testDuplicateDetectionSameAadhar() {
        Application app1 = Application.builder()
                .id(1L)
                .applicationId("PMSSS2026001")
                .firstName("Ajay")
                .lastName("Kumar")
                .aadhar("123456789012")
                .accountNumber("9999888811")
                .dateOfBirth(LocalDate.of(2003, 1, 1))
                .build();

        Application app2 = Application.builder()
                .id(2L)
                .applicationId("PMSSS2026002")
                .firstName("Ajay")
                .lastName("Kumar")
                .aadhar("123456789012") // duplicate Aadhaar
                .accountNumber("9999888822")
                .dateOfBirth(LocalDate.of(2003, 1, 1))
                .build();

        AiDuplicateResult dup = aiProvider.checkDuplicates(app2, List.of(app1, app2));

        assertTrue(dup.isPossibleDuplicate());
        assertTrue(dup.getConfidence() >= 0.90);
        assertTrue(dup.getDuplicateWithApplicationIds().contains("PMSSS2026001"));
    }

    @Test
    void testNaturalLanguageAnalyticsPendingVerification() {
        Map<String, Object> stats = Map.of(
                "total_applications", 150L,
                "pending_applications", 42L,
                "approved_applications", 90L,
                "rejected_applications", 18L
        );

        NaturalLanguageQueryResponse res = aiProvider.executeNaturalLanguageAnalytics(
                "How many applications are pending verification?", stats
        );

        assertEquals("PENDING_VERIFICATION_COUNT", res.getIdentifiedIntent());
        assertTrue(res.getAnswer().contains("42"));
    }

    @Test
    void testStatusExplanation() {
        String explanation = aiProvider.explainStatus(ApplicationStatus.DOCUMENT_VERIFICATION, null);
        assertNotNull(explanation);
        assertTrue(explanation.contains("Verification Officer"));
    }
}
