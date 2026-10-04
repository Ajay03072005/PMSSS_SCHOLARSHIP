USE pmsss;

CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(30) NOT NULL,
    student_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    scholarship_type_id BIGINT NOT NULL,
    college_id BIGINT,
    current_status VARCHAR(40) NOT NULL DEFAULT 'DRAFT',
    submission_date TIMESTAMP NULL,
    verification_completed_at TIMESTAMP NULL,
    approval_date TIMESTAMP NULL,
    rejection_date TIMESTAMP NULL,
    rejection_reason TEXT,
    current_officer_id BIGINT,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_applications_number UNIQUE (application_number),
    CONSTRAINT fk_applications_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_applications_academic_year FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    CONSTRAINT fk_applications_scholarship_type FOREIGN KEY (scholarship_type_id) REFERENCES scholarship_types(id),
    CONSTRAINT fk_applications_college FOREIGN KEY (college_id) REFERENCES colleges(id),
    CONSTRAINT fk_applications_officer FOREIGN KEY (current_officer_id) REFERENCES officers(id),
    CONSTRAINT uq_applications_student_year_type UNIQUE (student_id, academic_year_id, scholarship_type_id),
    CONSTRAINT chk_applications_risk CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS application_personal_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(30),
    category VARCHAR(50),
    email VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    aadhaar_last_four CHAR(4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_personal_application UNIQUE (application_id),
    CONSTRAINT fk_app_personal_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS application_family_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    father_name VARCHAR(200),
    mother_name VARCHAR(200),
    guardian_name VARCHAR(200),
    guardian_relationship VARCHAR(100),
    family_annual_income DECIMAL(14,2),
    income_certificate_number VARCHAR(100),
    income_certificate_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_family_application UNIQUE (application_id),
    CONSTRAINT fk_app_family_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS application_academic_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    board VARCHAR(150),
    institution_name VARCHAR(255),
    passing_year SMALLINT,
    total_marks DECIMAL(10,2),
    obtained_marks DECIMAL(10,2),
    percentage DECIMAL(5,2),
    grade VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_academic_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT chk_app_academic_percentage CHECK (percentage IS NULL OR percentage BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS application_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    address_type VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    village VARCHAR(100),
    city VARCHAR(100),
    district_id BIGINT,
    state_id BIGINT,
    pincode VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_addresses_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_app_addresses_district FOREIGN KEY (district_id) REFERENCES districts(id),
    CONSTRAINT fk_app_addresses_state FOREIGN KEY (state_id) REFERENCES states(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS application_bank_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    account_holder_name VARCHAR(200) NOT NULL,
    bank_name VARCHAR(200) NOT NULL,
    branch_name VARCHAR(200),
    account_number_encrypted VARBINARY(512) NOT NULL,
    account_number_last_four CHAR(4) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    account_type VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_bank_application UNIQUE (application_id),
    CONSTRAINT fk_app_bank_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;