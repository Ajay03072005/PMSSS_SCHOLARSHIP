USE pmsss;

INSERT INTO roles (role_code, role_name, description)
VALUES
    ('STUDENT', 'Student', 'Scholarship applicant'),
    ('VERIFICATION_OFFICER', 'Verification Officer', 'Performs document verification'),
    ('REVIEW_OFFICER', 'Review Officer', 'Reviews verified applications'),
    ('APPROVAL_OFFICER', 'Approval Officer', 'Approves eligible applications'),
    ('ADMIN', 'Administrator', 'Manages system configuration')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), description = VALUES(description);

INSERT INTO scholarship_types (unique_code, name, description, status)
VALUES ('PMSSS', 'Prime Minister''s Special Scholarship Scheme', 'PMSSS scholarship programme', 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO academic_years (academic_year, start_date, end_date, status)
VALUES ('2026-27', '2026-04-01', '2027-03-31', 'ACTIVE')
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO document_types (unique_code, name, mandatory, allowed_file_types, max_file_size)
VALUES
    ('AADHAAR', 'Aadhaar Card', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('INCOME_CERTIFICATE', 'Income Certificate', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('CASTE_CERTIFICATE', 'Caste Certificate', FALSE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('DOMICILE_CERTIFICATE', 'Domicile Certificate', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('MARKSHEET_10', '10th Marksheet', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('MARKSHEET_12', '12th Marksheet', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('DIPLOMA', 'Diploma Certificate', FALSE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('BANK_PASSBOOK', 'Bank Passbook', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('ADMISSION_LETTER', 'Admission Letter', TRUE, 'application/pdf,image/jpeg,image/png', 10485760),
    ('PHOTOGRAPH', 'Photograph', TRUE, 'image/jpeg,image/png', 5242880),
    ('SIGNATURE', 'Signature', FALSE, 'image/jpeg,image/png', 5242880)
ON DUPLICATE KEY UPDATE name = VALUES(name), mandatory = VALUES(mandatory), max_file_size = VALUES(max_file_size);

INSERT INTO states (state_code, state_name)
VALUES ('JK', 'Jammu and Kashmir'), ('LA', 'Ladakh')
ON DUPLICATE KEY UPDATE state_name = VALUES(state_name);