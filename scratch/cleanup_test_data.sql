-- ==================================================================
-- PMSSS 2.0 SAFE TEST DATA CLEANUP SCRIPT
-- Purges ONLY synthetic test accounts and applications generated 
-- during E2E automated test runs.
-- NEVER deletes real user data.
-- ==================================================================

START TRANSACTION;

-- 1. Delete extracted document data for test applications
DELETE FROM document_extracted_data 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%' OR email LIKE '%.officer%@pmsss.local' OR email LIKE '%admin%@pmsss.local'
);

-- 2. Delete document records for test applications
DELETE FROM documents 
WHERE uploaded_by IN (
    SELECT id FROM users WHERE email LIKE 'test.student%' OR email LIKE '%.officer%@pmsss.local' OR email LIKE '%admin%@pmsss.local'
) OR application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%' OR email LIKE '%.officer%@pmsss.local' OR email LIKE '%admin%@pmsss.local'
);

-- 3. Delete payment transactions and payments
DELETE FROM payment_transactions 
WHERE payment_id IN (
    SELECT id FROM payments WHERE application_id IN (
        SELECT id FROM applications WHERE email LIKE 'test.student%'
    )
);

DELETE FROM payments 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

-- 4. Delete application assignments, escalations, corrections, verification records, and status history
DELETE FROM application_assignments 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

DELETE FROM application_escalations 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

DELETE FROM correction_requests 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

DELETE FROM verification_records 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

DELETE FROM application_status_history 
WHERE application_id IN (
    SELECT id FROM applications WHERE email LIKE 'test.student%'
);

-- 5. Delete anomaly alerts for test applications
DELETE FROM anomaly_alerts 
WHERE student_name LIKE 'Test Student%';

-- 6. Delete notifications and logs for test users
DELETE FROM notification_logs 
WHERE notification_id IN (
    SELECT id FROM notifications WHERE user_id IN (
        SELECT id FROM users WHERE email LIKE 'test.student%' OR email LIKE '%.officer%@pmsss.local' OR email LIKE '%admin%@pmsss.local'
    )
);

DELETE FROM notifications 
WHERE user_id IN (
    SELECT id FROM users WHERE email LIKE 'test.student%' OR email LIKE '%.officer%@pmsss.local' OR email LIKE '%admin%@pmsss.local'
);

-- 7. Delete audit logs for test users
DELETE FROM audit_logs 
WHERE username LIKE 'test.student%' OR username LIKE '%.officer%@pmsss.local' OR username LIKE '%admin%@pmsss.local';

-- 8. Delete student profiles
DELETE FROM student_profiles 
WHERE user_id IN (
    SELECT id FROM users WHERE email LIKE 'test.student%'
);

-- 9. Delete applications
DELETE FROM applications 
WHERE email LIKE 'test.student%' OR user_id IN (
    SELECT id FROM users WHERE email LIKE 'test.student%'
);

-- 10. Delete test users
DELETE FROM users 
WHERE email LIKE 'test.student%' OR email LIKE 'sag.officer%@pmsss.local' OR email LIKE 'finance.officer%@pmsss.local' OR email LIKE 'admin01@pmsss.local' OR email LIKE 'superadmin01@pmsss.local';

COMMIT;

SELECT 'Test Data Cleanup Completed Successfully' AS status;
