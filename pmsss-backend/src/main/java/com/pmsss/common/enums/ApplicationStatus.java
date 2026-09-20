package com.pmsss.common.enums;

public enum ApplicationStatus {
    DRAFT,
    SUBMITTED,
    AUTOMATED_VALIDATION,
    VALIDATING,
    VALIDATION_FAILED,
    DOCUMENT_PROCESSING,
    DOCUMENT_VERIFICATION,
    DOCUMENT_REJECTED,
    NEEDS_CORRECTION,
    READY_FOR_REVIEW,
    ASSIGNED,
    UNDER_REVIEW,
    SAG_REVIEW,
    ESCALATED,
    SAG_APPROVED,
    SAG_REJECTED,
    SENT_TO_FINANCE,
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    COMPLETED;

    public static ApplicationStatus fromString(String status) {
        if (status == null) return DRAFT;
        String clean = status.trim().toUpperCase().replace(" ", "_");
        // Legacy mappings
        switch (clean) {
            case "PENDING":
            case "SUBMITTED":
                return SUBMITTED;
            case "UNDER_REVIEW":
                return DOCUMENT_VERIFICATION;
            case "VERIFIED":
                return SAG_REVIEW;
            case "APPROVED":
                return SAG_APPROVED;
            case "REJECTED":
                return SAG_REJECTED;
            case "DISBURSED":
                return PAYMENT_COMPLETED;
            default:
                try {
                    return ApplicationStatus.valueOf(clean);
                } catch (IllegalArgumentException e) {
                    return DRAFT;
                }
        }
    }
}
