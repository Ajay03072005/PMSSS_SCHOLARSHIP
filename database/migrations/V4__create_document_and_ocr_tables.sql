USE pmsss;

CREATE TABLE IF NOT EXISTS application_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_unique_id CHAR(36) NOT NULL,
    application_id BIGINT NOT NULL,
    document_type_id BIGINT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    cloudinary_public_id VARCHAR(255),
    cloudinary_url VARCHAR(1000),
    resource_type VARCHAR(30),
    file_format VARCHAR(20),
    file_size BIGINT,
    upload_status VARCHAR(20) NOT NULL DEFAULT 'UPLOADED',
    ocr_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    uploaded_by BIGINT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_by BIGINT,
    verified_at TIMESTAMP NULL,
    rejection_reason TEXT,
    CONSTRAINT uq_documents_unique_id UNIQUE (document_unique_id),
    CONSTRAINT fk_documents_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_documents_type FOREIGN KEY (document_type_id) REFERENCES document_types(id),
    CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id),
    CONSTRAINT fk_documents_verified_by FOREIGN KEY (verified_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ocr_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    extracted_text LONGTEXT,
    confidence_score DECIMAL(5,2),
    processing_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ocr_results_document FOREIGN KEY (document_id) REFERENCES application_documents(id),
    CONSTRAINT chk_ocr_results_confidence CHECK (confidence_score IS NULL OR confidence_score BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ocr_field_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ocr_result_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    extracted_value TEXT,
    application_value TEXT,
    match_status VARCHAR(20) NOT NULL DEFAULT 'NOT_FOUND',
    confidence_score DECIMAL(5,2),
    officer_status VARCHAR(30),
    officer_remarks TEXT,
    verified_by BIGINT,
    verified_at TIMESTAMP NULL,
    CONSTRAINT fk_ocr_fields_result FOREIGN KEY (ocr_result_id) REFERENCES ocr_results(id),
    CONSTRAINT fk_ocr_fields_officer FOREIGN KEY (verified_by) REFERENCES users(id),
    CONSTRAINT chk_ocr_fields_confidence CHECK (confidence_score IS NULL OR confidence_score BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;