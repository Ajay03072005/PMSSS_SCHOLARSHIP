package com.pmsss.common.enums;

public enum RoleType {
    ROLE_SUPER_ADMIN,
    ROLE_ADMIN,
    ROLE_SAG_OFFICER,
    ROLE_FINANCE_OFFICER,
    ROLE_STUDENT;

    public static RoleType fromString(String role) {
        if (role == null) return ROLE_STUDENT;
        String clean = role.trim().toUpperCase();
        if (!clean.startsWith("ROLE_")) {
            clean = "ROLE_" + clean;
        }
        try {
            return RoleType.valueOf(clean);
        } catch (IllegalArgumentException e) {
            return ROLE_STUDENT;
        }
    }
}
