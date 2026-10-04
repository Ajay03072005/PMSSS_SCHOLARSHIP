package com.pmsss.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDto {
    private String uniqueId;
    private String verificationStatus; // PENDING, VERIFIED, REJECTED, NEEDS_REVIEW
    private String verificationMethod; // MANUAL, AI_ASSISTED, QR_VERIFICATION, OFFICIAL_SOURCE, DIGITAL_SIGNATURE
    private Double confidenceScore;
    private String verifiedByName;
    private String verificationNotes;
    private LocalDateTime verifiedAt;
}
