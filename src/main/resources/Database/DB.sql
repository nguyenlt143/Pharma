-- ==========================================
-- PHARMACY MANAGEMENT SYSTEM - SQL DDL (ORDERED)
-- ==========================================
-- CREATE DATABASE MEDICINE

-- USE MEDICINE


-- ==========================================
-- 1. ROLE
-- ==========================================
CREATE TABLE ROLE (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. SUPPLIER
-- ==========================================
CREATE TABLE SUPPLIER (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255)
);

-- ==========================================
-- 3. CATEGORY
-- ==========================================
CREATE TABLE CATEGORY (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    parent_id INT,
    FOREIGN KEY (parent_id) REFERENCES CATEGORY(category_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ==========================================
-- 4. UNIT
-- ==========================================
CREATE TABLE UNIT (
    unit_id INT AUTO_INCREMENT PRIMARY KEY,
    unit_name VARCHAR(50) NOT NULL,
    base_quantity DECIMAL(10,2) DEFAULT 1.00,
    description VARCHAR(255)
);

-- ==========================================
-- 5. BRANCH
-- ==========================================
CREATE TABLE BRANCH (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(255) NOT NULL,
    branch_type VARCHAR(50),
    address VARCHAR(255),
    manager_id INT
);

-- ==========================================
-- 6. USER
-- ==========================================
CREATE TABLE USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(100),
    role_id INT,
    branch_id INT,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    status TINYINT DEFAULT 1,
    FOREIGN KEY (role_id) REFERENCES ROLE(role_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Sau khi có USER, gán manager cho BRANCH
ALTER TABLE BRANCH
ADD CONSTRAINT fk_branch_manager FOREIGN KEY (manager_id) REFERENCES USER(user_id)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- ==========================================
-- 7. SHIFT
-- ==========================================
CREATE TABLE SHIFT (
    shift_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    shift_name VARCHAR(100),
    start_time TIME,
    end_time TIME,
    note VARCHAR(255),
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 8. SHIFT_WORK
-- ==========================================
CREATE TABLE SHIFT_WORK (
    shift_work_id INT AUTO_INCREMENT PRIMARY KEY,
    shift_id INT NOT NULL,
    user_id INT NOT NULL,
    work_date DATE NOT NULL,
    status TINYINT DEFAULT 1,
    check_in_time DATETIME,
    check_out_time DATETIME,
    real_cash INT,
    is_locked TINYINT DEFAULT 0,
    FOREIGN KEY (shift_id) REFERENCES SHIFT(shift_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 9. REPORT
-- ==========================================
CREATE TABLE REPORT (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    report_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    report_type VARCHAR(50),
    total_revenue DECIMAL(15,2) DEFAULT 0.00,
    total_profit DECIMAL(15,2) DEFAULT 0.00,
    total_sales INT DEFAULT 0,
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 10. MEDICINE
-- ==========================================
CREATE TABLE MEDICINE (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(255) NOT NULL,
    category_id INT,
    active_ingredient VARCHAR(255),
    brand_name VARCHAR(255),
    manufacturer VARCHAR(255),
    country_of_origin VARCHAR(100),
    registration_number VARCHAR(100),
    storage_conditions TEXT,
    indications TEXT,
    contraindications TEXT,
    side_effects TEXT,
    instructions TEXT,
    prescription_required TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ==========================================
-- 11. MEDICINE_VARIANT
-- ==========================================
CREATE TABLE MEDICINE_VARIANT (
    variant_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    dosage_form VARCHAR(100),
    dosage VARCHAR(50),
    strength VARCHAR(50),
    base_unit_id INT,
    barcode VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES MEDICINE(medicine_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (base_unit_id) REFERENCES UNIT(unit_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ==========================================
-- 12. SAMPLE_PRESCRIPTION
-- ==========================================
CREATE TABLE SAMPLE_PRESCRIPTION (
    sample_id INT AUTO_INCREMENT PRIMARY KEY,
    sample_name VARCHAR(255),
    variant_id INT,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    duration VARCHAR(50),
    note VARCHAR(250),
    FOREIGN KEY (variant_id) REFERENCES MEDICINE_VARIANT(variant_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 13. BATCH
-- ==========================================
CREATE TABLE BATCH (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    variant_id INT NOT NULL,
    batch_number VARCHAR(100),
    mfg_date DATE,
    expiry_date DATE,
    quantity_init INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (variant_id) REFERENCES MEDICINE_VARIANT(variant_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 14. UNIT_CONVERSION
-- ==========================================
CREATE TABLE UNIT_CONVERSION (
    conversion_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    from_unit_id INT NOT NULL,
    to_unit_id INT NOT NULL,
    conversion_rate DECIMAL(10,4) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES MEDICINE(medicine_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (from_unit_id) REFERENCES UNIT(unit_id),
    FOREIGN KEY (to_unit_id) REFERENCES UNIT(unit_id)
);

-- ==========================================
-- 15. INVENTORY
-- ==========================================
CREATE TABLE INVENTORY (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT DEFAULT 0,
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES BATCH(batch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 16. IMPORT_FORM
-- ==========================================
CREATE TABLE IMPORT_FORM (
    import_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    supplier_id INT,
    source_branch_id INT,
    created_by INT,
    import_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id),
    FOREIGN KEY (supplier_id) REFERENCES SUPPLIER(supplier_id),
    FOREIGN KEY (source_branch_id) REFERENCES BRANCH(branch_id),
    FOREIGN KEY (created_by) REFERENCES USER(user_id)
);

-- ==========================================
-- 17. IMPORT_DETAIL
-- ==========================================
CREATE TABLE IMPORT_DETAIL (
    import_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    import_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT DEFAULT 0,
    unit_price DECIMAL(15,2) DEFAULT 0.00,
    FOREIGN KEY (import_id) REFERENCES IMPORT_FORM(import_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES BATCH(batch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 18. EXPORT_FORM
-- ==========================================
CREATE TABLE EXPORT_FORM (
    export_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    created_by INT,
    export_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id),
    FOREIGN KEY (created_by) REFERENCES USER(user_id)
);

-- ==========================================
-- 19. EXPORT_DETAIL
-- ==========================================
CREATE TABLE EXPORT_DETAIL (
    export_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    export_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT DEFAULT 0,
    unit_price DECIMAL(15,2) DEFAULT 0.00,
    FOREIGN KEY (export_id) REFERENCES EXPORT_FORM(export_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES BATCH(batch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 20. CUSTOMER
-- ==========================================
CREATE TABLE CUSTOMER (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    phone VARCHAR(20),
    note VARCHAR(255)
);

-- ==========================================
-- 21. INVOICE
-- ==========================================
CREATE TABLE INVOICE (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    staff_id INT,
    branch_id INT,
    total_price DECIMAL(15,2) DEFAULT 0.00,
    payment_method VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    FOREIGN KEY (staff_id) REFERENCES USER(user_id),
    FOREIGN KEY (branch_id) REFERENCES BRANCH(branch_id)
);

-- ==========================================
-- 22. INVOICE_DETAIL
-- ==========================================
CREATE TABLE INVOICE_DETAIL (
    invoice_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT DEFAULT 0,
    unit_price DECIMAL(15,2) DEFAULT 0.00,
    FOREIGN KEY (invoice_id) REFERENCES INVOICE(invoice_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES BATCH(batch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
