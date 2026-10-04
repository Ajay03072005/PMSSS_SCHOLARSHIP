# PMSSS Entity Relationships

## Core relationships

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : grants
    USERS ||--o| STUDENTS : identifies
    USERS ||--o| OFFICERS : identifies
    STATES ||--o{ DISTRICTS : contains
    STATES ||--o{ COLLEGES : contains
    UNIVERSITIES ||--o{ COLLEGES : accredits
    STUDENTS ||--o{ APPLICATIONS : submits
    ACADEMIC_YEARS ||--o{ APPLICATIONS : scopes
    SCHOLARSHIP_TYPES ||--o{ APPLICATIONS : classifies
    COLLEGES ||--o{ APPLICATIONS : admits
    OFFICERS ||--o{ APPLICATIONS : owns
    APPLICATIONS ||--o| APPLICATION_PERSONAL_DETAILS : snapshots
    APPLICATIONS ||--o| APPLICATION_FAMILY_DETAILS : snapshots
    APPLICATIONS ||--o{ APPLICATION_ACADEMIC_DETAILS : snapshots
    APPLICATIONS ||--o{ APPLICATION_ADDRESSES : snapshots
    APPLICATIONS ||--o| APPLICATION_BANK_DETAILS : snapshots
    APPLICATIONS ||--o{ APPLICATION_DOCUMENTS : contains
    DOCUMENT_TYPES ||--o{ APPLICATION_DOCUMENTS : classifies
    APPLICATION_DOCUMENTS ||--o{ OCR_RESULTS : produces
    OCR_RESULTS ||--o{ OCR_FIELD_RESULTS : contains
    APPLICATIONS ||--o{ APPLICATION_VERIFICATIONS : receives
    APPLICATIONS ||--o{ CORRECTION_REQUESTS : receives
    APPLICATIONS ||--o{ APPLICATION_ASSIGNMENTS : receives
    APPLICATIONS ||--o{ APPLICATION_STATUS_HISTORY : records
    APPLICATIONS ||--o{ AI_RISK_ASSESSMENTS : evaluates
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ AUDIT_LOGS : performs
```

## Table inventory

| Area | Tables |
| --- | --- |
| Master data | `academic_years`, `scholarship_types`, `document_types`, `states`, `districts`, `universities`, `colleges` |
| Identity | `users`, `roles`, `user_roles`, `students`, `officers` |
| Student profile | `student_addresses`, `student_family_details`, `student_academic_details`, `student_bank_details` |
| Application | `applications`, `application_personal_details`, `application_family_details`, `application_academic_details`, `application_addresses`, `application_bank_details` |
| Documents and OCR | `application_documents`, `ocr_results`, `ocr_field_results` |
| Workflow | `application_verifications`, `document_verification_history`, `correction_requests`, `application_assignments`, `application_status_history` |
| Supporting services | `ai_risk_assessments`, `notifications`, `audit_logs` |

## Design decisions

- `applications` references the student profile but stores submitted values in application snapshot tables so later profile edits do not rewrite history.
- A student may submit multiple applications across academic years and scholarship types; the composite uniqueness rule prevents duplicate applications for the same combination.
- Documents reference external Cloudinary objects, not binary document content.
- OCR is one-to-many from documents so reprocessing can retain prior results.
- Verification, correction, assignment, and status history are append-oriented records; current state remains on the owning record for efficient queue queries.
- AI risk is advisory data only. The schema has no AI action that can automatically reject an application.