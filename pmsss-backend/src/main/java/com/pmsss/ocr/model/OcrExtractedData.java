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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public static OcrExtractedDataBuilder builder() {
        return new OcrExtractedDataBuilder();
    }

    public static class OcrExtractedDataBuilder {
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

        public OcrExtractedDataBuilder name(String name) { this.name = name; return this; }
        public OcrExtractedDataBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public OcrExtractedDataBuilder certificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; return this; }
        public OcrExtractedDataBuilder institution(String institution) { this.institution = institution; return this; }
        public OcrExtractedDataBuilder course(String course) { this.course = course; return this; }
        public OcrExtractedDataBuilder income(BigDecimal income) { this.income = income; return this; }
        public OcrExtractedDataBuilder documentDate(LocalDate documentDate) { this.documentDate = documentDate; return this; }
        public OcrExtractedDataBuilder ifscCode(String ifscCode) { this.ifscCode = ifscCode; return this; }
        public OcrExtractedDataBuilder bankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; return this; }
        public OcrExtractedDataBuilder rawText(String rawText) { this.rawText = rawText; return this; }
        public OcrExtractedDataBuilder confidence(Double confidence) { this.confidence = confidence; return this; }
        public OcrExtractedDataBuilder source(String source) { this.source = source; return this; }
        public OcrExtractedDataBuilder detectedDocumentType(String detectedDocumentType) { this.detectedDocumentType = detectedDocumentType; return this; }

        public OcrExtractedData build() {
            OcrExtractedData data = new OcrExtractedData();
            data.name = this.name;
            data.dateOfBirth = this.dateOfBirth;
            data.certificateNumber = this.certificateNumber;
            data.institution = this.institution;
            data.course = this.course;
            data.income = this.income;
            data.documentDate = this.documentDate;
            data.ifscCode = this.ifscCode;
            data.bankAccountNumber = this.bankAccountNumber;
            data.rawText = this.rawText;
            data.confidence = this.confidence;
            data.source = this.source;
            data.detectedDocumentType = this.detectedDocumentType;
            return data;
        }
    }
}


