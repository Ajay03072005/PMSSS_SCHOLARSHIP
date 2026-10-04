package com.pmsss.common.utils;

public class DataMaskingUtils {

    /**
     * Masks Aadhaar number showing only last 4 digits: XXXX XXXX 9012
     */
    public static String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.isBlank()) return null;
        String clean = aadhaar.replaceAll("[^0-9]", "");
        if (clean.length() < 4) return "XXXX";
        String last4 = clean.substring(clean.length() - 4);
        return "XXXX XXXX " + last4;
    }

    /**
     * Masks bank account number showing only last 4 digits: XXXXXX1234
     */
    public static String maskBankAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) return null;
        String clean = accountNumber.replaceAll("[^0-9]", "");
        if (clean.length() < 4) return "XXXX";
        String last4 = clean.substring(clean.length() - 4);
        return "XXXXXX" + last4;
    }

    /**
     * Generic text string masking
     */
    public static String maskText(String text, int visibleCharsAtEnd) {
        if (text == null || text.isBlank()) return null;
        if (text.length() <= visibleCharsAtEnd) return text;
        String visible = text.substring(text.length() - visibleCharsAtEnd);
        return "X".repeat(text.length() - visibleCharsAtEnd) + visible;
    }
}
