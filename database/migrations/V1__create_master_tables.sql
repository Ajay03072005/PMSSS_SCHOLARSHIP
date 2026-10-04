USE pmsss;

CREATE TABLE IF NOT EXISTS academic_years (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_year VARCHAR(9) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_academic_years_year UNIQUE (academic_year),
    CONSTRAINT chk_academic_years_dates CHECK (end_date > start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS scholarship_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    eligibility_description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_scholarship_types_code UNIQUE (unique_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS document_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    allowed_file_types VARCHAR(500),
    max_file_size BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_document_types_code UNIQUE (unique_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state_code VARCHAR(20) NOT NULL,
    state_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uq_states_code UNIQUE (state_code),
    CONSTRAINT uq_states_name UNIQUE (state_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS districts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state_id BIGINT NOT NULL,
    district_code VARCHAR(20) NOT NULL,
    district_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_districts_state FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT uq_districts_state_code UNIQUE (state_id, district_code),
    CONSTRAINT uq_districts_state_name UNIQUE (state_id, district_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS universities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    university_unique_id CHAR(36) NOT NULL,
    university_code VARCHAR(50) NOT NULL,
    university_name VARCHAR(200) NOT NULL,
    state_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_universities_unique_id UNIQUE (university_unique_id),
    CONSTRAINT uq_universities_code UNIQUE (university_code),
    CONSTRAINT fk_universities_state FOREIGN KEY (state_id) REFERENCES states(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS colleges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_unique_id CHAR(36) NOT NULL,
    college_code VARCHAR(50) NOT NULL,
    college_name VARCHAR(200) NOT NULL,
    college_type VARCHAR(50),
    university_id BIGINT,
    state_id BIGINT,
    district_id BIGINT,
    address TEXT,
    pincode VARCHAR(10),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_colleges_unique_id UNIQUE (college_unique_id),
    CONSTRAINT uq_colleges_code UNIQUE (college_code),
    CONSTRAINT fk_colleges_university FOREIGN KEY (university_id) REFERENCES universities(id),
    CONSTRAINT fk_colleges_state FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT fk_colleges_district FOREIGN KEY (district_id) REFERENCES districts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;