package com.pmsss.ocr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrExtractedData {
    private String name;
    private LocalDate dateOfBirth;
    private String certificateNumber;
    private String institution;
    private String course;
    private BigDecimal income;
    private LocalDate documentDate;
    private String ifscCode;
    private String bankAccountNumber;
    private String rawText;
    private Double confidence;
    private String source;
    private String detectedDocumentType;
}
