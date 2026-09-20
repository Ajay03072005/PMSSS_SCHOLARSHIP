package com.pmsss.common.enums;

public enum DocumentType {
    PHOTO,
    AADHAR,
    DOMICILE,
    INCOME_CERT,
    TENTH_MARKSHEET,
    TWELFTH_MARKSHEET,
    ADMISSION_LETTER,
    BANK_PASSBOOK,
    OTHER;

    public static DocumentType fromString(String type) {
        if (type == null) return OTHER;
        String clean = type.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        switch (clean) {
            case "AADHAR_DOC":
            case "AADHAAR":
                return AADHAR;
            case "INCOME":
            case "INCOME_CERTIFICATE":
                return INCOME_CERT;
            case "TENTHMARKSHEET":
            case "10TH_MARKSHEET":
                return TENTH_MARKSHEET;
            case "TWELFTHMARKSHEET":
            case "12TH_MARKSHEET":
                return TWELFTH_MARKSHEET;
            case "ADMISSIONLETTER":
                return ADMISSION_LETTER;
            case "BANKPASSBOOK":
            case "PASSBOOK":
                return BANK_PASSBOOK;
            default:
                try {
                    return DocumentType.valueOf(clean);
                } catch (IllegalArgumentException e) {
                    return OTHER;
                }
        }
    }
}
