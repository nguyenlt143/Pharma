INSERT INTO roles (name, created_at, deleted)
VALUES ('ADMIN', NOW(), false),
       ('OWNER', NOW(), false),
       ('MANAGER', NOW(), false),
       ('INVENTORY', NOW(), false),
       ('WAREHOUSE', NOW(), false),
       ('PHARMACIST', NOW(), false);

INSERT INTO branchs (name, branch_type, address, user_id, created_at, deleted)
VALUES ('Tổng công ty', 'HEAD_QUARTER', 'Số 1 Phạm Hùng, Hà Nội', NULL, NOW(), FALSE),
       ('Kho hủy', 'DISPOSAL_AREA', 'Kho C - Bình Dương', NULL, NOW(), FALSE),
       ('Chi nhánh Hà Nội', 'BRANCH', '123 Đường A, Hà Nội', NULL, NOW(), FALSE),
       ('Chi nhánh TP.HCM', 'BRANCH', '456 Đường B, TP.HCM', NULL, NOW(), FALSE),
       ('Chi nhánh Đà Nẵng', 'BRANCH', '789 Đường C, Đà Nẵng', NULL, NOW(), FALSE);


INSERT INTO users (user_name, password, full_name, role_id, branch_id, phone_number, email, image_url, created_at,
                   deleted)
VALUES
    --  kho tong
    ('admin', '123456', 'Nguyễn Văn Admin', 1, 1, '0901000001', 'admin@hq.com', NULL, NOW(), FALSE),
    ('owner', '123456', 'Trần Thị Owner', 2, 1, '0902000002', 'owner@hq.com', NULL, NOW(), FALSE),
    ('warehouse', '123456', 'Lê Văn Warehouse', 5, 1, '0903000003', 'warehouse@hq.com', NULL, NOW(), FALSE),
    -- HANOI BRANCH (branch_id = 3)
    ('managerhn', '123456', 'Manager Hà Nội', 3, 3, 0901000004, 'managerhn@gmail.com', NULL, NOW(), FALSE),
    ('inventoryhn', '123456', 'Inventory Hà Nội', 4, 3, 0901000005, 'inventoryhn@gmail.com', NULL, NOW(), FALSE),
    ('pharmacist1hn', '123456', 'Pharmacist 1 Hà Nội', 6, 3, 0901000006, 'pharmacist1hn@gmail.com', NULL, NOW(), FALSE),
    ('pharmacist2hn', '123456', 'Pharmacist 2 Hà Nội', 6, 3, 0901000007, 'pharmacist2hn@gmail.com', NULL, NOW(), FALSE),
    ('pharmacist3hn', '123456', 'Pharmacist 3 Hà Nội', 6, 3, 0901000008, 'pharmacist3hn@gmail.com', NULL, NOW(), FALSE),
-- HCM BRANCH (branch_id = 4)
    ('managerhcm', '123456', 'Manager TP.HCM', 3, 4, 0901000009, 'managerhcm@gmail.com', NULL, NOW(), FALSE),
    ('inventoryhcm', '123456', 'Inventory TP.HCM', 4, 4, 0901000010, 'inventoryhcm@gmail.com', NULL, NOW(), FALSE),
    ('pharmacist1hcm', '123456', 'Pharmacist 1 TP.HCM', 6, 4, 0901000011, 'pharmacist1hcm@gmail.com', NULL, NOW(),
     FALSE),
    ('pharmacist2hcm', '123456', 'Pharmacist 2 TP.HCM', 6, 4, 0901000012, 'pharmacist2hcm@gmail.com', NULL, NOW(),
     FALSE),
    ('pharmacist3hcm', '123456', 'Pharmacist 3 TP.HCM', 6, 4, 0901000013, 'pharmacist3hcm@gmail.com', NULL, NOW(),
     FALSE),
-- DANANG BRANCH (branch_id = 5)
    ('managerdn', '123456', 'Manager Đà Nẵng', 3, 5, 0901000014, 'managerdn@gmail.com', NULL, NOW(), FALSE),
    ('inventorydn', '123456', 'Inventory Đà Nẵng', 4, 5, 0901000015, 'inventorydn@gmail.com', NULL, NOW(), FALSE),
    ('pharmacist1dn', '123456', 'Pharmacist 1 Đà Nẵng', 6, 5, 0901000016, 'pharmacist1dn@gmail.com', NULL, NOW(),
     FALSE),
    ('pharmacist2dn', '123456', 'Pharmacist 2 Đà Nẵng', 6, 5, 0901000017, 'pharmacist2dn@gmail.com', NULL, NOW(),
     FALSE),
    ('pharmacist3dn', '123456', 'Pharmacist 3 Đà Nẵng', 6, 5, 0901000018, 'pharmacist3dn@gmail.com', NULL, NOW(),
     FALSE);
-- update manager for branchs
UPDATE branchs
SET user_id = 2
WHERE id = 1;
UPDATE branchs
SET user_id = 2
WHERE id = 2;
-- Chi nhánh Hà Nội
UPDATE branchs
SET user_id = 4
WHERE id = 3;
-- Chi nhánh TP.HCM
UPDATE branchs
SET user_id = 9
WHERE id = 4;
-- Chi nhánh Đà Nẵng
UPDATE branchs
SET user_id = 14
WHERE id = 5;

INSERT INTO suppliers (name, phone, address, created_at, deleted)
VALUES ('Công ty Dược Hòa Bình', '0991000001', '123 Lê Lợi, Hà Nội', NOW(), FALSE),
       ('Công ty Dược Nam Dược', '0992000002', '45 Nguyễn Huệ, TP.HCM', NOW(), FALSE),
       ('Công ty Dược Bình Dương', '0993000003', '12 Trần Phú, Bình Dương', NOW(), FALSE);

INSERT INTO customers (name, phone, created_at, deleted)
VALUES ('Nguyễn Văn A', '0901000001', NOW(), FALSE),
       ('Trần Thị B', '0902000002', NOW(), FALSE),
       ('Lê Văn C', '0903000003', NOW(), FALSE),
       ('Phạm Thị D', '0904000004', NOW(), FALSE),
       ('Đặng Văn E', '0905000005', NOW(), FALSE),
       ('Hoàng Thị F', '0906000006', NOW(), FALSE);

INSERT INTO shifts (branch_id, start_time, end_time, name, note, created_at, deleted)
VALUES
-- Chi nhánh Hà Nội (branch_id = 3)
(3, '07:00:00', '11:30:00', 'Ca sáng Hà Nội', 'Ca sáng tại chi nhánh Hà Nội', NOW(), FALSE),
(3, '13:00:00', '17:30:00', 'Ca chiều Hà Nội', 'Ca chiều tại chi nhánh Hà Nội', NOW(), FALSE),

-- Chi nhánh TP.HCM (branch_id = 4)
(4, '07:00:00', '11:30:00', 'Ca sáng TP.HCM', 'Ca sáng tại chi nhánh TP.HCM', NOW(), FALSE),
(4, '13:00:00', '17:30:00', 'Ca chiều TP.HCM', 'Ca chiều tại chi nhánh TP.HCM', NOW(), FALSE),

-- Chi nhánh Đà Nẵng (branch_id = 5)
(5, '07:00:00', '12:00:00', 'Ca sáng Đà Nẵng', 'Ca sáng tại chi nhánh Đà Nẵng', NOW(), FALSE),
(5, '13:00:00', '17:30:00', 'Ca chiều Đà Nẵng', 'Ca chiều tại chi nhánh Đà Nẵng', NOW(), FALSE);

INSERT INTO shift_assignments (user_id, shift_id, created_at, deleted)
VALUES
    -- Hà Nội branch (branch_id = 3)
    (6, 1, NOW(), FALSE),  -- pharmacist1hn
    (7, 1, NOW(), FALSE),  -- pharmacist2hn
    (8, 2, NOW(), FALSE),-- pharmacist3hn
    -- TP.HCM branch (branch_id = 4)
    (11, 3, NOW(), FALSE), -- pharmacist1hcm
    (12, 3, NOW(), FALSE), -- pharmacist2hcm
    (13, 4, NOW(), FALSE), -- pharmacist3hcm
    -- Đà Nẵng branch (branch_id = 5)
    (16, 5, NOW(), FALSE), -- pharmacist1dn
    (17, 5, NOW(), FALSE), -- pharmacist2dn
    (18, 6, NOW(), FALSE);
-- pharmacist3dn

-- Hà Nội
INSERT INTO shift_works (assignment_id, work_date, created_at, deleted)
VALUES (1, '2025-11-16', NOW(), FALSE),
       (1, '2025-11-17', NOW(), FALSE),
       (1, '2025-11-18', NOW(), FALSE),
       (2, '2025-11-16', NOW(), FALSE),
       (2, '2025-11-17', NOW(), FALSE),
       (2, '2025-11-18', NOW(), FALSE),
       (3, '2025-11-16', NOW(), FALSE),
       (3, '2025-11-17', NOW(), FALSE),
       (3, '2025-11-18', NOW(), FALSE),
-- TP.HCM
       (4, '2025-11-16', NOW(), FALSE),
       (5, '2025-11-16', NOW(), FALSE),
       (6, '2025-11-16', NOW(), FALSE),
-- TP DN
       (7, '2025-11-16', NOW(), FALSE),
       (8, '2025-11-16', NOW(), FALSE),
       (9, '2025-11-16', NOW(), FALSE);

INSERT INTO units (name, description, created_at, deleted)
VALUES ('Viên', 'Dạng đơn vị thuốc nhỏ nhất, thường dùng cho thuốc viên nén hoặc viên nang', NOW(), FALSE),
       ('Vỉ', 'Gồm nhiều viên thuốc (thường 10 viên) đóng chung trong một vỉ nhôm hoặc nhựa', NOW(), FALSE),
       ('Hộp', 'Bao gồm nhiều vỉ hoặc gói thuốc, đơn vị bán phổ biến tại quầy', NOW(), FALSE),
       ('Chai', 'Đựng dung dịch, siro hoặc thuốc nhỏ giọt', NOW(), FALSE),
       ('Ống', 'Dùng cho thuốc tiêm, thuốc uống dạng ống nhựa hoặc thủy tinh', NOW(), FALSE),
       ('Gói', 'Thường dùng cho thuốc bột, cốm hoặc thuốc hòa tan', NOW(), FALSE),
       ('Tuýp', 'Dùng cho thuốc bôi, kem, gel hoặc thuốc mỡ', NOW(), FALSE),
       ('Lọ', 'Đựng viên nén, viên nang hoặc dung dịch nhỏ', NOW(), FALSE),
       ('Thùng', 'Đơn vị bao gồm nhiều hộp hoặc chai, thường dùng trong nhập hàng', NOW(), FALSE),
       ('Đơn vị', 'Đơn vị tính tổng quát, dùng khi chưa xác định rõ quy cách', NOW(), FALSE),
       ('Gói nhỏ', 'Dùng cho thuốc hòa tan dạng gói nhỏ', NOW(), FALSE);

INSERT INTO CATEGORYS (name, description, deleted)
VALUES
-- LEVEL 1 (PARENT)
('Thuốc điều trị', 'Nhóm thuốc điều trị bệnh phổ biến', FALSE),                                 -- ID = 1
('Thuốc theo bệnh lý', 'Thuốc điều trị theo nhóm bệnh và cơ quan', FALSE),                      -- ID = 2
('Chăm sóc sức khỏe và bổ trợ', 'Vitamin, khoáng chất và sản phẩm tăng cường sức khỏe', FALSE), -- ID = 3
('Sản phẩm cho trẻ em', 'Sản phẩm và thuốc dành riêng cho trẻ em', FALSE),
-- LEVEL 2 – CHILD OF: Thuốc điều trị (Parent = 1)
('Thuốc cảm cúm', 'Điều trị cảm lạnh, nghẹt mũi, sổ mũi', FALSE),
('Thuốc ho – long đờm', 'Giảm ho và hỗ trợ tiêu đờm', FALSE),
('Thuốc hạ sốt – giảm đau ', 'Hạ sốt, giảm đau ', FALSE),
('Thuốc dị ứng – kháng histamin', 'Giảm dị ứng, mề đay, ngứa, nổi mẩn', FALSE),
('Thuốc sát khuẩn – khử trùng', 'Sát khuẩn, vệ sinh và ngừa nhiễm trùng', FALSE),
-- LEVEL 2 – CHILD OF: Thuốc theo bệnh lý (Parent = 2)
('Dạ dày – tiêu hóa', 'Điều trị các vấn đề về dạ dày và tiêu hóa', FALSE),
('Tim mạch – huyết áp', 'Hỗ trợ và điều trị bệnh tim mạch và huyết áp', FALSE),
('Xương khớp – đau nhức', 'Giảm đau nhức và hỗ trợ xương khớp', FALSE),
('Gan – giải độc', 'Hỗ trợ chức năng gan và giải độc', FALSE),
('Thần kinh – giấc ngủ', 'Giảm căng thẳng, lo âu và hỗ trợ giấc ngủ', FALSE),
-- LEVEL 2 – CHILD OF: Chăm sóc sức khỏe và bổ trợ (Parent = 3)
('Vitamin và khoáng chất', 'Bổ sung vitamin và khoáng chất thiết yếu', FALSE),
('Tăng đề kháng – miễn dịch', 'Tăng cường sức đề kháng và hệ miễn dịch', FALSE),
('Điện giải – dinh dưỡng', 'Bù nước, bù điện giải và bổ sung dinh dưỡng', FALSE),
('Hỗ trợ tiêu hóa – men vi sinh', 'Men vi sinh và sản phẩm hỗ trợ tiêu hóa', FALSE),
('Sức khỏe phụ nữ', 'Sản phẩm chăm sóc và tăng cường sức khỏe cho phụ nữ', FALSE),
-- LEVEL 2 – CHILD OF: Sản phẩm cho trẻ em (Parent = 4)
('Thuốc cảm – ho – sốt cho trẻ', 'Điều trị cảm, ho và sốt cho trẻ em', FALSE),
('Vitamin và khoáng chất trẻ em', 'Bổ sung vitamin và khoáng chất cho trẻ em', FALSE),
('Điện giải và tiêu hóa cho trẻ', 'Bù điện giải và hỗ trợ tiêu hóa cho trẻ em', FALSE),
('Xịt mũi – nhỏ mũi trẻ em', 'Vệ sinh và hỗ trợ thông mũi cho trẻ', FALSE),
('Dinh dưỡng trẻ em', 'Sản phẩm dinh dưỡng và phát triển cho trẻ em', FALSE);

INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, category_id, created_at, deleted)
VALUES
    -- ====== NHÓM THUỐC CẢM CÚM 5======
    ('Paracetamol', 'Paracetamol', 'Panadol', 'GlaxoSmithKline', 'Anh', 1, NOW(), FALSE),
    ('Decolgen', 'Paracetamol + Phenylephrine HCl + Chlorpheniramine Maleate', 'Decolgen', 'United Pharma',
     'Philippines', 1, NOW(), FALSE),
    ('Tiffy', 'Paracetamol + Chlorpheniramine Maleate', 'Tiffy', 'Medica Laboratories', 'Thái Lan', 1, NOW(), FALSE),
    ('Aspirin', 'Acetylsalicylic Acid', 'Aspirin Bayer', 'Bayer AG', 'Đức', 1, NOW(), FALSE),
    ('Coldrex MaxGrip', 'Paracetamol + Phenylephrine HCl + Vitamin C', 'Coldrex', 'GlaxoSmithKline', 'Anh', 1, NOW(),
     FALSE),
    ('Vicks Formula 44', 'Dextromethorphan HBr 15mg/5ml', 'Vicks', 'Procter & Gamble', 'Mỹ', 1, NOW(), FALSE),

-- ====== NHÓM THUỐC HO 6======
    ('Prospan Syrup ', 'Hedera Helix Extract 7mg/ml', 'Prospan', 'Engelhard Arzneimittel', 'Đức', 2, NOW(), FALSE),
    ('Atussin Syrup ', 'Guaifenesin 100mg/5ml + Dextromethorphan HBr 10mg/5ml + Chlorpheniramine Maleate 2mg/5ml',
     'Atussin', 'DHG Pharma', 'Việt Nam', 2, NOW(), FALSE),
    ('Bromhexine ', 'Bromhexine Hydrochloride 8mg', 'Bromhexine Stella', 'Stella Pharma', 'Việt Nam', 2, NOW(), FALSE),
    ('Terpin Codein', 'Codeine Phosphate 10mg + Terpin Hydrate 100mg', 'Terpin Codein', 'Imexpharm', 'Việt Nam', 2,
     NOW(), FALSE),
    ('Ho Pha Viên', 'Dextromethorphan HBr + Guaifenesin', 'Ho Pha', 'Medipharm', 'Việt Nam', 2, NOW(), FALSE),

-- ====== NHÓM THUỐC HẠ SỐT – GIẢM ĐAU 7======
    ('Panadol Extra ', 'Paracetamol + Caffeine', 'Panadol', 'GlaxoSmithKline', 'Anh', 3, NOW(), FALSE),
    ('Efferalgan ', 'Paracetamol', 'Efferalgan', 'Bristol-Myers', 'Pháp', 3, NOW(), FALSE),
    ('Ibuprofen ', 'Ibuprofen', 'Nurofen', 'Reckitt Benckiser', 'Anh', 3, NOW(), FALSE),

    -- Thuốc dị ứng – kháng histamin (ID = 8)
    ('Cetirizine', 'Cetirizine 10mg', 'Zyrtec', 'UCB Pharma', 'Belgium', 4, NOW(), FALSE),
    ('Loratadine', 'Loratadine 10mg', 'Claritine', 'Bayer', 'Germany', 4, NOW(), FALSE),
    ('Fexofenadine', 'Fexofenadine Hydrochloride 180mg', 'Telfast', 'Sanofi', 'France', 4, NOW(), FALSE),
    -- Thuốc sát khuẩn – khử trùng (ID = 9)
    ('Betadine', 'Povidone Iodine 10%', 'Betadine', 'Mundipharma', 'Singapore', 5, NOW(), FALSE),
    ('Oxy già', 'Hydrogen Peroxide 3%', 'Hydrogen Peroxide', 'Medipharma', 'Vietnam', 5, NOW(), FALSE),
    -- Dạ dày – tiêu hóa (ID = 10)
    ('Omeprazole', 'Omeprazole 20mg', 'Losec', 'AstraZeneca', 'UK', 6, NOW(), FALSE),
    ('Esomeprazole', 'Esomeprazole 40mg', 'Nexium', 'AstraZeneca', 'Sweden', 6, NOW(), FALSE),
    ('Domperidone', 'Domperidone 10mg', 'Motilium', 'Janssen', 'Belgium', 6, NOW(), FALSE),
    -- Tim mạch – huyết áp (ID = 11
    ('Amlodipine', 'Amlodipine 5mg', 'Amlor', 'Pfizer', 'USA', 7, NOW(), FALSE),
    ('Losartan', 'Losartan Potassium 50mg', 'Cozaar', 'Merck Sharp & Dohme', 'USA', 7, NOW(), FALSE),
    ('Bisoprolol', 'Bisoprolol Fumarate 5mg', 'Concor', 'Merck', 'Germany', 7, NOW(), FALSE),
    -- Xương khớp – đau nhức (ID = 12)
    ('Glucosamine', 'Glucosamine Sulfate 1500mg', 'Schiff Glucosamine', 'Schiff', 'USA', 8, NOW(), FALSE),
    ('Meloxicam', 'Meloxicam 7.5mg', 'Mobic', 'Boehringer Ingelheim', 'Germany', 8, NOW(), FALSE),
    -- Gan – giải độc (ID = 13)
    ('Essentiale Forte', 'Phospholipid Extract', 'Essentiale', 'Sanofi', 'France', 9, NOW(), FALSE),
    ('LiverGold', 'Milk Thistle Extract', 'LiverGold', 'Nature''s Way', 'USA', 9, NOW(), FALSE),
    -- Thần kinh – giấc ngủ (ID = 14)
    ('Melatonin', 'Melatonin 3mg', 'Natrol Melatonin', 'Natrol', 'USA', 10, NOW(), FALSE),
    ('Magnesium B6', 'Magnesium + Vitamin B6', 'MagB6', 'Sanofi', 'France', 10, NOW(), FALSE),
    -- Vitamin và khoáng chất (ID = 15)
    ('Centrum', 'Multivitamins & Minerals', 'Centrum', 'Pfizer', 'USA', 11, NOW(), FALSE),
    -- Tăng đề kháng – miễn dịch (ID = 16)
    ('Vitamin C', 'Ascorbic Acid 1000mg', 'Vitamin C', 'Blackmores', 'Australia', 12, NOW(), FALSE),
    -- Điện giải – dinh dưỡng (ID = 17)
    ('ORS', 'Oral Rehydration Salts', 'ORESOL', 'DHG Pharma', 'Vietnam', 13, NOW(), FALSE),
    -- Hỗ trợ tiêu hóa – men vi sinh (ID = 18
    ('BioGaia', 'Lactobacillus reuteri Protectis', 'BioGaia', 'BioGaia AB', 'Sweden', 14, NOW(), FALSE),
    -- Sức khỏe phụ nữ (ID = 19)
    ('Evening Primrose Oil', 'Evening Primrose Oil 1000mg', 'EPO', 'Blackmores', 'Australia', 15, NOW(), FALSE),
    -- Thuốc cảm – ho – sốt cho trẻ (ID = 20
    ('Tylenol Children', 'Acetaminophen 160mg/5ml', 'Tylenol Children', 'Johnson & Johnson', 'USA', 16, NOW(), FALSE),
    -- Vitamin và khoáng chất trẻ em (ID = 21)
    ('Kids Smart Vita Gummies', 'Multivitamins for Kids', 'Nature', ' Way', 'Australia', 17, NOW(), FALSE),
    -- Điện giải và tiêu hóa cho trẻ (ID = 22)
    ('Hydrite', 'ORS for Kids', 'Hydrite', 'United Pharma', 'Philippines', 18, NOW(), FALSE),
    -- Xịt mũi – nhỏ mũi trẻ em (ID = 23)
    ('Sterimar Baby', 'aaa', 'sea water', 'Sterimar', 'France', 19, NOW(), FALSE),
    -- Dinh dưỡng trẻ em (ID = 24)
    ('PediaSure', 'Child Nutrition Formula', 'PediaSure', 'Abbott', 'USA', 20, NOW(), FALSE);

INSERT INTO medicine_variant
(dosage_form, dosage, strength, package_unit_id, base_unit_id, quantity_per_package, barcode, registration_number,
 storage_conditions, indications, contraindications, side_effects, instructions, prescription_require, uses,
 medicine_id, created_at, deleted)
VALUES
    -- 1. Paracetamol (OTC + 1 variant kê đơn)
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000011', 'VN-10001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban',
     'Không quá 8 viên/ngày', FALSE, 'Uống với nhiều nước', 1, NOW(), FALSE),
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '650mg', 3, 1, 10, '8935000000012', 'VN-10002',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban',
     'Không quá 8 viên/ngày', TRUE, 'Uống với nhiều nước', 1, NOW(), FALSE),
    ('Viên sủi', 'Uống 1 gói mỗi 6 giờ nếu cần', '500mg', 6, 1, 10, '8935000000013', 'VN-10003',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban',
     'Hòa tan trong nước', FALSE, 'Uống sau bữa ăn', 1, NOW(), FALSE),
-- 2. Decolgen (OTC)
    ('Viên nén', 'Uống 1 viên mỗi 8 giờ', '500mg', 3, 1, 10, '8935000000021', 'VN-10004',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt, nghẹt mũi', 'Mẫn cảm với bất kỳ thành phần nào',
     'Buồn nôn, chóng mặt', 'Uống với nước đầy', FALSE, 'Sau bữa ăn', 2, NOW(), FALSE),
    ('Viên nén', 'Uống 1 viên mỗi 8 giờ', '500mg', 3, 1, 20, '8935000000022', 'VN-10005',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt, nghẹt mũi', 'Mẫn cảm với bất kỳ thành phần nào',
     'Buồn nôn, chóng mặt', 'Uống với nước đầy', FALSE, 'Sau bữa ăn', 2, NOW(), FALSE),
-- 3. Tiffy (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000031', 'VN-10006',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Hiếm gặp dị ứng',
     'Không quá 8 viên/ngày', FALSE, 'Uống với nhiều nước', 3, NOW(), FALSE),
    ('Viên sủi', 'Uống 1 gói mỗi 6 giờ nếu cần', '500mg', 6, 1, 10, '8935000000032', 'VN-10007',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Hiếm gặp dị ứng',
     'Hòa tan trong nước', FALSE, 'Sau bữa ăn', 3, NOW(), FALSE),
-- 4. Aspirin 500mg (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000041', 'VN-10008',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Aspirin', 'Buồn nôn, đau bụng',
     'Không quá 8 viên/ngày', FALSE, 'Uống sau bữa ăn', 4, NOW(), FALSE),
-- 5. Coldrex MaxGrip (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000051', 'VN-10009',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt, nghẹt mũi', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn',
     'Uống với nước đầy', FALSE, 'Sau bữa ăn', 5, NOW(), FALSE),
-- 6. Vicks Formula 44 (OTC)
    ('Syrup', 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 4, 4, 1, '8935000000061', 'VN-10010',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn, chóng mặt',
     'Lắc đều trước khi dùng', FALSE, 'Sau bữa ăn', 6, NOW(), FALSE),
-- 7. Prospan Syrup (OTC)
    ('Syrup', 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 4, 4, 1, '8935000000071', 'VN-10011',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho, long đờm', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn, chóng mặt',
     'Lắc đều trước khi dùng', FALSE, 'Sau bữa ăn', 7, NOW(), FALSE),
-- 8. Atussin Syrup (OTC)
    ('Syrup', 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 4, 4, 1, '8935000000081', 'VN-10012',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho, long đờm', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn, chóng mặt',
     'Lắc đều trước khi dùng', FALSE, 'Sau bữa ăn', 8, NOW(), FALSE),
-- 9. Bromhexine 8mg (OTC)
    ('Viên nén', 'Uống 1 viên mỗi 8 giờ nếu cần', '8mg', 3, 1, 10, '8935000000091', 'VN-10013',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho', 'Mẫn cảm với Bromhexine', 'Buồn nôn', 'Uống với nước đầy', FALSE,
     'Sau bữa ăn', 9, NOW(), FALSE),
-- 10. Terpin Codein (OTC)
    ('Viên nén', 'Uống 1 viên mỗi 8 giờ nếu cần', '10mg + 100mg', 3, 1, 10, '8935000000101', 'VN-10014',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho', 'Mẫn cảm với Codein hoặc Terpin', 'Buồn nôn, chóng mặt',
     'Uống với nước đầy', FALSE, 'Sau bữa ăn', 10, NOW(), FALSE),
-- 11. Ho Pha Viên (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 8 giờ nếu cần', '500mg', 3, 1, 10, '8935000000111', 'VN-10015',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm ho', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn', 'Uống với nước đầy',
     FALSE, 'Sau bữa ăn', 11, NOW(), FALSE),
-- 12. Panadol Extra 500mg (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg + Caffeine', 3, 1, 10, '8935000000121', 'VN-10016',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol hoặc Caffeine',
     'Buồn nôn, phát ban', 'Uống với nước đầy', FALSE, 'Sau bữa ăn', 12, NOW(), FALSE),
-- 13. Efferalgan 500mg (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000131', 'VN-10017',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban',
     'Uống với nước đầy', FALSE, 'Sau bữa ăn', 13, NOW(), FALSE),
-- 14. Ibuprofen 400mg (OTC)
    ('Viên nén', 'Uống 1–2 viên mỗi 6–8 giờ nếu cần', '400mg', 3, 1, 10, '8935000000141', 'VN-10018',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau, hạ sốt', 'Mẫn cảm với Ibuprofen', 'Buồn nôn, đau dạ dày',
     'Uống với nước đầy', FALSE, 'Sau bữa ăn', 14, NOW(), FALSE),
    -- Dị ứng 15: Cetirizine
    ('Viên nén', 'Uống 1 viên mỗi ngày khi cần', '10mg', 3, 1, 10, '8935000001501', 'VN-15001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm triệu chứng dị ứng: ngứa, chảy nước mũi, sổ mũi', 'Quá mẫn với cetirizine',
     'Buồn ngủ nhẹ, mệt mỏi',
     'Uống nguyên viên; không lái xe nếu thấy buồn ngủ', FALSE, 'Giảm triệu chứng dị ứng', 15, NOW(), FALSE),
    -- Dị ứng 15: Cetirizine (variant lớn hộp)
    ('Viên nén', 'Uống 1 viên mỗi ngày khi cần', '10mg', 3, 1, 30, '8935000001502', 'VN-15002',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm triệu chứng dị ứng: ngứa, chảy nước mũi, sổ mũi', 'Quá mẫn với cetirizine',
     'Buồn ngủ nhẹ, mệt mỏi',
     'Uống nguyên viên; không lái xe nếu thấy buồn ngủ', FALSE, 'Giảm triệu chứng dị ứng', 15, NOW(), FALSE),
    -- Dị ứng 16: Loratadine
    ('Viên nén', 'Uống 1 viên mỗi ngày', '10mg', 3, 1, 10, '8935000001601', 'VN-16001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm dị ứng, mề đay, ngứa', 'Quá mẫn với loratadine', 'Khô miệng, đau đầu',
     'Uống nguyên viên, 1 lần/ngày', FALSE, 'Giảm dị ứng', 16, NOW(), FALSE),
    -- Dị ứng 17: Fexofenadine (OTC thấp liều)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '60mg', 3, 1, 10, '8935000001701', 'VN-17001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm triệu chứng dị ứng', 'Quá mẫn với fexofenadine', 'Nhức đầu, buồn nôn',
     'Uống nguyên viên, mỗi ngày', FALSE, 'Giảm dị ứng nhẹ', 17, NOW(), FALSE),
    -- Dị ứng 17: Fexofenadine (Rx cao liều)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '180mg', 3, 1, 10, 'INT0000001702', 'INT-17002',
     'Bảo quản nơi khô, dưới 30°C', 'Dị ứng nặng hoặc không đáp ứng với liệu pháp khác', 'Quá mẫn với fexofenadine',
     'Nhức đầu, mệt mỏi',
     'Dùng theo chỉ định bác sĩ', TRUE, 'Dị ứng trung bình–nặng', 17, NOW(), FALSE),
    -- Sát khuẩn 18: Betadine (chất sát khuẩn)
    ('Dung dịch bôi', 'Rửa hoặc bôi vùng cần sát khuẩn 1–2 lần/ngày', 'Povidone Iodine 10%', 8, 8, 100, '8935000001801',
     'VN-18001',
     'Bảo quản nơi khô, tránh ánh nắng trực tiếp', 'Sát khuẩn vết thương, vệ sinh da',
     'Không dùng ở vùng có tổn thương nặng hoặc rách da lớn', 'Kích ứng da hiếm gặp',
     'Rửa vết thương, bôi một lớp mỏng', FALSE, 'Sát khuẩn ngoài da', 18, NOW(), FALSE),
    -- Sát khuẩn 19: Oxy già (Hydrogen Peroxide)
    ('Dung dịch', 'Rửa vùng cần sát khuẩn, sau đó rửa lại bằng nước', 'Hydrogen Peroxide 3%', 4, 8, 100,
     '8935000001901', 'VN-19001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Vệ sinh vết thương, sát khuẩn nhẹ',
     'Không nuốt, không dùng cho vết thương sâu', 'Rát nhẹ khi bôi',
     'Sử dụng ngoài da, rửa kỹ sau khi dùng', FALSE, 'Vệ sinh vết thương', 19, NOW(), FALSE),
    -- Dạ dày 20: Omeprazole (OTC thấp liều)
    ('Viên nang', 'Uống 1 viên mỗi ngày trước bữa sáng', '10mg', 3, 1, 14, '8935000002001', 'VN-20001',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Giảm triệu chứng ợ nóng, trào ngược nhẹ', 'Quá mẫn với omeprazole',
     'Đau đầu, tiêu chảy',
     'Uống nguyên viên trước ăn', FALSE, 'Giảm ợ nóng/GERD nhẹ', 20, NOW(), FALSE),
    -- Dạ dày 20: Omeprazole (Rx cao liều)
    ('Viên nang', 'Uống 1 viên mỗi ngày trước bữa sáng', '20mg', 3, 1, 28, 'INT0000002002', 'INT-20002',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Điều trị loét, GERD trung bình–nặng', 'Quá mẫn với omeprazole',
     'Đau đầu, tiêu chảy, buồn nôn',
     'Dùng theo hướng dẫn bác sĩ', TRUE, 'Điều trị loét/GERD', 20, NOW(), FALSE),
    -- Dạ dày 21: Esomeprazole (thường Rx)
    ('Viên nang', 'Uống 1 viên mỗi ngày trước ăn', '40mg', 3, 1, 28, 'INT0000002101', 'INT-21001',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Điều trị GERD, loét dạ dày tá tràng', 'Quá mẫn với esomeprazole',
     'Đau đầu, tiêu chảy',
     'Dùng theo chỉ định bác sĩ', TRUE, 'Điều trị loét/GERD', 21, NOW(), FALSE),
    -- Dạ dày 22: Domperidone (thường Rx)
    ('Viên nén', 'Uống 1 viên trước bữa ăn, 3 lần/ngày', '10mg', 3, 1, 20, '8935000002201', 'VN-22001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm buồn nôn, nôn, hỗ trợ tiêu hóa', 'Quá mẫn với domperidone; bệnh tim nặng',
     'Mệt mỏi, khô miệng',
     'Dùng theo hướng dẫn; không dùng quá liều khuyến cáo', TRUE, 'Giảm buồn nôn, nôn', 22, NOW(), FALSE),
    -- Tim mạch 23: Amlodipine (Rx)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '5mg', 3, 1, 30, 'INT0000002301', 'INT-23001',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Hạ huyết áp, điều trị tăng huyết áp', 'Suy tim nặng chưa kiểm soát, quá mẫn',
     'Chóng mặt, phù chân',
     'Uống vào cùng một thời điểm mỗi ngày', TRUE, 'Điều trị tăng huyết áp', 23, NOW(), FALSE),
    -- Tim mạch 24: Losartan (Rx)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '50mg', 3, 1, 30, 'INT0000002401', 'INT-24001',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Hỗ trợ điều trị tăng huyết áp', 'Phụ nữ có thai, quá mẫn với losartan',
     'Tiêu chảy, chóng mặt',
     'Uống theo chỉ dẫn bác sĩ', TRUE, 'Điều trị huyết áp', 24, NOW(), FALSE),
    -- Tim mạch 25: Bisoprolol (Rx)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '5mg', 3, 1, 28, 'INT0000002501', 'INT-25001',
     'Bảo quản nơi khô, nhiệt độ phòng', 'Hỗ trợ điều trị tăng huyết áp, suy tim', 'Hen, block tim nặng, quá mẫn',
     'Mệt mỏi, mạch chậm',
     'Dùng theo chỉ định bác sĩ', TRUE, 'Tăng huyết áp, suy tim', 25, NOW(), FALSE),
    -- Xương khớp 26: Glucosamine (OTC)
    ('Viên nang', 'Uống 1 viên mỗi ngày', '1500mg', 3, 1, 30, '8935000002601', 'VN-26001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Hỗ trợ sụn khớp, giảm đau nhẹ xương khớp', 'Quá mẫn với thành phần',
     'Tiêu hóa nhẹ, buồn nôn',
     'Uống cùng bữa ăn nếu đau dạ dày', FALSE, 'Hỗ trợ xương khớp', 26, NOW(), FALSE),
    -- Xương khớp 27: Meloxicam (Rx, NSAID)
    ('Viên nén', 'Uống 1 viên mỗi ngày', '7.5mg', 3, 1, 20, 'INT0000002701', 'INT-27001',
     'Bảo quản nơi khô, dưới 30°C', 'Giảm đau viêm xương khớp', 'Loét tiêu hóa, suy thận nặng, quá mẫn',
     'Đau dạ dày, chóng mặt',
     'Dùng theo chỉ định; uống sau ăn để giảm kích ứng dạ dày', TRUE, 'Giảm đau viêm', 27, NOW(), FALSE),
    -- Gan 28: Essentiale Forte (OTC / hỗ trợ gan)
    ('Viên nang', 'Uống 1 viên, 2–3 lần/ngày', '300mg phospholipid', 3, 1, 30, '8935000002801', 'VN-28001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Hỗ trợ chức năng gan, giải độc', 'Quá mẫn với thành phần', 'Tiêu hóa nhẹ',
     'Uống sau ăn để hỗ trợ hấp thu', FALSE, 'Hỗ trợ chức năng gan', 28, NOW(), FALSE),
    -- Gan 29: LiverGold (Milk Thistle, OTC)
    ('Viên nang', 'Uống 1 viên mỗi ngày', 'Silymarin 140mg', 3, 1, 30, '8935000002901', 'VN-29001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Hỗ trợ giải độc gan', 'Quá mẫn với cây kế sữa', 'Tiêu hóa nhẹ',
     'Uống sau ăn', FALSE, 'Hỗ trợ chức năng gan', 29, NOW(), FALSE),
    -- Thần kinh 30: Melatonin (OTC)
    ('Viên nén', 'Uống 1 viên trước khi ngủ', '3mg', 3, 1, 30, '8935000003001', 'VN-30001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Hỗ trợ giấc ngủ, giảm thời gian khó ngủ',
     'Phụ nữ mang thai cho con bú chưa có nghiên cứu đầy đủ', 'Buồn ngủ, chóng mặt',
     'Uống 30 phút trước khi ngủ; không lái xe nếu buồn ngủ', FALSE, 'Hỗ trợ giấc ngủ', 30, NOW(), FALSE),
    -- Thần kinh 31: Magnesium B6 (OTC)
    ('Viên nén', 'Uống 1 viên mỗi ngày', 'Magnesium + Vitamin B6', 3, 1, 30, '8935000003101', 'VN-31001',
     'Bảo quản nơi khô, tránh ánh nắng', 'Hỗ trợ giảm căng cơ, mệt mỏi, cải thiện giấc ngủ', 'Quá mẫn với thành phần',
     'Tiêu hóa nhẹ',
     'Uống sau ăn nếu gây khó chịu dạ dày', FALSE, 'Hỗ trợ thần kinh và giấc ngủ', 31, NOW(), FALSE);

INSERT INTO batches
(batch_code, mfg_date, expiry_date, source_movement_id, total_received, batch_status, variant_id,
 supplier_id, created_at, deleted)
VALUES
-- ===== Paracetamol variants =====
('BATCH-PARA-500-2024-01', '2024-01-10', '2026-01-10', NULL, 5000, 'ACTIVE', 1, 1, NOW(), FALSE),
('BATCH-PARA-650-2024-02', '2024-02-05', '2026-02-05', NULL, 3000, 'ACTIVE', 2, 1, NOW(), FALSE),
('BATCH-PARA-SF-2024-03', '2024-03-01', '2026-03-01', NULL, 2000, 'ACTIVE', 3, 1, NOW(), FALSE),

-- ===== Decolgen variants =====
('BATCH-DECO-2024-01', '2024-03-15', '2026-03-15', NULL, 4000, 'ACTIVE', 4, 2, NOW(), FALSE),
('BATCH-DECO-2024-02', '2024-04-10', '2026-04-10', NULL, 3000, 'ACTIVE', 5, 2, NOW(), FALSE),

-- ===== Tiffy variants =====
('BATCH-TIFFY-500-2023-12', '2023-12-01', '2025-12-01', NULL, 3000, 'EXHAUSTED', 6, 2, NOW(), FALSE),
('BATCH-TIFFY-SF-2024-01', '2024-01-10', '2026-01-10', NULL, 1500, 'ACTIVE', 7, 2, NOW(), FALSE),

-- ===== Aspirin 500mg =====
('BATCH-ASP-2023-06', '2023-06-01', '2025-06-01', NULL, 2000, 'ACTIVE', 8, 1, NOW(), FALSE),

-- ===== Coldrex MaxGrip =====
('BATCH-COLD-2024-01', '2024-01-15', '2026-01-15', NULL, 3500, 'ACTIVE', 9, 1, NOW(), FALSE),

-- ===== Vicks Formula 44 =====
('BATCH-VICKS-2023-11', '2023-11-01', '2025-11-01', NULL, 2500, 'ACTIVE', 10, 3, NOW(), FALSE),

-- ===== Prospan Syrup =====
('BATCH-PRO-2023-04', '2023-04-20', '2025-04-20', NULL, 1200, 'EXHAUSTED', 11, 3, NOW(), FALSE),

-- ===== Atussin Syrup =====
('BATCH-ATUS-2022-09', '2022-09-01', '2024-09-01', NULL, 1000, 'EXPIRED', 12, 3, NOW(), FALSE),

-- ===== Bromhexine 8mg =====
('BATCH-BROM-2024-02', '2024-02-10', '2026-02-10', NULL, 2500, 'ACTIVE', 13, 2, NOW(), FALSE),

-- ===== Terpin Codein =====
('BATCH-TERP-2023-05', '2023-05-01', '2025-05-01', NULL, 1500, 'DISPOSED', 14, 2, NOW(), FALSE),

-- ===== Ho Pha Viên =====
('BATCH-HOPHA-2024-01', '2024-01-20', '2026-01-20', NULL, 2000, 'ACTIVE', 15, 3, NOW(), FALSE),

-- ===== Panadol Extra 500mg =====
('BATCH-PANA-2024-01', '2024-01-10', '2026-01-10', NULL, 3000, 'ACTIVE', 16, 1, NOW(), FALSE),

-- ===== Efferalgan 500mg =====
('BATCH-EFFER-2024-01', '2024-01-15', '2026-01-15', NULL, 2500, 'ACTIVE', 17, 1, NOW(), FALSE),

-- ===== Ibuprofen 400mg =====
('BATCH-IBU-2024-01', '2024-01-05', '2026-01-05', NULL, 2000, 'ACTIVE', 18, 2, NOW(), FALSE),

-- ===== Amoxicillin 500mg =====
('BATCH-AMOX-2024-01', '2024-01-10', '2026-01-10', NULL, 1500, 'ACTIVE', 19, 1, NOW(), FALSE),

-- ===== Azithromycin 250mg =====
('BATCH-AZI-2024-01', '2024-01-12', '2026-01-12', NULL, 1200, 'ACTIVE', 20, 1, NOW(), FALSE),

-- ===== Cefixime 200mg =====
('BATCH-CEF-2024-01', '2024-01-15', '2026-01-15', NULL, 1000, 'ACTIVE', 21, 1, NOW(), FALSE);

INSERT INTO request_forms (branch_id, request_type, request_status, note, created_at, deleted)
VALUES (3, 'IMPORT', 'CONFIRMED', 'IMPORT: yêu cầu nhập nhóm giảm đau, đợt 1', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: yêu cầu nhập nhóm cảm cúm, đợt 2', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: yêu cầu nhập nhóm ho, đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Vitamin & dinh dưỡng', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung men tiêu hóa & dạ dày', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung kháng sinh nhẹ', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: nhập nhóm sát khuẩn và băng gạc', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: chiến dịch khuyến mại mùa lạnh', NOW(), FALSE),
       (3, 'RETURN', 'CONFIRMED', 'RETURN: trả Decolgen lỗi bao bì (sample)', NOW(), FALSE),
       (3, 'RETURN', 'CONFIRMED', 'RETURN: trả Atussin hết hạn', NOW(), FALSE);

INSERT INTO request_details (request_form_id, variant_id, quantity, deleted)
VALUES
-- Request 1
(1, 1, 600, FALSE),
(1, 6, 300, FALSE),
(1, 4, 200, FALSE),
-- Request 2
(2, 1, 400, FALSE),
(2, 3, 250, FALSE),
(2, 2, 200, FALSE),
-- Request 3
(3, 11, 150, FALSE),
(3, 12, 120, FALSE),
(3, 13, 200, FALSE),
-- Request 4
(4, 5, 500, FALSE),
(4, 6, 200, FALSE),
-- Request 5
(5, 20, 300, FALSE),
(5, 22, 150, FALSE),
-- Request 6
(6, 7, 250, FALSE),
(6, 8, 180, FALSE),
-- Request 7
(7, 18, 200, FALSE),
(7, 19, 150, FALSE),
-- Request 8
(8, 1, 800, FALSE),
(8, 5, 400, FALSE),
(8, 6, 300, FALSE),
-- Request 9 (RETURN)
(9, 4, 60, FALSE),
-- Request 10 (RETURN)
(10, 12, 40, FALSE);

-- =========================================================
-- 3) SUP_TO_WARE (Kho tổng nhập từ Supplier): 10 movements
--    Movement rows may be for HQ (destination NULL means HQ)
-- =========================================================
INSERT INTO inventory_movements
(movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id, approved_by_id, movement_status,
 created_at, deleted)
VALUES ('SUP_TO_WARE', 1, NULL, NULL, NULL, 1, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 2, NULL, NULL, NULL, 1, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 1, NULL, NULL, NULL, 2, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 3, NULL, NULL, NULL, 3, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 1, NULL, NULL, NULL, 4, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 2, NULL, NULL, NULL, 5, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 3, NULL, NULL, NULL, 6, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 1, NULL, NULL, NULL, 7, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 2, NULL, NULL, NULL, 8, 'RECEIVED', NOW(), FALSE),
       ('SUP_TO_WARE', 3, NULL, NULL, NULL, 8, 'RECEIVED', NOW(), FALSE);

-- movement_id 1..10

-- movement_details for SUP_TO_WARE: each movement 2-3 variant (batch_id = variant_id)
INSERT INTO inventory_movement_details
(movement_id, variant_id, batch_id, quantity, price, received_quantity, return_quantity, cost, created_at, deleted)
VALUES
-- movement 1
(1, 1, 1, 2000, 21000, 2000, 0, 42000000, NOW(), FALSE),
(1, 4, 4, 1000, 84000, 1000, 0, 84000000, NOW(), FALSE),
-- movement 2
(2, 2, 2, 1500, 16800, 1500, 0, 25200000, NOW(), FALSE),
(2, 3, 3, 1200, 72000, 1200, 0, 86400000, NOW(), FALSE),
-- movement 3
(3, 5, 5, 1800, 111000, 1800, 0, 199800000, NOW(), FALSE),
(3, 6, 6, 1200, 51000, 1200, 0, 61200000, NOW(), FALSE),
-- movement 4
(4, 7, 7, 1000, 104000, 1000, 0, 104000000, NOW(), FALSE),
(4, 8, 8, 800, 155000, 800, 0, 124000000, NOW(), FALSE),
-- movement 5
(5, 9, 9, 600, 60000, 600, 0, 36000000, NOW(), FALSE),
(5, 10, 10, 500, 30000, 500, 0, 15000000, NOW(), FALSE),
-- movement 6
(6, 1, 1, 1000, 21000, 1000, 0, 21000000, NOW(), FALSE),
(6, 5, 5, 700, 111000, 700, 0, 77700000, NOW(), FALSE),
-- movement 7
(7, 3, 3, 900, 72000, 900, 0, 64800000, NOW(), FALSE),
(7, 6, 6, 600, 51000, 600, 0, 30600000, NOW(), FALSE),
-- movement 8
(8, 2, 2, 800, 16800, 800, 0, 13440000, NOW(), FALSE),
(8, 7, 7, 700, 104000, 700, 0, 72800000, NOW(), FALSE),
-- movement 9
(9, 4, 4, 500, 84000, 500, 0, 42000000, NOW(), FALSE),
(9, 8, 8, 300, 155000, 300, 0, 46500000, NOW(), FALSE),
-- movement 10
(10, 9, 9, 400, 60000, 400, 0, 24000000, NOW(), FALSE),
(10, 10, 10, 350, 30000, 350, 0, 10500000, NOW(), FALSE);

-- =========================================================
-- 4) WARE_TO_BR (Kho tổng -> Chi nhánh): 30 movements
--    Some linked to request_forms (IMPORT), others as direct allocations (request_form_id = NULL)
-- =========================================================
-- First, create 8 WARE_TO_BR linked to 8 import requests (request_form_id 1..8)
INSERT INTO inventory_movements
(movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id, approved_by_id, movement_status,
 created_at, deleted)
VALUES ('WARE_TO_BR', NULL, NULL, 3, 1, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 2, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 3, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 4, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 5, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 6, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 7, 5, 'RECEIVED', NOW(), FALSE),
       ('WARE_TO_BR', NULL, NULL, 3, 8, 5, 'RECEIVED', NOW(), FALSE);

-- movement_ids continue from 11..18
-- Then add 22 more WARE_TO_BR as ad-hoc allocations (request_form_id = NULL)
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                 approved_by_id, movement_status, created_at, deleted)
VALUES
('WARE_TO_BR', NULL, NULL, 3, NULL, 1, 'RECEIVED', NOW(), FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE),
('WARE_TO_BR',NULL,1,3,NULL,1,'RECEIVED',NOW(),FALSE);

-- Now, movement_details for WARE_TO_BR (keep 1-2 variant per movement for brevity)
-- Movements 11..18 correspond to request 1..8 — use those request_details quantities as shipped
-- movement 11 (req 1): ship variant 1,6,4
INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, received_quantity,
                                        return_quantity, cost, created_at, deleted)
VALUES (11, 1, 1, 600, 21000, 600, 0, 12600000, NOW(), FALSE),
       (11, 6, 6, 300, 51000, 300, 0, 15300000, NOW(), FALSE),
       (11, 4, 4, 200, 84000, 200, 0, 16800000, NOW(), FALSE),

-- movement 12 (req2)
       (12, 1, 1, 400, 21000, 400, 0, 8400000, NOW(), FALSE),
       (12, 3, 3, 250, 72000, 250, 0, 18000000, NOW(), FALSE),
       (12, 2, 2, 200, 16800, 200, 0, 3360000, NOW(), FALSE),

-- movement 13 (req3)
       (13, 11, 11, 150, 33000, 150, 0, 4950000, NOW(), FALSE),
       (13, 12, 12, 120, 51000, 120, 0, 6120000, NOW(), FALSE),

-- movement 14 (req4)
       (14, 5, 5, 500, 111000, 500, 0, 55500000, NOW(), FALSE),
       (14, 6, 6, 200, 51000, 200, 0, 10200000, NOW(), FALSE),

-- movement 15 (req5)
       (15, 20, 20, 300, 30000, 300, 0, 9000000, NOW(), FALSE),
       (15, 22, 22, 150, 39000, 150, 0, 5850000, NOW(), FALSE),

-- movement 16 (req6)
       (16, 7, 7, 250, 104000, 250, 0, 26000000, NOW(), FALSE),
       (16, 8, 8, 180, 155000, 180, 0, 27900000, NOW(), FALSE),

-- movement 17 (req7)
       (17, 18, 18, 200, 48000, 200, 0, 9600000, NOW(), FALSE),
       (17, 19, 19, 150, 18000, 150, 0, 2700000, NOW(), FALSE),

-- movement 18 (req8)
       (18, 1, 1, 800, 21000, 800, 0, 16800000, NOW(), FALSE),
       (18, 5, 5, 400, 111000, 400, 0, 44400000, NOW(), FALSE),
       (18, 6, 6, 300, 51000, 300, 0, 15300000, NOW(), FALSE),

-- Movements 19..41: ad-hoc allocations (one variant each)
       (19, 1, 1, 200, 21000, 200, 0, 4200000, NOW(), FALSE),
       (20, 2, 2, 150, 16800, 150, 0, 2520000, NOW(), FALSE),
       (21, 3, 3, 180, 72000, 180, 0, 12960000, NOW(), FALSE),
       (22, 4, 4, 120, 84000, 120, 0, 10080000, NOW(), FALSE),
       (23, 5, 5, 140, 111000, 140, 0, 15540000, NOW(), FALSE),
       (24, 6, 6, 160, 51000, 160, 0, 8160000, NOW(), FALSE),
       (25, 7, 7, 130, 104000, 130, 0, 13520000, NOW(), FALSE),
       (26, 8, 8, 110, 155000, 110, 0, 17050000, NOW(), FALSE),
       (27, 9, 9, 90, 60000, 90, 0, 5400000, NOW(), FALSE),
       (28, 10, 10, 100, 30000, 100, 0, 3000000, NOW(), FALSE),
       (29, 1, 1, 250, 21000, 250, 0, 5250000, NOW(), FALSE),
       (30, 2, 2, 220, 16800, 220, 0, 3696000, NOW(), FALSE),
       (31, 3, 3, 200, 72000, 200, 0, 14400000, NOW(), FALSE),
       (32, 4, 4, 180, 84000, 180, 0, 15120000, NOW(), FALSE),
       (33, 5, 5, 160, 111000, 160, 0, 17760000, NOW(), FALSE),
       (34, 6, 6, 140, 51000, 140, 0, 7140000, NOW(), FALSE),
       (35, 7, 7, 120, 104000, 120, 0, 12480000, NOW(), FALSE),
       (36, 8, 8, 110, 155000, 110, 0, 17050000, NOW(), FALSE),
       (37, 9, 9, 100, 60000, 100, 0, 6000000, NOW(), FALSE),
       (38, 10, 10, 90, 30000, 90, 0, 2700000, NOW(), FALSE),
       (39, 1, 1, 210, 21000, 210, 0, 4410000, NOW(), FALSE),
       (40, 2, 2, 190, 16800, 190, 0, 3192000, NOW(), FALSE),
       (41, 3, 3, 170, 72000, 170, 0, 12240000, NOW(), FALSE);

-- =========================================================
-- 5) BR_TO_WARE (Chi nhánh -> Kho tổng): 3 movements (returns)
--    Map some to request_form_id 9..10, plus one ad-hoc
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                 approved_by_id, movement_status, created_at, deleted)
VALUES ('BR_TO_WARE', NULL, 3, NULL, 9, 5, 'RECEIVED', NOW(), FALSE),  -- movement 42
       ('BR_TO_WARE', NULL, 3, NULL, 10, 5, 'RECEIVED', NOW(), FALSE), -- movement 43
       ('BR_TO_WARE', NULL, 3, NULL, NULL, 5, 'RECEIVED', NOW(), FALSE); -- movement 44 (ad-hoc)

INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, received_quantity,
                                        return_quantity, cost, created_at, deleted)
VALUES (42, 4, 4, 60, 84000, 0, 60, 0, NOW(), FALSE),
       (43, 12, 12, 40, 51000, 0, 40, 0, NOW(), FALSE),
       (44, 6, 6, 30, 51000, 0, 30, 0, NOW(), FALSE);

-- =========================================================
-- 6) WARE_TO_SUP (Kho tổng trả Supplier): 2 movements
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                 approved_by_id, movement_status, created_at, deleted)
VALUES ('WARE_TO_SUP', 2, NULL, NULL, NULL, 1, 'SHIPPED', NOW(), FALSE),
('WARE_TO_SUP',3,NULL,NULL,NULL,1,'SHIPPED',NOW(),FALSE);


INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, received_quantity,
                                        return_quantity, cost, created_at, deleted)
VALUES (45, 9, 9, 100, 60000, 0, 100, 0, NOW(), FALSE),
       (45, 10, 10, 50, 30000, 0, 50, 0, NOW(), FALSE),
       (46, 3, 3, 120, 72000, 0, 120, 0, NOW(), FALSE);

-- =========================================================
-- 7) DISPOSAL (2 movements) — chi nhánh hủy hàng
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                 approved_by_id, movement_status, created_at, deleted)
VALUES ('DISPOSAL', NULL, 3, NULL, NULL, 5, 'COMPLETED', NOW(), FALSE),
('DISPOSAL',NULL,3,NULL,NULL,5,'COMPLETED',NOW(),FALSE);

INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, received_quantity,
                                        return_quantity, cost, created_at, deleted)
VALUES (47, 1, 1, 15, 21000, 0, 0, 0, NOW(), FALSE),
       (47, 4, 4, 5, 84000, 0, 0, 0, NOW(), FALSE),
       (48, 12, 12, 8, 51000, 0, 0, 0, NOW(), FALSE);

-- =========================================================
-- 8) INVENTORY: cập nhật tồn kho cho Kho tổng (branch_id = NULL) và chi nhánh 3
--    We'll compute coarse balances: total_received - total_sent (approx)
-- =========================================================
-- Clear any existing inventory rows for those variant/batch combos if necessary (done earlier)

-- Inventory at HQ (NULL branch) after SUP_TO_WARE and WARE_TO_SUP/WARE_TO_BR
INSERT INTO inventory (branch_id, variant_id, batch_id, quantity, min_stock, last_movement_id, created_at, deleted)
VALUES (1, 1, 1, (2000 + 1000 + 600 + 800) - (600 + 800 + 200 + 210), 0, 41, NOW(), FALSE),
       (1, 2, 2, (1500 + 800 + 220) - (200 + 150 + 190), 0, 40, NOW(), FALSE),
       (1, 3, 3, (1200 + 900 + 600) - (250 + 170 + 200), 0, 41, NOW(), FALSE),
       (1, 4, 4, (1000 + 500 + 500) - (200 + 120 + 60 + 5), 0, 41, NOW(), FALSE),
       (1, 5, 5, (1800 + 700 + 400) - (500 + 140 + 160), 0, 18, NOW(), FALSE);

-- Inventory at Branch 3 (Hà Nội)
INSERT INTO inventory (branch_id, variant_id, batch_id, quantity, min_stock, last_movement_id, created_at, deleted)
VALUES (3, 1, 1, (600 + 400 + 800 + 200 + 250 + 210) - 15, 30, 41, NOW(), FALSE),
       (3, 2, 2, (200 + 150 + 220 + 190), 20, 40, NOW(), FALSE),
       (3, 3, 3, (250 + 180 + 170), 20, 41, NOW(), FALSE),
       (3, 4, 4, (200 + 120 + 180) - (60 + 5), 20, 44, NOW(), FALSE),
       (3, 5, 5, (500 + 400 + 140 + 160), 30, 18, NOW(), FALSE),
       (3, 6, 6, (300 + 200 + 160 + 140) - 30, 20, 47, NOW(), FALSE),
       (3, 7, 7, (250 + 130 + 120), 20, 35, NOW(), FALSE),
       (3, 8, 8, (180 + 110 + 110), 20, 36, NOW(), FALSE),
       (3, 9, 9, (600 + 90 + 100), 15, 10, NOW(), FALSE),
       (3, 10, 10, (500 + 100 + 90), 15, 10, NOW(), FALSE);

-- Note: quantities above are coarse aggregates built from movement_details; adjust as needed.

-- =========================================================
-- 9) PRICES: tạo giá bán cho các variant được dùng (insert or update)
-- =========================================================
INSERT INTO prices (variant_id, sale_price, branch_price, start_date, end_date, created_at, deleted)
VALUES (1, 35000, 34000, NOW(), NULL, NOW(), FALSE),
       (2, 28000, 27000, NOW(), NULL, NOW(), FALSE),
       (3, 120000, 115000, NOW(), NULL, NOW(), FALSE),
       (4, 140000, 135000, NOW(), NULL, NOW(), FALSE),
       (5, 185000, 180000, NOW(), NULL, NOW(), FALSE),
       (6, 85000, 83000, NOW(), NULL, NOW(), FALSE),
       (7, 104000, 99000, NOW(), NULL, NOW(), FALSE),
       (8, 155000, 150000, NOW(), NULL, NOW(), FALSE),
       (9, 60000, 58000, NOW(), NULL, NOW(), FALSE),
       (10, 30000, 29000, NOW(), NULL, NOW(), FALSE);

-- =========================================================
-- 10) INVOICES: tạo ~20 hóa đơn bán lẻ cho branch 3 (HN) và nội bộ
-- =========================================================
INSERT INTO invoices (invoice_code, customer_id, shift_work_id, branch_id, total_price, payment_method, invoice_type,
                      created_at, created_by, deleted, user_id)
VALUES ('INV-HN-2001', 1, 1, 3, 70000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2002', 2, 2, 3, 120000, 'Card', 'PAID', NOW(), 6, FALSE, 7),
       ('INV-HN-2003', 3, 3, 3, 185000, 'Cash', 'PAID', NOW(), 7, FALSE, 8),
       ('INV-HN-2004', 1, 1, 3, 98000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2005', 4, 2, 3, 150000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-HN-2006', 5, 3, 3, 45000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2007', 2, 4, 3, 230000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-HN-2008', 3, 5, 3, 76000, 'Cash', 'PAID', NOW(), 6, FALSE, 8),
       ('INV-HN-2009', 4, 6, 3, 125000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2010', 5, 7, 3, 54000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-INT-3001',1, 11, 1, 185000, 'Transfer', 'DRAFT', NOW(), 1, FALSE, NULL),
       ('INV-INT-3002',2, 12, 3, 250000, 'Transfer', 'DRAFT', NOW(), 1, FALSE, NULL),
       ('INV-HN-2011', 6, 2, 3, 92000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2012', 1, 3, 3, 68000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-HN-2013', 2, 1, 3, 43000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2014', 3, 4, 3, 155000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-HN-2015', 4, 5, 3, 112000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2016', 5, 6, 3, 30000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),
       ('INV-HN-2017', 6, 7, 3, 87000, 'Card', 'PAID', NOW(), 7, FALSE, 7),
       ('INV-HN-2018', null, 8, 3, 142000, 'Cash', 'PAID', NOW(), 6, FALSE, 6);

-- invoice_id will be 1..20 etc.

-- =========================================================
-- 11) INVOICE_DETAILS: map some real sold items (use batches as above)
-- =========================================================
INSERT INTO invoice_details (invoice_id, batch_id, variant_id, quantity, price, created_at, deleted)
VALUES (1, 1, 1, 2, 35000, NOW(), FALSE),
       (1, 2, 2, 1, 28000, NOW(), FALSE),
       (2, 3, 3, 1, 120000, NOW(), FALSE),
       (3, 5, 5, 1, 185000, NOW(), FALSE),
       (4, 6, 6, 1, 85000, NOW(), FALSE),
       (5, 7, 7, 2, 104000, NOW(), FALSE),
       (6, 10, 10, 1, 30000, NOW(), FALSE),
       (7, 1, 1, 3, 35000, NOW(), FALSE),
       (8, 5, 5, 1, 185000, NOW(), FALSE),
       (9, 4, 4, 1, 140000, NOW(), FALSE),
       (10, 2, 2, 2, 28000, NOW(), FALSE),
       (11, 1, 1, 10, 35000, NOW(), FALSE),
       (12, 4, 4, 5, 140000, NOW(), FALSE),
       (13, 6, 6, 1, 85000, NOW(), FALSE),
       (14, 3, 3, 1, 120000, NOW(), FALSE),
       (15, 7, 7, 1, 104000, NOW(), FALSE),
       (16, 10, 10, 1, 30000, NOW(), FALSE),
       (17, 5, 5, 1, 185000, NOW(), FALSE),
       (18, 2, 2, 1, 28000, NOW(), FALSE),
       (19, 8, 8, 1, 155000, NOW(), FALSE),
       (20, 9, 9, 2, 60000, NOW(), FALSE);

-- =========================================================
-- 12) STOCK ADJUSTMENTS: vài dòng để test kiểm kê
-- =========================================================
INSERT INTO stock_adjustments (variant_id, brand_id, batch_id, before_quantity, after_quantity, difference_quantity,
                               reason, created_at, deleted)
VALUES (1, 1, 1, 250, 248, -2, 'Hư hỏng trong quá trình vận chuyển', NOW(), FALSE),
       (4, 2, 4, 120, 115, -5, 'Trả kiểm tra chất lượng', NOW(), FALSE),
       (6, 1, 6, 300, 295, -5, 'Hư hỏng do ẩm', NOW(), FALSE),
       (7, 1, 7, 130, 132, 2, 'Tồn thực tế hơn hệ thống', NOW(), FALSE),
       (12, 3, 12, 60, 52, -8, 'Hết hạn', NOW(), FALSE),
       (5, 1, 5, 160, 165, 5, 'Nhập nhầm', NOW(), FALSE),
       (9, 1, 9, 90, 88, -2, 'Kiểm kê', NOW(), FALSE),
       (10, 1, 10, 100, 98, -2, 'Kiểm kê', NOW(), FALSE);



INSERT INTO reports (branch_id, report_type, report_date, total_revenue, total_profit, total_sales, created_at, deleted)
VALUES
--  Chi nhánh Hà Nội
(1, 'REVENUE', '2025-10-01', 85000000.00, 0, 0, NOW(), FALSE),
(1, 'PROFIT', '2025-10-01', 0, 21000000.00, 0, NOW(), FALSE),
(1, 'SALES', '2025-10-01', 0, 0, 420, NOW(), FALSE),
(1, 'IMPORT', '2025-10-01', 55000000.00, 0, 15, NOW(), FALSE),
(1, 'STAFF', '2025-10-01', 0, 0, 12, NOW(), FALSE),

--  Chi nhánh TP.HCM
(2, 'REVENUE', '2025-10-01', 97000000.00, 0, 0, NOW(), FALSE),
(2, 'PROFIT', '2025-10-01', 0, 24500000.00, 0, NOW(), FALSE),
(2, 'SALES', '2025-10-01', 0, 0, 480, NOW(), FALSE),
(2, 'IMPORT', '2025-10-01', 62000000.00, 0, 18, NOW(), FALSE),
(2, 'STAFF', '2025-10-01', 0, 0, 15, NOW(), FALSE),

--  Kho Trung tâm (Bình Dương)
(3, 'IMPORT', '2025-10-01', 88000000.00, 0, 25, NOW(), FALSE),
(3, 'STAFF', '2025-10-01', 0, 0, 8, NOW(), FALSE),

--  Chi nhánh Đà Nẵng
(4, 'REVENUE', '2025-10-01', 56000000.00, 0, 0, NOW(), FALSE),
(4, 'PROFIT', '2025-10-01', 0, 14000000.00, 0, NOW(), FALSE),
(4, 'SALES', '2025-10-01', 0, 0, 300, NOW(), FALSE),
(4, 'STAFF', '2025-10-01', 0, 0, 10, NOW(), FALSE),

--  Tổng công ty
(5, 'REVENUE', '2025-10-01', 296000000.00, 0, 0, NOW(), FALSE),
(5, 'PROFIT', '2025-10-01', 0, 69500000.00, 0, NOW(), FALSE),
(5, 'IMPORT', '2025-10-01', 205000000.00, 0, 60, NOW(), FALSE);
