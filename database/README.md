# PMSSS Database Schema

This directory contains the Module 2 MySQL 8+ schema for the PMSSS Scholarship Management System.

## Execution order

1. Run `bootstrap.sql` as a MySQL administrator.
2. Connect to the `pmsss` database.
3. Execute `migrations/V1__create_master_tables.sql` through `V8__create_refresh_token_table.sql` in filename order.

The scripts use Flyway-compatible names and dependency order, but are intentionally kept outside the running Spring Boot classpath for now. The current backend uses a legacy JPA schema with `spring.jpa.hibernate.ddl-auto=update`; enabling these migrations without a baseline would risk changing or invalidating existing tables and data.

The Docker Compose deployment intentionally does not auto-run these normalized migrations. It deploys the current legacy JPA model against a fresh database. Run the migrations only as part of a planned compatibility/data migration after the backend entities have been moved to the normalized model.

## Schema areas

- `V1`: academic years, scholarship types, document types, geography, universities, and colleges.
- `V2`: users, roles, students, officers, addresses, family, academic, and bank data.
- `V3`: applications and immutable application snapshots.
- `V4`: document metadata and OCR results.
- `V5`: verification, correction, assignment, status history, risk assessment, notifications, and audit logs.
- `V6`: search and workflow indexes.
- `V7`: non-sensitive master seed data only.
- `V8`: hashed, revocable refresh-token storage.

## Security rules

- Passwords are represented only by `users.password_hash`.
- Aadhaar is represented by a last-four value in this schema.
- Bank account numbers are represented by encrypted bytes plus a last-four value.
- Documents are stored outside MySQL; only Cloudinary metadata and references are stored.
- Audit JSON must be sanitized by the application before insertion. Passwords, tokens, full Aadhaar values, and full bank account numbers must never be recorded.

## Compatibility boundary

The existing application currently maps legacy denormalized tables such as `users`, `applications`, and `application_history`, and also seeds demo records from `DataInitializer`. This Module 2 schema is the target normalized model and does not delete or rename those existing structures. A later compatibility migration must either map current data into this model or establish a clean deployment baseline before Flyway is enabled in Spring Boot.