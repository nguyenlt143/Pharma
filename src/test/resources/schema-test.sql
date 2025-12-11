-- ============================================================================
-- SCHEMA FOR INTEGRATION TEST - H2 Database (MySQL Mode)
-- ============================================================================

-- Drop tables if exist (in reverse dependency order)
DROP TABLE IF EXISTS stock_adjustments;
DROP TABLE IF EXISTS invoice_details;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS shift_works;
DROP TABLE IF EXISTS shift_assignments;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS request_details;
DROP TABLE IF EXISTS inventory_movement_details;
DROP TABLE IF EXISTS inventory_movements;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS batches;
DROP TABLE IF EXISTS prices;
DROP TABLE IF EXISTS medicine_variant;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS request_forms;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS shifts;
DROP TABLE IF EXISTS branchs;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS categorys;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS suppliers;

-- ============================================================================
-- LEVEL 1: Independent/Reference Tables
-- ============================================================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE branchs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    branch_type VARCHAR(50),
    address VARCHAR(500),
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE categorys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- LEVEL 2: Tables with one level of dependency
-- ============================================================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    full_name VARCHAR(255),
    role_id BIGINT,
    branch_id BIGINT,
    phone_number VARCHAR(50),
    email VARCHAR(255),
    image_url LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    category_id BIGINT,
    active_ingredient VARCHAR(500),
    brand_name VARCHAR(255),
    manufacturer VARCHAR(255),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (category_id) REFERENCES categorys(id)
);

CREATE TABLE request_forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT,
    request_type VARCHAR(50),
    request_status VARCHAR(50),
    note VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE shifts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT,
    name VARCHAR(255),
    note VARCHAR(500),
    start_time TIME,
    end_time TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (branch_id) REFERENCES branchs(id)
);

CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- LEVEL 3: Tables with multiple levels of dependency
-- ============================================================================

CREATE TABLE medicine_variant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_id BIGINT,
    dosage_form VARCHAR(100),
    dosage VARCHAR(100),
    strength VARCHAR(100),
    package_unit_id BIGINT,
    base_unit_id BIGINT,
    quantity_per_package DOUBLE,
    barcode VARCHAR(100),
    registration_number VARCHAR(100),
    storage_conditions VARCHAR(500),
    indications VARCHAR(1000),
    contraindications VARCHAR(1000),
    side_effects VARCHAR(1000),
    instructions VARCHAR(1000),
    prescription_require BOOLEAN DEFAULT FALSE,
    uses VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id),
    FOREIGN KEY (package_unit_id) REFERENCES units(id),
    FOREIGN KEY (base_unit_id) REFERENCES units(id)
);

CREATE TABLE batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variant_id BIGINT,
    batch_code VARCHAR(255) NOT NULL UNIQUE,
    mfg_date DATE,
    expiry_date DATE,
    supplier_id BIGINT,
    batch_status VARCHAR(50),
    source_movement_id BIGINT,
    total_received INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (variant_id) REFERENCES medicine_variant(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variant_id BIGINT,
    branch_id BIGINT,
    sale_price DOUBLE,
    branch_price DOUBLE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (variant_id) REFERENCES medicine_variant(id),
    FOREIGN KEY (branch_id) REFERENCES branchs(id)
);

CREATE TABLE inventory_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movement_type VARCHAR(50),
    supplier_id BIGINT,
    source_branch_id BIGINT,
    destination_branch_id BIGINT,
    request_form_id BIGINT,
    movement_status VARCHAR(50),
    total_money DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (request_form_id) REFERENCES request_forms(id)
);

-- ============================================================================
-- LEVEL 4: Tables with deepest dependency levels
-- ============================================================================

CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT,
    variant_id BIGINT,
    batch_id BIGINT,
    quantity BIGINT,
    cost_price DOUBLE,
    min_stock BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (branch_id) REFERENCES branchs(id),
    FOREIGN KEY (variant_id) REFERENCES medicine_variant(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE inventory_movement_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movement_id BIGINT,
    variant_id BIGINT,
    batch_id BIGINT,
    quantity BIGINT,
    price DOUBLE,
    snap_cost DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (movement_id) REFERENCES inventory_movements(id),
    FOREIGN KEY (variant_id) REFERENCES medicine_variant(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE request_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_form_id BIGINT,
    variant_id BIGINT,
    quantity BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (request_form_id) REFERENCES request_forms(id)
);

CREATE TABLE stock_adjustments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT,
    variant_id BIGINT,
    batch_id BIGINT,
    before_quantity BIGINT,
    after_quantity BIGINT,
    difference_quantity BIGINT,
    reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

-- ============================================================================
-- SHIFT MANAGEMENT TABLES
-- ============================================================================

CREATE TABLE shift_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (shift_id) REFERENCES shifts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE shift_works (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_assignment_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (shift_assignment_id) REFERENCES shift_assignments(id)
);

-- ============================================================================
-- INVOICE TABLES (for Revenue Reports)
-- ============================================================================

CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_code VARCHAR(255) NOT NULL UNIQUE,
    customer_id BIGINT,
    shift_work_id BIGINT,
    branch_id BIGINT,
    total_price DOUBLE,
    description VARCHAR(1000),
    payment_method VARCHAR(50),
    user_id BIGINT,
    invoice_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (shift_work_id) REFERENCES shift_works(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE invoice_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    inventory_id BIGINT NOT NULL,
    quantity BIGINT,
    price DOUBLE,
    multiplier DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (inventory_id) REFERENCES inventory(id)
);
