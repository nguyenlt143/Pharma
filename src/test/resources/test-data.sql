-- ============================================================================
-- TEST DATA FOR WAREHOUSE INTEGRATION TEST
-- ============================================================================

-- ============================================================================
-- LEVEL 1: Reference Data
-- ============================================================================

-- Roles
INSERT INTO roles (id, name, deleted) VALUES (1, 'ROLE_ADMIN', FALSE);
INSERT INTO roles (id, name, deleted) VALUES (2, 'ROLE_WAREHOUSE', FALSE);
INSERT INTO roles (id, name, deleted) VALUES (3, 'ROLE_MANAGER', FALSE);
INSERT INTO roles (id, name, deleted) VALUES (4, 'ROLE_PHARMACIST', FALSE);
INSERT INTO roles (id, name, deleted) VALUES (5, 'ROLE_INVENTORY', FALSE);

-- Branches
INSERT INTO branchs (id, name, branch_type, address, deleted) VALUES (1, 'Kho Tổng', 'HEAD_QUARTER', '123 Nguyễn Văn A, Q1, HCM', FALSE);
INSERT INTO branchs (id, name, branch_type, address, deleted) VALUES (2, 'Chi Nhánh 1', 'BRANCH', '456 Lê Văn B, Q2, HCM', FALSE);
INSERT INTO branchs (id, name, branch_type, address, deleted) VALUES (3, 'Chi Nhánh 2', 'BRANCH', '789 Trần Văn C, Q3, HCM', FALSE);

-- Categories
INSERT INTO categorys (id, name, description, deleted) VALUES (1, 'Thuốc giảm đau', 'Các loại thuốc giảm đau', FALSE);
INSERT INTO categorys (id, name, description, deleted) VALUES (2, 'Thuốc kháng sinh', 'Các loại thuốc kháng sinh', FALSE);
INSERT INTO categorys (id, name, description, deleted) VALUES (3, 'Vitamin', 'Các loại vitamin và thực phẩm chức năng', FALSE);

-- Units
INSERT INTO units (id, name, description, deleted) VALUES (1, 'Viên', 'Đơn vị viên', FALSE);
INSERT INTO units (id, name, description, deleted) VALUES (2, 'Hộp', 'Đơn vị hộp', FALSE);
INSERT INTO units (id, name, description, deleted) VALUES (3, 'Chai', 'Đơn vị chai', FALSE);
INSERT INTO units (id, name, description, deleted) VALUES (4, 'Vỉ', 'Đơn vị vỉ', FALSE);

-- Suppliers
INSERT INTO suppliers (id, name, phone, address, deleted) VALUES (1, 'Công ty Dược phẩm ABC', '0901234567', '111 Đường XYZ, Q1, HCM', FALSE);
INSERT INTO suppliers (id, name, phone, address, deleted) VALUES (2, 'Công ty Dược phẩm DEF', '0907654321', '222 Đường ABC, Q2, HCM', FALSE);

-- ============================================================================
-- LEVEL 2: Users, Medicines, Request Forms
-- ============================================================================

-- Users
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (1, 'warehouse_user', '123456', 'Nhân viên Kho', 2, 1, '0912345678', 'warehouse@pharma.vn', FALSE);
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (2, 'manager_user', '123456', 'Quản lý Chi nhánh', 3, 2, '0923456789', 'manager@pharma.vn', FALSE);
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (3, 'admin_user', '123456', 'Admin', 1, 1, '0934567890', 'admin@pharma.vn', FALSE);

-- Medicines
INSERT INTO medicines (id, name, category_id, active_ingredient, brand_name, manufacturer, country, deleted)
VALUES (1, 'Paracetamol 500mg', 1, 'Paracetamol', 'Panadol', 'GSK', 'Vietnam', FALSE);
INSERT INTO medicines (id, name, category_id, active_ingredient, brand_name, manufacturer, country, deleted)
VALUES (2, 'Amoxicillin 500mg', 2, 'Amoxicillin', 'Amoxil', 'Pfizer', 'USA', FALSE);
INSERT INTO medicines (id, name, category_id, active_ingredient, brand_name, manufacturer, country, deleted)
VALUES (3, 'Vitamin C 1000mg', 3, 'Acid Ascorbic', 'Cevit', 'DHG', 'Vietnam', FALSE);

-- Request Forms
INSERT INTO request_forms (id, branch_id, request_type, request_status, note, deleted)
VALUES (1, 2, 'IMPORT', 'REQUESTED', 'Yêu cầu nhập hàng từ chi nhánh 1', FALSE);
INSERT INTO request_forms (id, branch_id, request_type, request_status, note, deleted)
VALUES (2, 3, 'IMPORT', 'CONFIRMED', 'Yêu cầu nhập hàng từ chi nhánh 2', FALSE);
INSERT INTO request_forms (id, branch_id, request_type, request_status, note, deleted)
VALUES (3, 2, 'RETURN', 'REQUESTED', 'Yêu cầu xuất hàng về chi nhánh 1', FALSE);

-- ============================================================================
-- LEVEL 3: Medicine Variants, Batches
-- ============================================================================

-- Medicine Variants
INSERT INTO medicine_variant (id, medicine_id, dosage_form, dosage, strength, package_unit_id, base_unit_id, quantity_per_package, barcode, registration_number, prescription_require, deleted)
VALUES (1, 1, 'Viên nén', '500mg', '500mg', 2, 1, 100, '8934567890123', 'VD-12345-18', FALSE, FALSE);
INSERT INTO medicine_variant (id, medicine_id, dosage_form, dosage, strength, package_unit_id, base_unit_id, quantity_per_package, barcode, registration_number, prescription_require, deleted)
VALUES (2, 2, 'Viên nang', '500mg', '500mg', 2, 1, 50, '8934567890124', 'VD-12346-18', TRUE, FALSE);
INSERT INTO medicine_variant (id, medicine_id, dosage_form, dosage, strength, package_unit_id, base_unit_id, quantity_per_package, barcode, registration_number, prescription_require, deleted)
VALUES (3, 3, 'Viên sủi', '1000mg', '1000mg', 3, 1, 20, '8934567890125', 'VD-12347-18', FALSE, FALSE);

-- Batches
INSERT INTO batches (id, variant_id, batch_code, mfg_date, expiry_date, supplier_id, batch_status, total_received, deleted)
VALUES (1, 1, 'BATCH-001', '2024-01-01', '2026-01-01', 1, 'ACTIVE', 1000, FALSE);
INSERT INTO batches (id, variant_id, batch_code, mfg_date, expiry_date, supplier_id, batch_status, total_received, deleted)
VALUES (2, 2, 'BATCH-002', '2024-02-01', '2026-02-01', 1, 'ACTIVE', 500, FALSE);
INSERT INTO batches (id, variant_id, batch_code, mfg_date, expiry_date, supplier_id, batch_status, total_received, deleted)
VALUES (3, 3, 'BATCH-003', '2024-03-01', '2026-03-01', 2, 'ACTIVE', 200, FALSE);
INSERT INTO batches (id, variant_id, batch_code, mfg_date, expiry_date, supplier_id, batch_status, total_received, deleted)
VALUES (4, 1, 'BATCH-004', '2024-06-01', '2025-01-15', 1, 'ACTIVE', 100, FALSE);

-- ============================================================================
-- LEVEL 4: Inventory, Movements, Stock Adjustments
-- ============================================================================

-- Inventory (at Warehouse - branch_id = 1)
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (1, 1, 1, 1, 500, 5000.0, 100, FALSE);
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (2, 1, 2, 2, 300, 15000.0, 50, FALSE);
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (3, 1, 3, 3, 150, 8000.0, 30, FALSE);
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (4, 1, 1, 4, 80, 5000.0, 100, FALSE);

-- Inventory (at Branch 2 - branch_id = 2)
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (5, 2, 1, 1, 100, 5000.0, 20, FALSE);
INSERT INTO inventory (id, branch_id, variant_id, batch_id, quantity, cost_price, min_stock, deleted)
VALUES (6, 2, 2, 2, 50, 15000.0, 10, FALSE);

-- Inventory Movements
-- Movement 1: DRAFT status (Nhập từ NCC vào Kho)
INSERT INTO inventory_movements (id, movement_type, supplier_id, source_branch_id, destination_branch_id, movement_status, total_money, created_at, deleted)
VALUES (1, 'SUP_TO_WARE', 1, NULL, 1, 'DRAFT', 5000000.0, '2024-12-01 10:00:00', FALSE);

-- Movement 2: APPROVED status (Xuất từ Kho đến Chi nhánh)
INSERT INTO inventory_movements (id, movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id, movement_status, total_money, created_at, deleted)
VALUES (2, 'WARE_TO_BR', NULL, 1, 2, 1, 'APPROVED', 2500000.0, '2024-12-05 14:00:00', FALSE);

-- Movement 3: RECEIVED status (Đã nhận)
INSERT INTO inventory_movements (id, movement_type, supplier_id, source_branch_id, destination_branch_id, movement_status, total_money, created_at, deleted)
VALUES (3, 'SUP_TO_WARE', 2, NULL, 1, 'RECEIVED', 1600000.0, '2024-12-08 09:00:00', FALSE);

-- Movement 4: SHIPPED status (Đang vận chuyển)
INSERT INTO inventory_movements (id, movement_type, supplier_id, source_branch_id, destination_branch_id, movement_status, total_money, created_at, deleted)
VALUES (4, 'WARE_TO_BR', NULL, 1, 3, 'SHIPPED', 800000.0, '2024-12-09 11:00:00', FALSE);

-- Inventory Movement Details
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (1, 1, 1, 1, 500, 5000.0, 5000.0, FALSE);
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (2, 1, 2, 2, 200, 15000.0, 15000.0, FALSE);
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (3, 2, 1, 1, 100, 5000.0, 5000.0, FALSE);
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (4, 2, 2, 2, 50, 15000.0, 15000.0, FALSE);
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (5, 3, 3, 3, 200, 8000.0, 8000.0, FALSE);
INSERT INTO inventory_movement_details (id, movement_id, variant_id, batch_id, quantity, price, snap_cost, deleted)
VALUES (6, 4, 1, 1, 80, 5000.0, 5000.0, FALSE);

-- Request Details
INSERT INTO request_details (id, request_form_id, variant_id, quantity, deleted)
VALUES (1, 1, 1, 100, FALSE);
INSERT INTO request_details (id, request_form_id, variant_id, quantity, deleted)
VALUES (2, 1, 2, 50, FALSE);
INSERT INTO request_details (id, request_form_id, variant_id, quantity, deleted)
VALUES (3, 2, 3, 30, FALSE);
INSERT INTO request_details (id, request_form_id, variant_id, quantity, deleted)
VALUES (4, 3, 1, 50, FALSE);

-- Stock Adjustments (lịch sử kiểm kho)
INSERT INTO stock_adjustments (id, brand_id, variant_id, batch_id, before_quantity, after_quantity, difference_quantity, reason, created_at, deleted)
VALUES (1, 1, 1, 1, 520, 500, -20, 'Kiểm kê định kỳ - phát hiện thiếu', '2024-12-01 08:00:00', FALSE);
INSERT INTO stock_adjustments (id, brand_id, variant_id, batch_id, before_quantity, after_quantity, difference_quantity, reason, created_at, deleted)
VALUES (2, 1, 2, 2, 300, 300, 0, 'Kiểm kê định kỳ - đúng số lượng', '2024-12-01 08:00:00', FALSE);

-- ============================================================================
-- LEVEL 5: Shift Management Data
-- ============================================================================

-- Additional Staff Users for Shift Management (role_id=5 is INVENTORY/STAFF role)
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (4, 'staff1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'Nhân viên 1', 5, 2, '0945678901', 'staff1@pharma.vn', FALSE);
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (5, 'staff2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'Nhân viên 2', 5, 2, '0956789012', 'staff2@pharma.vn', FALSE);
INSERT INTO users (id, user_name, password, full_name, role_id, branch_id, phone_number, email, deleted)
VALUES (6, 'pharmacist1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'Dược sĩ 1', 4, 2, '0967890123', 'pharmacist1@pharma.vn', FALSE);

-- Shifts for Branch 2 (Manager's branch)
INSERT INTO shifts (id, branch_id, name, start_time, end_time, created_at, updated_at, deleted)
VALUES (1, 2, 'Ca Sáng', '08:00:00', '14:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shifts (id, branch_id, name, start_time, end_time, created_at, updated_at, deleted)
VALUES (2, 2, 'Ca Chiều', '14:00:00', '20:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shifts (id, branch_id, name, start_time, end_time, created_at, updated_at, deleted)
VALUES (3, 2, 'Ca Tối', '20:00:00', '02:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- Shift Assignments (assign staff to shifts)
INSERT INTO shift_assignments (id, shift_id, user_id, created_at, updated_at, deleted)
VALUES (1, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shift_assignments (id, shift_id, user_id, created_at, updated_at, deleted)
VALUES (2, 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shift_assignments (id, shift_id, user_id, created_at, updated_at, deleted)
VALUES (3, 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- Shift Works (actual work schedules)
INSERT INTO shift_works (id, assignment_id, work_date, status, created_at, updated_at, deleted)
VALUES (1, 1, CURRENT_DATE, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shift_works (id, assignment_id, work_date, status, created_at, updated_at, deleted)
VALUES (2, 1, DATEADD('DAY', 1, CURRENT_DATE), 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO shift_works (id, assignment_id, work_date, status, created_at, updated_at, deleted)
VALUES (3, 2, CURRENT_DATE, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- ============================================================================
-- LEVEL 6: Revenue Data (Customers & Invoices for Revenue Reports)
-- ============================================================================

-- Customers
INSERT INTO customers (id, name, phone, created_at, updated_at, deleted)
VALUES (1, 'Nguyễn Văn A', '0901234567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO customers (id, name, phone, created_at, updated_at, deleted)
VALUES (2, 'Trần Thị B', '0912345678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO customers (id, name, phone, created_at, updated_at, deleted)
VALUES (3, 'Lê Văn C', '0923456789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- Invoices (for revenue reports) - Branch 2
INSERT INTO invoices (id, invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, user_id, invoice_type, created_at, updated_at, deleted)
VALUES (1, 'INV-001', 1, 1, 2, 150000, 'cash', 4, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoices (id, invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, user_id, invoice_type, created_at, updated_at, deleted)
VALUES (2, 'INV-002', 2, 1, 2, 250000, 'transfer', 6, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoices (id, invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, user_id, invoice_type, created_at, updated_at, deleted)
VALUES (3, 'INV-003', 3, 3, 2, 180000, 'cash', 5, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoices (id, invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, user_id, invoice_type, created_at, updated_at, deleted)
VALUES (4, 'INV-004', 1, 1, 2, 320000, 'transfer', 4, 'PAID', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), FALSE);
INSERT INTO invoices (id, invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, user_id, invoice_type, created_at, updated_at, deleted)
VALUES (5, 'INV-005', 2, 3, 2, 95000, 'cash', 5, 'PAID', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), FALSE);

-- Invoice Details (for profit calculation)
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (1, 1, 5, 10, 5000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (2, 1, 6, 2, 50000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (3, 2, 5, 20, 5000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (4, 2, 6, 3, 50000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (5, 3, 5, 15, 5000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (6, 3, 6, 2, 52500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (7, 4, 5, 30, 5000, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (8, 4, 6, 3, 57000, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (9, 5, 5, 15, 5000, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), FALSE);
INSERT INTO invoice_details (id, invoice_id, inventory_id, quantity, price, created_at, updated_at, deleted)
VALUES (10, 5, 6, 1, 20000, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), FALSE);
