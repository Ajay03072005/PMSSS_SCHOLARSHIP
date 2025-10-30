-- Create database
CREATE DATABASE IF NOT EXISTS pmsss_scholarship;
USE pmsss_scholarship;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    aadhar VARCHAR(12) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    role ENUM('student', 'admin', 'reviewer') DEFAULT 'student',
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_aadhar (aadhar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Applications table
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    application_id VARCHAR(50) NOT NULL UNIQUE,
    
    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('male', 'female', 'other') NOT NULL,
    category ENUM('general', 'obc', 'sc', 'st') NOT NULL,
    aadhar VARCHAR(12) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    district VARCHAR(100) NOT NULL,
    state ENUM('jk', 'ladakh') NOT NULL,
    pincode VARCHAR(6) NOT NULL,
    
    -- Academic Information (stored as JSON for flexibility)
    academic_info JSON,
    
    -- Family Information
    father_name VARCHAR(200) NOT NULL,
    father_occupation VARCHAR(200),
    father_mobile VARCHAR(10),
    mother_name VARCHAR(200) NOT NULL,
    mother_occupation VARCHAR(200),
    mother_mobile VARCHAR(10),
    annual_income DECIMAL(10,2),
    income_source VARCHAR(200),
    
    -- Bank Details
    account_holder_name VARCHAR(200) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(11) NOT NULL,
    bank_name VARCHAR(200) NOT NULL,
    branch_name VARCHAR(200) NOT NULL,
    
    -- Documents (file paths)
    photo VARCHAR(255),
    aadhar_doc VARCHAR(255),
    domicile VARCHAR(255),
    income_cert VARCHAR(255),
    tenth_marksheet VARCHAR(255),
    twelfth_marksheet VARCHAR(255),
    admission_letter VARCHAR(255),
    bank_passbook VARCHAR(255),
    
    -- Application Status
    status ENUM('draft', 'submitted', 'under_review', 'verified', 'approved', 'rejected', 'disbursed') DEFAULT 'draft',
    declaration BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMP NULL,
    reviewed_at TIMESTAMP NULL,
    reviewed_by INT,
    review_remarks TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_application_id (application_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Application Status History
CREATE TABLE IF NOT EXISTS application_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    status ENUM('draft', 'submitted', 'under_review', 'verified', 'approved', 'rejected', 'disbursed') NOT NULL,
    remarks TEXT,
    updated_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    INDEX idx_application_id (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    application_id INT,
    type ENUM('application_submitted', 'status_update', 'document_required', 'approved', 'rejected', 'disbursed', 'general') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default admin user (password: Admin@123)
INSERT INTO users (first_name, last_name, email, password, mobile, aadhar, date_of_birth, role, is_verified)
VALUES ('Admin', 'User', 'admin@pmsss.gov.in', '$2y$10$YourHashedPasswordHere', '9999999999', '999999999999', '1990-01-01', 'admin', TRUE)
ON DUPLICATE KEY UPDATE email=email;
