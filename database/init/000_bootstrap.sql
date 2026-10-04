CREATE DATABASE IF NOT EXISTS pmsss
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE pmsss;

SOURCE /opt/pmsss-migrations/V1__create_master_tables.sql;
SOURCE /opt/pmsss-migrations/V2__create_identity_and_student_tables.sql;
SOURCE /opt/pmsss-migrations/V3__create_application_and_snapshot_tables.sql;
SOURCE /opt/pmsss-migrations/V4__create_document_and_ocr_tables.sql;
SOURCE /opt/pmsss-migrations/V5__create_workflow_notification_audit_tables.sql;
SOURCE /opt/pmsss-migrations/V6__create_indexes.sql;
SOURCE /opt/pmsss-migrations/V7__insert_master_seed_data.sql;
SOURCE /opt/pmsss-migrations/V8__create_refresh_token_table.sql;