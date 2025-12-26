SET FOREIGN_KEY_CHECKS = 0;

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
(5, '13:00:00', '17:30:00', 'Ca chiều Đà Nẵng', 'Ca chiều tại chi nhánh Đà Nẵng', NOW(), FALSE),
(3, '00:00:00', '23:59:59', 'Ca full ngày', 'Ca test chạy full 24 giờ', NOW(), FALSE);

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

INSERT INTO shift_works (assignment_id, work_date, created_at, deleted)
VALUES
-- Hà Nội
(1, DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW(), FALSE),
(2, DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW(), FALSE),
(3, DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW(), FALSE),
(1, DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW(), FALSE),
(2, DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW(), FALSE),
(3, DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW(), FALSE),
(1, NOW(),                            NOW(), FALSE),
(2, NOW(),                            NOW(), FALSE),
(3, NOW(),                            NOW(), FALSE),
-- TP.HCM
(4, NOW(),                           NOW(), FALSE),
(5, NOW(),                           NOW(), FALSE),
(6, NOW(),                           NOW(), FALSE),
-- TP. Đà Nẵng
(7, NOW(),                           NOW(), FALSE),
(8, NOW(),                           NOW(), FALSE),
(9, NOW(),                           NOW(), FALSE),
(10, CURDATE(), NOW(), FALSE),                        -- hôm nay
(10, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), FALSE);

INSERT INTO units (name, description, unit_type, packaging_level, base_unit, created_at, deleted)
VALUES ('Viên', 'Dạng đơn vị thuốc nhỏ nhất, thường dùng cho thuốc viên nén hoặc viên nang', 'COUNT', 'SMALL', TRUE, NOW(), FALSE),
       ('Vỉ', 'Gồm nhiều viên thuốc (thường 10 viên) đóng chung trong một vỉ nhôm hoặc nhựa', 'COUNT', 'MEDIUM', FALSE, NOW(), FALSE),
       ('Hộp', 'Bao gồm nhiều vỉ hoặc gói thuốc, đơn vị bán phổ biến tại quầy', 'COUNT', 'LARGE', FALSE, NOW(), FALSE),
       ('Chai', 'Đựng dung dịch, siro hoặc thuốc nhỏ giọt', 'PACKAGE', 'LARGE', TRUE, NOW(), FALSE),
       ('Ống', 'Dùng cho thuốc tiêm, thuốc uống dạng ống nhựa hoặc thủy tinh', 'PACKAGE', 'SMALL', TRUE, NOW(), FALSE),
       ('Gói', 'Thường dùng cho thuốc bột, cốm hoặc thuốc hòa tan', 'PACKAGE', 'SMALL', TRUE, NOW(), FALSE),
       ('Tuýp', 'Dùng cho thuốc bôi, kem, gel hoặc thuốc mỡ', 'PACKAGE', 'SMALL', TRUE, NOW(), FALSE),
       ('Lọ', 'Đựng viên nén, viên nang hoặc dung dịch nhỏ', 'PACKAGE', 'SMALL', TRUE, NOW(), FALSE),
       ('Thùng', 'Đơn vị bao gồm nhiều hộp hoặc chai, thường dùng trong nhập hàng', 'COUNT', 'XLARGE', FALSE, NOW(), FALSE),
       ('ml', 'Đơn vị đo thể tích cho dung dịch', 'VOLUME', NULL, TRUE, NOW(), FALSE),
       ('g', 'Đơn vị đo khối lượng', 'WEIGHT', NULL, TRUE, NOW(), FALSE);

-- Insert dosage forms with base units
INSERT INTO dosage_forms (name, base_unit_id, created_at, deleted)
VALUES ('Viên nén', 1, NOW(), FALSE),          -- base_unit: Viên (id=1)
       ('Viên nang', 1, NOW(), FALSE),         -- base_unit: Viên (id=1)
       ('Viên sủi', 1, NOW(), FALSE),          -- base_unit: Viên (id=1)
       ('Syrup', 4, NOW(), FALSE),             -- base_unit: Chai (id=4)
       ('Dung dịch bôi', 7, NOW(), FALSE),     -- base_unit: Tuýp (id=7)
       ('Dung dịch', 4, NOW(), FALSE);         -- base_unit: Chai (id=4)

INSERT INTO CATEGORYS (name, description, deleted)
VALUES
-- LEVEL 2 – CHILD OF: Thuốc điều trị (Parent = 2)
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

INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, indications, contraindications, side_effects, uses, category_id, created_at, deleted)
VALUES
    -- ====== NHÓM THUỐC CẢM CÚM 5======
    ('Paracetamol', 'Paracetamol', 'Panadol', 'GlaxoSmithKline', 'Anh',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban', 'Dùng cho đau nhẹ đến vừa và sốt', 1, NOW(), FALSE),
    ('Decolgen', 'Paracetamol + Phenylephrine HCl + Chlorpheniramine Maleate', 'Decolgen', 'United Pharma',
     'Philippines', 'Giảm đau, hạ sốt, nghẹt mũi', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn, chóng mặt', 'Dùng cho cảm cúm', 1, NOW(), FALSE),
    ('Tiffy', 'Paracetamol + Chlorpheniramine Maleate', 'Tiffy', 'Medica Laboratories', 'Thái Lan',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Hiếm gặp dị ứng', 'Dùng cho cảm cúm', 1, NOW(), FALSE),
    ('Aspirin', 'Acetylsalicylic Acid', 'Aspirin Bayer', 'Bayer AG', 'Đức',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Aspirin', 'Buồn nôn, đau bụng', 'Dùng cho đau và sốt', 1, NOW(), FALSE),
    ('Coldrex MaxGrip', 'Paracetamol + Phenylephrine HCl + Vitamin C', 'Coldrex', 'GlaxoSmithKline', 'Anh',
     'Giảm đau, hạ sốt, nghẹt mũi', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn', 'Dùng cho cảm cúm', 1, NOW(), FALSE),
    ('Vicks Formula 44', 'Dextromethorphan HBr 15mg/5ml', 'Vicks', 'Procter & Gamble', 'Mỹ',
     'Giảm ho', 'Mẫn cảm với bất kỳ thành phần nào', 'Buồn nôn, chóng mặt', 'Dùng cho ho', 1, NOW(), FALSE),

-- ====== NHÓM THUỐC HO 6======
    ('Prospan Syrup ', 'Hedera Helix Extract 7mg/ml', 'Prospan', 'Engelhard Arzneimittel', 'Đức',
     'Giảm ho, long đờm', 'Mẫn cảm với thành phần', 'Buồn nôn nhẹ', 'Dùng cho ho có đờm', 2, NOW(), FALSE),
    ('Atussin Syrup ', 'Guaifenesin 100mg/5ml + Dextromethorphan HBr 10mg/5ml + Chlorpheniramine Maleate 2mg/5ml',
     'Atussin', 'DHG Pharma', 'Việt Nam', 'Giảm ho, long đờm', 'Mẫn cảm với thành phần', 'Buồn nôn, chóng mặt', 'Dùng cho ho', 2, NOW(), FALSE),
    ('Bromhexine ', 'Bromhexine Hydrochloride 8mg', 'Bromhexine Stella', 'Stella Pharma', 'Việt Nam',
     'Giảm ho', 'Mẫn cảm với Bromhexine', 'Buồn nôn', 'Dùng cho ho', 2, NOW(), FALSE),
    ('Terpin Codein', 'Codeine Phosphate 10mg + Terpin Hydrate 100mg', 'Terpin Codein', 'Imexpharm', 'Việt Nam',
     'Giảm ho', 'Mẫn cảm với Codein hoặc Terpin', 'Buồn nôn, chóng mặt', 'Dùng cho ho', 2, NOW(), FALSE),
    ('Ho Pha Viên', 'Dextromethorphan HBr + Guaifenesin', 'Ho Pha', 'Medipharm', 'Việt Nam',
     'Giảm ho', 'Mẫn cảm với thành phần', 'Buồn nôn', 'Dùng cho ho', 2, NOW(), FALSE),

-- ====== NHÓM THUỐC HẠ SỐT – GIẢM ĐAU 7======
    ('Panadol Extra ', 'Paracetamol + Caffeine', 'Panadol', 'GlaxoSmithKline', 'Anh',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol hoặc Caffeine', 'Buồn nôn, phát ban', 'Dùng cho đau và sốt', 3, NOW(), FALSE),
    ('Efferalgan ', 'Paracetamol', 'Efferalgan', 'Bristol-Myers', 'Pháp',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Paracetamol', 'Buồn nôn, phát ban', 'Dùng cho đau và sốt', 3, NOW(), FALSE),
    ('Ibuprofen ', 'Ibuprofen', 'Nurofen', 'Reckitt Benckiser', 'Anh',
     'Giảm đau, hạ sốt', 'Mẫn cảm với Ibuprofen', 'Buồn nôn, đau dạ dày', 'Dùng cho đau và viêm', 3, NOW(), FALSE),

    -- Thuốc dị ứng – kháng histamin (ID = 8)
    ('Cetirizine', 'Cetirizine 10mg', 'Zyrtec', 'UCB Pharma', 'Belgium',
     'Giảm triệu chứng dị ứng', 'Quá mẫn với cetirizine', 'Buồn ngủ nhẹ, mệt mỏi', 'Dùng cho dị ứng', 4, NOW(), FALSE),
    ('Loratadine', 'Loratadine 10mg', 'Claritine', 'Bayer', 'Germany',
     'Giảm dị ứng, mề đay', 'Quá mẫn với loratadine', 'Khô miệng, đau đầu', 'Dùng cho dị ứng', 4, NOW(), FALSE),
    ('Fexofenadine', 'Fexofenadine Hydrochloride 180mg', 'Telfast', 'Sanofi', 'France',
     'Giảm triệu chứng dị ứng', 'Quá mẫn với fexofenadine', 'Nhức đầu, buồn nôn', 'Dùng cho dị ứng', 4, NOW(), FALSE),
    -- Thuốc sát khuẩn – khử trùng (ID = 9)
    ('Betadine', 'Povidone Iodine 10%', 'Betadine', 'Mundipharma', 'Singapore',
     'Sát khuẩn vết thương', 'Không dùng ở vùng tổn thương nặng', 'Kích ứng da hiếm gặp', 'Sát khuẩn ngoài da', 5, NOW(), FALSE),
    ('Oxy già', 'Hydrogen Peroxide 3%', 'Hydrogen Peroxide', 'Medipharma', 'Vietnam',
     'Vệ sinh vết thương', 'Không nuốt, không dùng cho vết thương sâu', 'Rát nhẹ khi bôi', 'Vệ sinh vết thương', 5, NOW(), FALSE),
    -- Dạ dày – tiêu hóa (ID = 10)
    ('Omeprazole', 'Omeprazole 20mg', 'Losec', 'AstraZeneca', 'UK',
     'Giảm ợ nóng, trào ngược', 'Quá mẫn với omeprazole', 'Đau đầu, tiêu chảy', 'Điều trị loét/GERD', 6, NOW(), FALSE),
    ('Esomeprazole', 'Esomeprazole 40mg', 'Nexium', 'AstraZeneca', 'Sweden',
     'Điều trị GERD, loét', 'Quá mẫn với esomeprazole', 'Đau đầu, tiêu chảy', 'Điều trị loét/GERD', 6, NOW(), FALSE),
    ('Domperidone', 'Domperidone 10mg', 'Motilium', 'Janssen', 'Belgium',
     'Giảm buồn nôn, nôn', 'Quá mẫn với domperidone; bệnh tim nặng', 'Mệt mỏi, khô miệng', 'Giảm buồn nôn', 6, NOW(), FALSE),
    -- Tim mạch – huyết áp (ID = 11
    ('Amlodipine', 'Amlodipine 5mg', 'Amlor', 'Pfizer', 'USA',
     'Hạ huyết áp', 'Suy tim nặng, quá mẫn', 'Chóng mặt, phù chân', 'Điều trị tăng huyết áp', 7, NOW(), FALSE),
    ('Losartan', 'Losartan Potassium 50mg', 'Cozaar', 'Merck Sharp & Dohme', 'USA',
     'Hỗ trợ điều trị huyết áp', 'Phụ nữ có thai, quá mẫn', 'Tiêu chảy, chóng mặt', 'Điều trị huyết áp', 7, NOW(), FALSE),
    ('Bisoprolol', 'Bisoprolol Fumarate 5mg', 'Concor', 'Merck', 'Germany',
     'Điều trị huyết áp, suy tim', 'Hen, block tim nặng', 'Mệt mỏi, mạch chậm', 'Tăng huyết áp, suy tim', 7, NOW(), FALSE),
    -- Xương khớp – đau nhức (ID = 12)
    ('Glucosamine', 'Glucosamine Sulfate 1500mg', 'Schiff Glucosamine', 'Schiff', 'USA',
     'Hỗ trợ sụn khớp', 'Quá mẫn với thành phần', 'Tiêu hóa nhẹ, buồn nôn', 'Hỗ trợ xương khớp', 8, NOW(), FALSE),
    ('Meloxicam', 'Meloxicam 7.5mg', 'Mobic', 'Boehringer Ingelheim', 'Germany',
     'Giảm đau viêm xương khớp', 'Loét tiêu hóa, suy thận nặng', 'Đau dạ dày, chóng mặt', 'Giảm đau viêm', 8, NOW(), FALSE),
    -- Gan – giải độc (ID = 13)
    ('Essentiale Forte', 'Phospholipid Extract', 'Essentiale', 'Sanofi', 'France',
     'Hỗ trợ chức năng gan', 'Quá mẫn với thành phần', 'Tiêu hóa nhẹ', 'Hỗ trợ chức năng gan', 9, NOW(), FALSE),
    ('LiverGold', 'Milk Thistle Extract', 'LiverGold', 'Nature''s Way', 'USA',
     'Hỗ trợ giải độc gan', 'Quá mẫn với cây kế sữa', 'Tiêu hóa nhẹ', 'Hỗ trợ chức năng gan', 9, NOW(), FALSE),
    -- Thần kinh – giấc ngủ (ID = 14)
    ('Melatonin', 'Melatonin 3mg', 'Natrol Melatonin', 'Natrol', 'USA',
     'Hỗ trợ giấc ngủ', 'Phụ nữ mang thai chưa có nghiên cứu đầy đủ', 'Buồn ngủ, chóng mặt', 'Hỗ trợ giấc ngủ', 10, NOW(), FALSE),
    ('Magnesium B6', 'Magnesium + Vitamin B6', 'MagB6', 'Sanofi', 'France',
     'Hỗ trợ giảm căng cơ', 'Quá mẫn với thành phần', 'Tiêu hóa nhẹ', 'Hỗ trợ thần kinh và giấc ngủ', 10, NOW(), FALSE),
    -- Vitamin và khoáng chất (ID = 15)
    ('Centrum', 'Multivitamins & Minerals', 'Centrum', 'Pfizer', 'USA',
     'Bổ sung vitamin', 'Quá mẫn với thành phần', 'Hiếm gặp', 'Bổ sung dinh dưỡng', 11, NOW(), FALSE),
    -- Tăng đề kháng – miễn dịch (ID = 16)
    ('Vitamin C', 'Ascorbic Acid 1000mg', 'Vitamin C', 'Blackmores', 'Australia',
     'Tăng cường miễn dịch', 'Quá mẫn với vitamin C', 'Đau dạ dày nhẹ', 'Tăng đề kháng', 12, NOW(), FALSE),
    -- Điện giải – dinh dưỡng (ID = 17)
    ('ORS', 'Oral Rehydration Salts', 'ORESOL', 'DHG Pharma', 'Vietnam',
     'Bù nước, điện giải', 'Không có', 'Hiếm gặp', 'Bù nước điện giải', 13, NOW(), FALSE),
    -- Hỗ trợ tiêu hóa – men vi sinh (ID = 18
    ('BioGaia', 'Lactobacillus reuteri Protectis', 'BioGaia', 'BioGaia AB', 'Sweden',
     'Hỗ trợ tiêu hóa', 'Quá mẫn với thành phần', 'Hiếm gặp', 'Hỗ trợ hệ tiêu hóa', 14, NOW(), FALSE),
    -- Sức khỏe phụ nữ (ID = 19)
    ('Evening Primrose Oil', 'Evening Primrose Oil 1000mg', 'EPO', 'Blackmores', 'Australia',
     'Hỗ trợ sức khỏe phụ nữ', 'Quá mẫn với thành phần', 'Tiêu hóa nhẹ', 'Hỗ trợ sức khỏe phụ nữ', 15, NOW(), FALSE),
    -- Thuốc cảm – ho – sốt cho trẻ (ID = 20
    ('Tylenol Children', 'Acetaminophen 160mg/5ml', 'Tylenol Children', 'Johnson & Johnson', 'USA',
     'Giảm đau, hạ sốt cho trẻ', 'Mẫn cảm với acetaminophen', 'Hiếm gặp', 'Dùng cho trẻ em', 16, NOW(), FALSE),
    -- Vitamin và khoáng chất trẻ em (ID = 21)
    ('Kids Smart Vita Gummies', 'Multivitamins for Kids', 'Nature', ' Way', 'Australia',
     'Bổ sung vitamin cho trẻ', 'Quá mẫn với thành phần', 'Hiếm gặp', 'Bổ sung dinh dưỡng cho trẻ', 17, NOW(), FALSE),
    -- Điện giải và tiêu hóa cho trẻ (ID = 22)
    ('Hydrite', 'ORS for Kids', 'Hydrite', 'United Pharma', 'Philippines',
     'Bù nước điện giải cho trẻ', 'Không có', 'Hiếm gặp', 'Bù nước cho trẻ', 18, NOW(), FALSE),
    -- Xịt mũi – nhỏ mũi trẻ em (ID = 23)
    ('Sterimar Baby', 'aaa', 'sea water', 'Sterimar', 'France',
     'Vệ sinh mũi cho trẻ', 'Không có', 'Hiếm gặp', 'Vệ sinh mũi', 19, NOW(), FALSE),
    -- Dinh dưỡng trẻ em (ID = 24)
    ('PediaSure', 'Child Nutrition Formula', 'PediaSure', 'Abbott', 'USA',
     'Bổ sung dinh dưỡng cho trẻ', 'Quá mẫn với thành phần', 'Hiếm gặp', 'Dinh dưỡng cho trẻ', 20, NOW(), FALSE);

INSERT INTO medicine_variant
(dosage_form_id, dosage, strength, packaging, barcode, registration_number,
 storage_conditions, instructions, prescription_require, base_unit_id,
 medicine_id, created_at, deleted)
VALUES
    -- 1. Paracetamol (OTC + 1 variant kê đơn)
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000011', 'VN-10001',
     'Bảo quản nơi khô, dưới 30°C',
     'Không quá 8 viên/ngày. Uống với nhiều nước', FALSE, 1, 1, NOW(), FALSE),
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '650mg', 'Hộp 10 vỉ x 10 viên', '8935000000012', 'VN-10002',
     'Bảo quản nơi khô, dưới 30°C',
     'Không quá 8 viên/ngày. Uống với nhiều nước', TRUE, 1, 1, NOW(), FALSE),
    (3, 'Uống 1 gói mỗi 6 giờ nếu cần', '500mg', 'Hộp 10 gói', '8935000000013', 'VN-10003',
     'Bảo quản nơi khô, dưới 30°C',
     'Hòa tan trong nước. Uống sau bữa ăn', FALSE, 1, 1, NOW(), FALSE),
-- 2. Decolgen (OTC)
    (1, 'Uống 1 viên mỗi 8 giờ', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000021', 'VN-10004',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 2, NOW(), FALSE),
    (1, 'Uống 1 viên mỗi 8 giờ', '500mg', 'Hộp 20 vỉ x 10 viên', '8935000000022', 'VN-10005',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 2, NOW(), FALSE),
-- 3. Tiffy (OTC)
    (1, 'Uống 1–2 viên mỗi 6 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000031', 'VN-10006',
     'Bảo quản nơi khô, dưới 30°C',
     'Không quá 8 viên/ngày. Uống với nhiều nước', FALSE, 1, 3, NOW(), FALSE),
    (3, 'Uống 1 gói mỗi 6 giờ nếu cần', '500mg', 'Hộp 10 gói', '8935000000032', 'VN-10007',
     'Bảo quản nơi khô, dưới 30°C',
     'Hòa tan trong nước. Sau bữa ăn', FALSE, 1, 3, NOW(), FALSE),
-- 4. Aspirin 500mg (OTC)
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000041', 'VN-10008',
     'Bảo quản nơi khô, dưới 30°C',
     'Không quá 8 viên/ngày. Uống sau bữa ăn', FALSE, 1, 4, NOW(), FALSE),
-- 5. Coldrex MaxGrip (OTC)
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000051', 'VN-10009',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 5, NOW(), FALSE),
-- 6. Vicks Formula 44 (OTC)
    (4, 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 'Chai 100ml', '8935000000061', 'VN-10010',
     'Bảo quản nơi khô, dưới 30°C',
     'Lắc đều trước khi dùng. Sau bữa ăn', FALSE, 10, 6, NOW(), FALSE),
-- 7. Prospan Syrup (OTC)
    (4, 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 'Chai 100ml', '8935000000071', 'VN-10011',
     'Bảo quản nơi khô, dưới 30°C',
     'Lắc đều trước khi dùng. Sau bữa ăn', FALSE, 10, 7, NOW(), FALSE),
-- 8. Atussin Syrup (OTC)
    (4, 'Uống 5ml mỗi 8 giờ nếu cần', '100ml', 'Chai 100ml', '8935000000081', 'VN-10012',
     'Bảo quản nơi khô, dưới 30°C',
     'Lắc đều trước khi dùng. Sau bữa ăn', FALSE, 10, 8, NOW(), FALSE),
-- 9. Bromhexine 8mg (OTC)
    (1, 'Uống 1 viên mỗi 8 giờ nếu cần', '8mg', 'Hộp 10 vỉ x 10 viên', '8935000000091', 'VN-10013',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 9, NOW(), FALSE),
-- 10. Terpin Codein (OTC)
    (1, 'Uống 1 viên mỗi 8 giờ nếu cần', '10mg + 100mg', 'Hộp 10 vỉ x 10 viên', '8935000000101', 'VN-10014',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 10, NOW(), FALSE),
-- 11. Ho Pha Viên (OTC)
    (1, 'Uống 1–2 viên mỗi 8 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000111', 'VN-10015',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 11, NOW(), FALSE),
-- 12. Panadol Extra 500mg (OTC)
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg + Caffeine', 'Hộp 10 vỉ x 10 viên', '8935000000121', 'VN-10016',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 12, NOW(), FALSE),
-- 13. Efferalgan 500mg (OTC)
    (1, 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 'Hộp 10 vỉ x 10 viên', '8935000000131', 'VN-10017',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 13, NOW(), FALSE),
-- 14. Ibuprofen 400mg (OTC)
    (1, 'Uống 1–2 viên mỗi 6–8 giờ nếu cần', '400mg', 'Hộp 10 vỉ x 10 viên', '8935000000141', 'VN-10018',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống với nước đầy. Sau bữa ăn', FALSE, 1, 14, NOW(), FALSE),
    -- Dị ứng 15: Cetirizine
    (1, 'Uống 1 viên mỗi ngày khi cần', '10mg', 'Hộp 10 vỉ x 10 viên', '8935000001501', 'VN-15001',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống nguyên viên; không lái xe nếu thấy buồn ngủ', FALSE, 1, 15, NOW(), FALSE),
    -- Dị ứng 15: Cetirizine (variant lớn hộp)
    (1, 'Uống 1 viên mỗi ngày khi cần', '10mg', 'Hộp 30 vỉ x 10 viên', '8935000001502', 'VN-15002',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống nguyên viên; không lái xe nếu thấy buồn ngủ', FALSE, 1, 15, NOW(), FALSE),
    -- Dị ứng 16: Loratadine
    (1, 'Uống 1 viên mỗi ngày', '10mg', 'Hộp 10 vỉ x 10 viên', '8935000001601', 'VN-16001',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống nguyên viên, 1 lần/ngày', FALSE, 1, 16, NOW(), FALSE),
    -- Dị ứng 17: Fexofenadine (OTC thấp liều)
    (1, 'Uống 1 viên mỗi ngày', '60mg', 'Hộp 10 vỉ x 10 viên', '8935000001701', 'VN-17001',
     'Bảo quản nơi khô, dưới 30°C',
     'Uống nguyên viên, mỗi ngày', FALSE, 1, 17, NOW(), FALSE),
    -- Dị ứng 17: Fexofenadine (Rx cao liều)
    (1, 'Uống 1 viên mỗi ngày', '180mg', 'Hộp 10 vỉ x 10 viên', 'INT0000001702', 'INT-17002',
     'Bảo quản nơi khô, dưới 30°C',
     'Dùng theo chỉ định bác sĩ', TRUE, 1, 17, NOW(), FALSE),
    -- Sát khuẩn 18: Betadine (chất sát khuẩn)
    (5, 'Rửa hoặc bôi vùng cần sát khuẩn 1–2 lần/ngày', 'Povidone Iodine 10%', 'Chai 100ml', '8935000001801',
     'VN-18001',
     'Bảo quản nơi khô, tránh ánh nắng trực tiếp',
     'Rửa vết thương, bôi một lớp mỏng', FALSE, 10, 18, NOW(), FALSE),
    -- Sát khuẩn 19: Oxy già (Hydrogen Peroxide)
    (6, 'Rửa vùng cần sát khuẩn, sau đó rửa lại bằng nước', 'Hydrogen Peroxide 3%', 'Chai 100ml',
     '8935000001901', 'VN-19001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Sử dụng ngoài da, rửa kỹ sau khi dùng', FALSE, 10, 19, NOW(), FALSE),
    -- Dạ dày 20: Omeprazole (OTC thấp liều)
    (2, 'Uống 1 viên mỗi ngày trước bữa sáng', '10mg', 'Hộp 14 vỉ x 10 viên', '8935000002001', 'VN-20001',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Uống nguyên viên trước ăn', FALSE, 1, 20, NOW(), FALSE),
    -- Dạ dày 20: Omeprazole (Rx cao liều)
    (2, 'Uống 1 viên mỗi ngày trước bữa sáng', '20mg', 'Hộp 28 vỉ x 10 viên', 'INT0000002002', 'INT-20002',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Dùng theo hướng dẫn bác sĩ', TRUE, 1, 20, NOW(), FALSE),
    -- Dạ dày 21: Esomeprazole (thường Rx)
    (2, 'Uống 1 viên mỗi ngày trước ăn', '40mg', 'Hộp 28 vỉ x 10 viên', 'INT0000002101', 'INT-21001',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Dùng theo chỉ định bác sĩ', TRUE, 1, 21, NOW(), FALSE),
    -- Dạ dày 22: Domperidone (thường Rx)
    (1, 'Uống 1 viên trước bữa ăn, 3 lần/ngày', '10mg', 'Hộp 20 vỉ x 10 viên', '8935000002201', 'VN-22001',
     'Bảo quản nơi khô, dưới 30°C',
     'Dùng theo hướng dẫn; không dùng quá liều khuyến cáo', TRUE, 1, 22, NOW(), FALSE),
    -- Tim mạch 23: Amlodipine (Rx)
    (1, 'Uống 1 viên mỗi ngày', '5mg', 'Hộp 30 vỉ x 10 viên', 'INT0000002301', 'INT-23001',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Uống vào cùng một thời điểm mỗi ngày', TRUE, 1, 23, NOW(), FALSE),
    -- Tim mạch 24: Losartan (Rx)
    (1, 'Uống 1 viên mỗi ngày', '50mg', 'Hộp 30 vỉ x 10 viên', 'INT0000002401', 'INT-24001',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Uống theo chỉ dẫn bác sĩ', TRUE, 1, 24, NOW(), FALSE),
    -- Tim mạch 25: Bisoprolol (Rx)
    (1, 'Uống 1 viên mỗi ngày', '5mg', 'Hộp 28 vỉ x 10 viên', 'INT0000002501', 'INT-25001',
     'Bảo quản nơi khô, nhiệt độ phòng',
     'Dùng theo chỉ định bác sĩ', TRUE, 1, 25, NOW(), FALSE),
    -- Xương khớp 26: Glucosamine (OTC)
    (2, 'Uống 1 viên mỗi ngày', '1500mg', 'Hộp 30 vỉ x 10 viên', '8935000002601', 'VN-26001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Uống cùng bữa ăn nếu đau dạ dày', FALSE, 1, 26, NOW(), FALSE),
    -- Xương khớp 27: Meloxicam (Rx, NSAID)
    (1, 'Uống 1 viên mỗi ngày', '7.5mg', 'Hộp 20 vỉ x 10 viên', 'INT0000002701', 'INT-27001',
     'Bảo quản nơi khô, dưới 30°C',
     'Dùng theo chỉ định; uống sau ăn để giảm kích ứng dạ dày', TRUE, 1, 27, NOW(), FALSE),
    -- Gan 28: Essentiale Forte (OTC / hỗ trợ gan)
    (2, 'Uống 1 viên, 2–3 lần/ngày', '300mg phospholipid', 'Hộp 30 vỉ x 10 viên', '8935000002801', 'VN-28001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Uống sau ăn để hỗ trợ hấp thu', FALSE, 1, 28, NOW(), FALSE),
    -- Gan 29: LiverGold (Milk Thistle, OTC)
    (2, 'Uống 1 viên mỗi ngày', 'Silymarin 140mg', 'Hộp 30 vỉ x 10 viên', '8935000002901', 'VN-29001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Uống sau ăn', FALSE, 1, 29, NOW(), FALSE),
    -- Thần kinh 30: Melatonin (OTC)
    (1, 'Uống 1 viên trước khi ngủ', '3mg', 'Hộp 30 vỉ x 10 viên', '8935000003001', 'VN-30001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Uống 30 phút trước khi ngủ; không lái xe nếu buồn ngủ', FALSE, 1, 30, NOW(), FALSE),
    -- Thần kinh 31: Magnesium B6 (OTC)
    (1, 'Uống 1 viên mỗi ngày', 'Magnesium + Vitamin B6', 'Hộp 30 vỉ x 10 viên', '8935000003101', 'VN-31001',
     'Bảo quản nơi khô, tránh ánh nắng',
     'Uống sau ăn nếu gây khó chịu dạ dày', FALSE, 1, 31, NOW(), FALSE);

-- =========================================================
-- PACKAGINGS: Define packaging units for medicine variants
-- =========================================================
INSERT INTO packagings (medicine_variant_id, package_type, quantity, unit_id, created_at, deleted)
VALUES
    -- Paracetamol 500mg (variant 1): Viên -> Vỉ -> Hộp
    (1, 'Viên', 1, 1, NOW(), FALSE),
    (1, 'Vỉ', 10, 1, NOW(), FALSE),
    (1, 'Hộp', 100, 1, NOW(), FALSE),
    -- Paracetamol 650mg (variant 2): Viên -> Vỉ -> Hộp
    (2, 'Viên', 1, 1, NOW(), FALSE),
    (2, 'Vỉ', 10, 1, NOW(), FALSE),
    (2, 'Hộp', 100, 1, NOW(), FALSE),
    -- Paracetamol Sủi (variant 3): Gói -> Hộp
    (3, 'Gói', 1, 1, NOW(), FALSE),
    (3, 'Hộp', 10, 1, NOW(), FALSE),
    -- Decolgen 500mg (variant 4): Viên -> Vỉ -> Hộp
    (4, 'Viên', 1, 1, NOW(), FALSE),
    (4, 'Vỉ', 10, 1, NOW(), FALSE),
    (4, 'Hộp', 100, 1, NOW(), FALSE),
    -- Decolgen 500mg large (variant 5): Viên -> Vỉ -> Hộp
    (5, 'Viên', 1, 1, NOW(), FALSE),
    (5, 'Vỉ', 10, 1, NOW(), FALSE),
    (5, 'Hộp', 200, 1, NOW(), FALSE),
    -- Tiffy (variant 6): Viên -> Vỉ -> Hộp
    (6, 'Viên', 1, 1, NOW(), FALSE),
    (6, 'Vỉ', 10, 1, NOW(), FALSE),
    (6, 'Hộp', 100, 1, NOW(), FALSE),
    -- Tiffy Sủi (variant 7): Gói -> Hộp
    (7, 'Gói', 1, 1, NOW(), FALSE),
    (7, 'Hộp', 10, 1, NOW(), FALSE),
    -- Aspirin (variant 8): Viên -> Vỉ -> Hộp
    (8, 'Viên', 1, 1, NOW(), FALSE),
    (8, 'Vỉ', 10, 1, NOW(), FALSE),
    (8, 'Hộp', 100, 1, NOW(), FALSE),
    -- Coldrex (variant 9): Viên -> Vỉ -> Hộp
    (9, 'Viên', 1, 1, NOW(), FALSE),
    (9, 'Vỉ', 10, 1, NOW(), FALSE),
    (9, 'Hộp', 100, 1, NOW(), FALSE),
    -- Vicks Formula 44 (variant 10): Chai (ml)
    (10, 'Chai', 100, 10, NOW(), FALSE),
    -- Prospan Syrup (variant 11): Chai (ml)
    (11, 'Chai', 100, 10, NOW(), FALSE),
    -- Atussin Syrup (variant 12): Chai (ml)
    (12, 'Chai', 100, 10, NOW(), FALSE),
    -- Bromhexine (variant 13): Viên -> Vỉ -> Hộp
    (13, 'Viên', 1, 1, NOW(), FALSE),
    (13, 'Vỉ', 10, 1, NOW(), FALSE),
    (13, 'Hộp', 100, 1, NOW(), FALSE),
    -- Terpin Codein (variant 14): Viên -> Vỉ -> Hộp
    (14, 'Viên', 1, 1, NOW(), FALSE),
    (14, 'Vỉ', 10, 1, NOW(), FALSE),
    (14, 'Hộp', 100, 1, NOW(), FALSE),
    -- Ho Pha Viên (variant 15): Viên -> Vỉ -> Hộp
    (15, 'Viên', 1, 1, NOW(), FALSE),
    (15, 'Vỉ', 10, 1, NOW(), FALSE),
    (15, 'Hộp', 100, 1, NOW(), FALSE),
    -- Panadol Extra (variant 16): Viên -> Vỉ -> Hộp
    (16, 'Viên', 1, 1, NOW(), FALSE),
    (16, 'Vỉ', 10, 1, NOW(), FALSE),
    (16, 'Hộp', 100, 1, NOW(), FALSE),
    -- Efferalgan (variant 17): Viên -> Vỉ -> Hộp
    (17, 'Viên', 1, 1, NOW(), FALSE),
    (17, 'Vỉ', 10, 1, NOW(), FALSE),
    (17, 'Hộp', 100, 1, NOW(), FALSE),
    -- Ibuprofen (variant 18): Viên -> Vỉ -> Hộp
    (18, 'Viên', 1, 1, NOW(), FALSE),
    (18, 'Vỉ', 10, 1, NOW(), FALSE),
    (18, 'Hộp', 100, 1, NOW(), FALSE),
    -- Cetirizine 10mg (variant 19): Viên -> Vỉ -> Hộp
    (19, 'Viên', 1, 1, NOW(), FALSE),
    (19, 'Vỉ', 10, 1, NOW(), FALSE),
    (19, 'Hộp', 100, 1, NOW(), FALSE),
    -- Cetirizine 10mg large (variant 20): Viên -> Vỉ -> Hộp
    (20, 'Viên', 1, 1, NOW(), FALSE),
    (20, 'Vỉ', 10, 1, NOW(), FALSE),
    (20, 'Hộp', 300, 1, NOW(), FALSE),
    -- Loratadine (variant 21): Viên -> Vỉ -> Hộp
    (21, 'Viên', 1, 1, NOW(), FALSE),
    (21, 'Vỉ', 10, 1, NOW(), FALSE),
    (21, 'Hộp', 100, 1, NOW(), FALSE),
    -- Fexofenadine 60mg (variant 22): Viên -> Vỉ -> Hộp
    (22, 'Viên', 1, 1, NOW(), FALSE),
    (22, 'Vỉ', 10, 1, NOW(), FALSE),
    (22, 'Hộp', 100, 1, NOW(), FALSE);

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
('BATCH-CEF-2024-01', '2024-01-15', '2026-01-15', NULL, 1000, 'ACTIVE', 21, 1, NOW(), FALSE),

-- ===== Fexofenadine (thêm batch) =====
('BATCH-FEXO-2024-01', '2024-05-01', '2026-05-01', NULL, 2000, 'ACTIVE', 22, 1, NOW(), FALSE);

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
       (3, 'RETURN', 'CONFIRMED', 'RETURN: trả Atussin hết hạn', NOW(), FALSE),
       -- Request forms 11-41 for additional WARE_TO_BR movements
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Paracetamol đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Vitamin C đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Amoxicillin đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Omeprazole đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Lipitor đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Aspirin đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Metformin đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Losartan đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Ibuprofen đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Cetirizine đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Paracetamol đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Vitamin C đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Amoxicillin đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Omeprazole đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Lipitor đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Aspirin đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Metformin đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Losartan đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Ibuprofen đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Cetirizine đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Paracetamol đợt 5', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Vitamin C đợt 5', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung Amoxicillin đợt 5', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 3', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 4', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 5', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 6', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 7', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 8', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 9', NOW(), FALSE),
       (3, 'IMPORT', 'CONFIRMED', 'IMPORT: bổ sung các sản phẩm khác đợt 10', NOW(), FALSE);

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
(10, 12, 40, FALSE),
-- Requests 11-41 for WARE_TO_BR movements
(11, 1, 200, FALSE),
(12, 2, 150, FALSE),
(13, 3, 180, FALSE),
(14, 4, 120, FALSE),
(15, 5, 140, FALSE),
(16, 6, 160, FALSE),
(17, 7, 130, FALSE),
(18, 8, 110, FALSE),
(19, 9, 90, FALSE),
(20, 10, 100, FALSE),
(21, 1, 250, FALSE),
(22, 2, 220, FALSE),
(23, 3, 200, FALSE),
(24, 4, 180, FALSE),
(25, 5, 160, FALSE),
(26, 6, 140, FALSE),
(27, 7, 120, FALSE),
(28, 8, 110, FALSE),
(29, 9, 100, FALSE),
(30, 10, 90, FALSE),
(31, 1, 210, FALSE),
(32, 2, 190, FALSE),
(33, 3, 170, FALSE),
(34, 11, 150, FALSE),
(35, 12, 120, FALSE),
(36, 13, 200, FALSE),
(37, 14, 150, FALSE),
(38, 15, 200, FALSE),
(39, 16, 300, FALSE),
(40, 17, 250, FALSE),
(41, 18, 200, FALSE);

-- =========================================================
-- 1) SUP_TO_WARE (Supplier -> Warehouse)
-- ==============================
INSERT INTO inventory_movements
(movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id, total_money, movement_status, created_at, deleted)
VALUES
    ('SUP_TO_WARE', 1, NULL, 1, NULL, 120000000, 'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 2, NULL, 1, NULL, 108000000, 'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 1, NULL, 1, NULL, 258000000, 'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 3, NULL, 1, NULL, 138400000, 'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 1, NULL, 1, NULL, 31800000,  'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 2, NULL, 1, NULL, 97000000,  'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 3, NULL, 1, NULL, 93000000,  'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 1, NULL, 1, NULL, 60400000,  'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 2, NULL, 1, NULL, 66400000,  'RECEIVED', NOW(), FALSE),
    ('SUP_TO_WARE', 3, NULL, 1, NULL, 21500000,  'RECEIVED', NOW(), FALSE);

-- SUP_TO_WARE: Giá từ NCC (Bậc 1) - price = snap_cost (đơn vị cơ bản)
INSERT INTO inventory_movement_details
(movement_id, variant_id, batch_id, quantity, price, snap_cost, created_at, deleted)
VALUES
-- movement 1: price = snap_cost (giá từ NCC) chia cho quantity_per_package
(1, 1, 1, 20000, 2000, 2000, NOW(), FALSE),    -- qty: 2000*10=20000, price: 20000/10=2000
(1, 4, 4, 10000, 8000, 8000, NOW(), FALSE),    -- qty: 1000*10=10000, price: 80000/10=8000
-- movement 2
(2, 2, 2, 15000, 1600, 1600, NOW(), FALSE),    -- qty: 1500*10=15000, price: 16000/10=1600
(2, 3, 3, 12000, 7000, 7000, NOW(), FALSE),    -- qty: 1200*10=12000, price: 70000/10=7000
-- movement 3
(3, 5, 5, 36000, 5500, 5500, NOW(), FALSE),    -- qty: 1800*20=36000, price: 110000/20=5500
(3, 6, 6, 12000, 5000, 5000, NOW(), FALSE),    -- qty: 1200*10=12000, price: 50000/10=5000
-- movement 4
(4, 7, 7, 1000, 68000, 68000, NOW(), FALSE),   -- qty: 1000*1=1000, price: 68000/1=68000
(4, 8, 8, 800, 88000, 88000, NOW(), FALSE),    -- qty: 800*1=800, price: 88000/1=88000
-- movement 5
(5, 9, 9, 6000, 3800, 3800, NOW(), FALSE),     -- qty: 600*10=6000, price: 38000/10=3800
(5, 10, 10, 500, 18000, 18000, NOW(), FALSE),  -- qty: 500*1=500, price: 18000/1=18000
-- movement 6
(6, 1, 1, 10000, 2000, 2000, NOW(), FALSE),    -- qty: 1000*10=10000, price: 20000/10=2000
(6, 5, 5, 14000, 5500, 5500, NOW(), FALSE),    -- qty: 700*20=14000, price: 110000/20=5500
-- movement 7
(7, 3, 3, 9000, 7000, 7000, NOW(), FALSE),     -- qty: 900*10=9000, price: 70000/10=7000
(7, 6, 6, 6000, 5000, 5000, NOW(), FALSE),     -- qty: 600*10=6000, price: 50000/10=5000
-- movement 8
(8, 2, 2, 8000, 1600, 1600, NOW(), FALSE),     -- qty: 800*10=8000, price: 16000/10=1600
(8, 7, 7, 700, 68000, 68000, NOW(), FALSE),    -- qty: 700*1=700, price: 68000/1=68000
-- movement 9
(9, 4, 4, 5000, 8000, 8000, NOW(), FALSE),     -- qty: 500*10=5000, price: 80000/10=8000
(9, 8, 8, 300, 88000, 88000, NOW(), FALSE),    -- qty: 300*1=300, price: 88000/1=88000
-- movement 10
(10, 9, 9, 4000, 3800, 3800, NOW(), FALSE),    -- qty: 400*10=4000, price: 38000/10=3800
(10, 10, 10, 350, 18000, 18000, NOW(), FALSE); -- qty: 350*1=350, price: 18000/1=18000
-- =========================================================
-- 4) WARE_TO_BR (Kho tổng -> Chi nhánh): 30 movements
--    Some linked to request_forms (IMPORT), others as direct allocations (request_form_id = NULL)
-- =========================================================
-- First, create 8 WARE_TO_BR linked to 8 import requests (request_form_id 1..8)
INSERT INTO inventory_movements
(movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id, total_money, movement_status,
 created_at, deleted)
VALUES
    ('WARE_TO_BR', NULL, 1, 3, 1, 53750000, 'RECEIVED', NOW(), FALSE),  -- movement 11
    ('WARE_TO_BR', NULL, 1, 3, 2, 35875000, 'RECEIVED', NOW(), FALSE),  -- movement 12
    ('WARE_TO_BR', NULL, 1, 3, 3, 11070000, 'RECEIVED', NOW(), FALSE),  -- movement 13
    ('WARE_TO_BR', NULL, 1, 3, 4, 81250000, 'RECEIVED', NOW(), FALSE),  -- movement 14
    ('WARE_TO_BR', NULL, 1, 3, 5, 15090000, 'RECEIVED', NOW(), FALSE),     -- movement 15 (recomputed: 8400*1100 + 3000*1950 = 15,090,000)
    ('WARE_TO_BR', NULL, 1, 3, 6, 41050000, 'RECEIVED', NOW(), FALSE),  -- movement 16
    ('WARE_TO_BR', NULL, 1, 3, 7, 12300000, 'RECEIVED', NOW(), FALSE),  -- movement 17
    ('WARE_TO_BR', NULL, 1, 3, 8, 93750000, 'RECEIVED', NOW(), FALSE),
    -- movement 18
    ('WARE_TO_BR', NULL, 1, 3, 19, 5000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 20, 3000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 21, 15750000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 22, 12000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 23, 19250000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 24, 10000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 25, 11050000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 26, 12100000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 27, 4275000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 28, 2250000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 29, 6250000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 30, 4400000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 31, 17500000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 32, 18000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 33, 22000000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 34, 8750000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 35, 10200000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 36, 12100000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 37, 4750000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 38, 2025000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 39, 5250000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 40, 3800000, 'RECEIVED', NOW(), FALSE),
    ('WARE_TO_BR', NULL, 1, 3, 41, 14875000, 'RECEIVED', NOW(), FALSE);

-- WARE_TO_BR (Bậc 2): price = snap_cost × 1.25 (warehouse lãi 25%) chia cho quantity_per_package
INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, snap_cost, created_at, deleted)
VALUES
    -- movementdetail 21-62
-- movement 11: price = snap_cost × 1.25 chia cho quantity_per_package
(11, 1, 1, 6000, 2500, 2000, NOW(), FALSE),     -- qty: 600*10=6000, price: 25000/10=2500
(11, 6, 6, 3000, 6250, 5000, NOW(), FALSE),     -- qty: 300*10=3000, price: 62500/10=6250
(11, 4, 4, 2000, 10000, 8000, NOW(), FALSE),    -- qty: 200*10=2000, price: 100000/10=10000

-- movement 12
(12, 1, 1, 4000, 2500, 2000, NOW(), FALSE),     -- qty: 400*10=4000, price: 25000/10=2500
(12, 3, 3, 2500, 8750, 7000, NOW(), FALSE),     -- qty: 250*10=2500, price: 87500/10=8750
(12, 2, 2, 2000, 2000, 1600, NOW(), FALSE),     -- qty: 200*10=2000, price: 20000/10=2000

-- movement 13 (variant 11,12 không có giai đoạn 1, dùng giá warehouse = 33k, 51k)
(13, 11, 11, 1500, 3300, 3300, NOW(), FALSE),   -- qty: 150*10=1500, price: 33000/10=3300
(13, 12, 12, 1200, 5100, 5100, NOW(), FALSE),   -- qty: 120*10=1200, price: 51000/10=5100

-- movement 14
(14, 5, 5, 10000, 6875, 5500, NOW(), FALSE),    -- qty: 500*20=10000, price: 137500/20=6875
(14, 6, 6, 2000, 6250, 5000, NOW(), FALSE),     -- qty: 200*10=2000, price: 62500/10=6250

-- movement 15 (variant 20,22 không có giai đoạn 1)
(15, 20, 20, 8400, 1100, 1100, NOW(), FALSE),  -- qty: 300*28=8400, updated to branch_price 1100
(15, 22, 22, 3000, 1950, 1950, NOW(), FALSE),        -- qty: 150*20=3000, price: 39000/20=1950

-- movement 16
(16, 7, 7, 250, 85000, 68000, NOW(), FALSE),    -- qty: 250*1=250, price: 85000/1=85000
(16, 8, 8, 180, 110000, 88000, NOW(), FALSE),   -- qty: 180*1=180, price: 110000/1=110000

-- movement 17 (variant 18,19 không có giai đoạn 1)
(17, 18, 18, 2000, 4800, 4800, NOW(), FALSE),   -- qty: 200*10=2000, price: 48000/10=4800
(17, 19, 19, 15000, 180, 180, NOW(), FALSE),    -- qty: 150*100=15000, price: 18000/100=180

-- movement 18
(18, 1, 1, 8000, 2500, 2000, NOW(), FALSE),     -- qty: 800*10=8000, price: 25000/10=2500
(18, 5, 5, 8000, 6875, 5500, NOW(), FALSE),     -- qty: 400*20=8000, price: 137500/20=6875
(18, 6, 6, 3000, 6250, 5000, NOW(), FALSE),     -- qty: 300*10=3000, price: 62500/10=6250

-- movements 19..41: price = snap_cost × 1.25 chia cho quantity_per_package
(19, 1, 1, 2000, 2500, 2000, NOW(), FALSE),     -- qty: 200*10=2000, price: 25000/10=2500
(20, 2, 2, 1500, 2000, 1600, NOW(), FALSE),     -- qty: 150*10=1500, price: 20000/10=2000
(21, 3, 3, 1800, 8750, 7000, NOW(), FALSE),     -- qty: 180*10=1800, price: 87500/10=8750
(22, 4, 4, 1200, 10000, 8000, NOW(), FALSE),    -- qty: 120*10=1200, price: 100000/10=10000
(23, 5, 5, 2800, 6875, 5500, NOW(), FALSE),     -- qty: 140*20=2800, price: 137500/20=6875
(24, 6, 6, 1600, 6250, 5000, NOW(), FALSE),     -- qty: 160*10=1600, price: 62500/10=6250
(25, 7, 7, 130, 85000, 68000, NOW(), FALSE),    -- qty: 130*1=130, price: 85000/1=85000
(26, 8, 8, 110, 110000, 88000, NOW(), FALSE),   -- qty: 110*1=110, price: 110000/1=110000
(27, 9, 9, 900, 4750, 3800, NOW(), FALSE),      -- qty: 90*10=900, price: 47500/10=4750
(28, 10, 10, 100, 22500, 18000, NOW(), FALSE),  -- qty: 100*1=100, price: 22500/1=22500
(29, 1, 1, 2500, 2500, 2000, NOW(), FALSE),     -- qty: 250*10=2500, price: 25000/10=2500
(30, 2, 2, 2200, 2000, 1600, NOW(), FALSE),     -- qty: 220*10=2200, price: 20000/10=2000
(31, 3, 3, 2000, 8750, 7000, NOW(), FALSE),     -- qty: 200*10=2000, price: 87500/10=8750
(32, 4, 4, 1800, 10000, 8000, NOW(), FALSE),    -- qty: 180*10=1800, price: 100000/10=10000
(33, 5, 5, 3200, 6875, 5500, NOW(), FALSE),     -- qty: 160*20=3200, price: 137500/20=6875
(34, 6, 6, 1400, 6250, 5000, NOW(), FALSE),     -- qty: 140*10=1400, price: 62500/10=6250
(35, 7, 7, 120, 85000, 68000, NOW(), FALSE),    -- qty: 120*1=120, price: 85000/1=85000
(36, 8, 8, 110, 110000, 88000, NOW(), FALSE),   -- qty: 110*1=110, price: 110000/1=110000
(37, 9, 9, 1000, 4750, 3800, NOW(), FALSE),     -- qty: 100*10=1000, price: 47500/10=4750
(38, 10, 10, 90, 22500, 18000, NOW(), FALSE),   -- qty: 90*1=90, price: 22500/1=22500
(39, 1, 1, 2100, 2500, 2000, NOW(), FALSE),     -- qty: 210*10=2100, price: 25000/10=2500
(40, 2, 2, 1900, 2000, 1600, NOW(), FALSE),     -- qty: 190*10=1900, price: 20000/10=2000
(41, 3, 3, 1700, 8750, 7000, NOW(), FALSE);     -- qty: 170*10=1700, price: 87500/10=8750

-- =========================================================
-- 5) BR_TO_WARE (Chi nhánh -> Kho tổng):
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                  total_money, movement_status, created_at, deleted)
VALUES
    ('BR_TO_WARE', NULL, 3, 1, 9, 6000000, 'RECEIVED', NOW(), FALSE),   -- movement 42
    ('BR_TO_WARE', NULL, 3, 1, 10, 2040000, 'RECEIVED', NOW(), FALSE),  -- movement 43
    ('BR_TO_WARE', NULL, 3, 1, NULL, 1875000, 'RECEIVED', NOW(), FALSE);-- movement 44

INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price,
                                         snap_cost, created_at, deleted)
VALUES
    (42, 4, 4, 600, 10000,  10000, NOW(), FALSE),     -- qty: 60*10=600, price: 100000/10=10000
    (43, 12, 12, 400, 5100,  5100, NOW(), FALSE),     -- qty: 40*10=400, price: 51000/10=5100
    (44, 6, 6, 300, 6250, 6250,  NOW(), FALSE);       -- qty: 30*10=300, price: 62500/10=6250

-- =========================================================
-- 6) WARE_TO_SUP (Kho tổng trả Supplier): 2 movements
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                  total_money, movement_status, created_at, deleted)
VALUES
    ('WARE_TO_SUP', 2, 1, NULL, NULL, 5875000, 'SHIPPED', NOW(), FALSE),  -- movement 45
    ('WARE_TO_SUP', 3, 1, NULL, NULL, 10500000, 'SHIPPED', NOW(), FALSE); -- movement 46


INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price,  snap_cost, created_at, deleted)
VALUES (45, 9, 9, 1000, 4750,  4750, NOW(), FALSE),    -- qty: 100*10=1000, price: 47500/10=4750
       (45, 10, 10, 50, 22500, 22500, NOW(), FALSE),   -- qty: 50*1=50, price: 22500/1=22500
       (46, 3, 3, 1200, 8750, 8750, NOW(), FALSE);     -- qty: 120*10=1200, price: 87500/10=8750

-- =========================================================
-- 7) DISPOSAL (2 movements) — chi nhánh hủy hàng
-- =========================================================
INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id, request_form_id,
                                 total_money, movement_status, created_at, deleted)
VALUES ('DISPOSAL', NULL, 1, NULL, NULL, 700000, 'RECEIVED', NOW(), FALSE),
('DISPOSAL',NULL,1,NULL,NULL,408000,'RECEIVED',NOW(),FALSE);
INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price,  snap_cost, created_at, deleted)
VALUES (47, 1, 1, 150, 2000,  2000, NOW(), FALSE),     -- qty: 15*10=150, price: 20000/10=2000
       (47, 4, 4, 50, 8000, 8000, NOW(), FALSE),       -- qty: 5*10=50, price: 80000/10=8000
       (48, 12, 12, 80, 5100,  5100, NOW(), FALSE);    -- qty: 8*10=80, price: 51000/10=5100

-- =========================================================
-- 8) INVENTORY: cập nhật tồn kho cho Kho tổng (branch_id = NULL) và chi nhánh 3
--    We'll compute coarse balances: total_received - total_sent (approx)

-- Inventory at HQ (warehouse, branch_id=1) - cost_price = giá từ SUP_TO_WARE (giá NCC) chia cho quantity_per_package
INSERT INTO inventory (id, created_at, deleted, branch_id, variant_id, batch_id, quantity, base_unit_id, cost_price, min_stock)
VALUES (1, NOW(),  FALSE, 1, 1, 1, ((20000 + 10000) - (6000 + 4000 + 8000 + 2000 + 2500 + 2100 + 150)), 1, 2000, 0),     -- qty*10, price/10
       (2, NOW(),  FALSE, 1, 2, 2, ((15000 + 8000) - (2000 + 1500 + 2200 + 1900)), 1, 1600, 0),                           -- qty*10, price/10
       (3, NOW(),  FALSE, 1, 3, 3, ((12000 + 9000) - (2500 + 1800 + 1700 + 2000 + 1200)), 1, 7000, 0),                    -- qty*10, price/10
       (4, NOW(),  FALSE, 1, 4, 4, ((10000 + 5000) - (2000 + 1200 + 1800 + 600 + 50)), 1, 8000, 0),                       -- qty*10, price/10
       (5, NOW(),  FALSE, 1, 5, 5, ((36000 + 14000) - (10000 + 8000 + 2800 + 3200)), 1, 5500, 0),                         -- qty*20, price/20
       (6, NOW(),  FALSE, 1, 6, 6, ((12000 + 6000) - (3000 + 2000 + 1600 + 1400 + 300)), 1, 5000, 0),                     -- qty*10, price/10
       (7, NOW(), FALSE, 1, 7, 7, ((1000 + 700) - (250 + 130 + 120)), 10, 68000, 0),                                       -- qty*1, price/1
       (8, NOW(), FALSE, 1, 8, 8, ((800 + 300) - (180 + 110 + 110)), 10, 88000, 0),                                        -- qty*1, price/1
       (9, NOW(), FALSE, 1, 9, 9, ((6000 + 4000) - (900 + 1000 + 1000)), 1, 3800, 0),                                     -- qty*10, price/10
       (10, NOW(), FALSE, 1, 10, 10, ((500 + 350) - (100 + 90 + 50)), 10, 18000, 0),                                       -- qty*1, price/1
       (11, NOW(),  FALSE, 1, 11, 11, 10500, 10, 3300, 0),                                                                 -- qty*10, price/10
       (12, NOW(),  FALSE, 1, 12, 12, ((1200 + 400) - (400 + 80)), 10, 5100, 0),                                           -- qty*10, price/10
       (13, NOW(),  FALSE, 1, 13, 13, 23000, 1, 5800, 0),                                                                 -- qty*10, price/10
       (14, NOW(),  FALSE, 1, 14, 14, 15000, 1, 4800, 0),                                                                 -- qty*10, price/10
       (15, NOW(), FALSE, 1, 15, 15, 20000, 1, 3800, 0),                                                                  -- qty*10, price/10
       (16, NOW(),  FALSE, 1, 16, 16, 30000, 1, 4300, 0),                                                                 -- qty*10, price/10
       (17, NOW(),  FALSE, 1, 17, 17, 25000, 1, 4000, 0),                                                                 -- qty*10, price/10
       (18, NOW(),  FALSE, 1, 18, 18, 18000, 1, 4800, 0),                                                                 -- qty*10, price/10
       (19, NOW(), FALSE, 1, 19, 19, 135000, 10, 180, 0),                                                                  -- qty*100, price/100
       (20, NOW(), FALSE, 1, 20, 20, 25200, 1, 1100, 0),                                                               -- qty*28, cost_price updated to 1100 for variant 20
       (21, NOW(),  FALSE, 1, 21, 21, 10000, 1, 4300, 0),                                                                 -- qty*10, price/10
       (22, NOW(), FALSE, 1, 22, 22, 27000, 1, 1950, 0);                                                                  -- qty*20, price/20

-- Inventory at Branch 3 (Hà Nội) - cost_price = WARE_TO_BR price (warehouse_price × 1.25) chia cho quantity_per_package
INSERT INTO inventory (id, created_at,  deleted, branch_id, variant_id, batch_id, quantity, base_unit_id, cost_price, min_stock)
VALUES (23, NOW() , FALSE, 3, 1, 1, (6000 + 4000 + 8000 + 2000 + 2500 + 2100), 1, 2500, 300),    -- qty*10, price/10, min*10
       (24, NOW(),  FALSE, 3, 2, 2, (2000 + 1500 + 2200 + 1900), 1, 2000, 200),                   -- qty*10, price/10, min*10
       (25, NOW(),   FALSE, 3, 3, 3, (2500 + 1800 + 2000), 1, 8750, 200),                         -- qty*10, price/10, min*10
       (26, NOW(),   FALSE, 3, 4, 4, ((2000 + 1200 + 1800) - 600), 1, 10000, 200),                -- qty*10, price/10, min*10
       (27, NOW(),  FALSE, 3, 5, 5, (10000 + 8000 + 2800 + 3200), 1, 6875, 600),                  -- qty*20, price/20, min*20
       (28, NOW(),   FALSE, 3, 6, 6, ((3000 + 2000 + 1600 + 1400) - 300), 1, 6250, 200),          -- qty*10, price/10, min*10
       (29, NOW(),  FALSE, 3, 7, 7, (250 + 130 + 120), 10, 85000, 20),                             -- qty*1, price/1, min*1
       (30, NOW(),  FALSE, 3, 8, 8, (180 + 110 + 110), 10, 110000, 20),                            -- qty*1, price/1, min*1
       (31, NOW(),   FALSE, 3, 9, 9, (900 + 1000), 1, 4750, 150),                                 -- qty*10, price/10, min*10
       (32, NOW(),   FALSE, 3, 10, 10, (100 + 90), 10, 22500, 15),                                 -- qty*1, price/1, min*1
       (33, NOW(),   FALSE, 3, 11, 11, 1500, 10, 3300, 100),                                       -- qty*10, price/10, min*10
       (34, NOW(),   FALSE, 3, 12, 12, ((1200 - 400)), 10, 5100, 100),                             -- qty*10, price/10, min*10
       (35, NOW(),   FALSE, 3, 18, 18, 2000, 1, 4800, 100),                                       -- qty*10, price/10, min*10
       (36, NOW(),  FALSE, 3, 19, 19, 15000, 10, 180, 1000),                                       -- qty*100, price/100, min*100
       (37, NOW(),  FALSE, 3, 20, 20, 8400, 1, 1100, 420),                                     -- qty*28, cost_price updated to 1100 for variant 20 at branch 3
       (38, NOW(),   FALSE, 3, 22, 22, 3000, 1, 1950, 200);                                       -- qty*20, price/20, min*20

-- Note: quantities above are coarse aggregates built from movement_details; adjust as needed.

-- =========================================================
-- 9) PRICES: tạo giá bán cho các variant được dùng (insert or update)
-- =========================================================
-- Prices (Bậc 3): branch_price và sale_price chia cho quantity_per_package để có giá đơn vị cơ bản
INSERT INTO prices (variant_id, branch_id, sale_price, branch_price, start_date, end_date, created_at, deleted)
    VALUES (1, NULL, 3500, 2500, NOW(), NULL, NOW(), FALSE),       -- 35000/10=3500, 25000/10=2500
       (2, NULL, 2500, 2000, NOW(), NULL, NOW(), FALSE),       -- 25000/10=2500, 20000/10=2000
       (3, NULL, 11000, 8750, NOW(), NULL, NOW(), FALSE),      -- 110000/10=11000, 87500/10=8750
       (4, NULL, 12500, 10000, NOW(), NULL, NOW(), FALSE),     -- 125000/10=12500, 100000/10=10000
       (5, NULL, 8750, 6875, NOW(), NULL, NOW(), FALSE),       -- 175000/20=8750, 137500/20=6875
       (6, NULL, 8000, 6250, NOW(), NULL, NOW(), FALSE),       -- 80000/10=8000, 62500/10=6250
       (7, NULL, 110000, 85000, NOW(), NULL, NOW(), FALSE),    -- 110000/1=110000, 85000/1=85000
       (8, NULL, 140000, 110000, NOW(), NULL, NOW(), FALSE),   -- 140000/1=140000, 110000/1=110000
       (9, NULL, 6000, 4750, NOW(), NULL, NOW(), FALSE),       -- 60000/10=6000, 47500/10=4750
       (10, NULL, 30000, 22500, NOW(), NULL, NOW(), FALSE),    -- 30000/1=30000, 22500/1=22500
       (11, NULL, 4500, 3300, NOW(), NULL, NOW(), FALSE),      -- 45000/10=4500, 33000/10=3300
       (12, NULL, 6500, 5100, NOW(), NULL, NOW(), FALSE),      -- 65000/10=6500, 51000/10=5100
       (13, NULL, 7500, 5800, NOW(), NULL, NOW(), FALSE),      -- 75000/10=7500, 58000/10=5800
       (14, NULL, 6000, 4800, NOW(), NULL, NOW(), FALSE),      -- 60000/10=6000, 48000/10=4800
       (15, NULL, 5000, 3800, NOW(), NULL, NOW(), FALSE),      -- 50000/10=5000, 38000/10=3800
       (16, NULL, 5500, 4300, NOW(), NULL, NOW(), FALSE),      -- 55000/10=5500, 43000/10=4300
       (17, NULL, 5000, 4000, NOW(), NULL, NOW(), FALSE),      -- 50000/10=5000, 40000/10=4000
       (18, NULL, 6000, 4800, NOW(), NULL, NOW(), FALSE),      -- 60000/10=6000, 48000/10=4800
       (19, NULL, 250, 180, NOW(), NULL, NOW(), FALSE),        -- 25000/100=250, 18000/100=180
       (20, NULL, 1500, 1100, NOW(), NULL, NOW(), FALSE),-- sale_price=1500, branch_price=1100 for variant 20 (rounded)
       (21, NULL, 5500, 4300, NOW(), NULL, NOW(), FALSE),      -- 55000/10=5500, 43000/10=4300
       (22, NULL, 2500, 1950, NOW(), NULL, NOW(), FALSE);      -- 50000/20=2500, 39000/20=1950
-- Done
-- total_price = sum of invoice_details with new rounded prices
INSERT INTO invoices (invoice_code, customer_id, shift_work_id, branch_id, total_price,
                      payment_method, invoice_type, created_at, created_by, deleted, user_id)
VALUES
-- user_id 6 → shift_work_id {1,4,7}
('INV-HN-2001', 1, 1, 3, 95000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 6, FALSE, 6),  -- inv1: (30000*2 + 25000*1)
('INV-HN-2004', 1, 4, 3, 80000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 6, FALSE, 6),  -- inv2: 65000*1
('INV-HN-2006', 5, 7, 3, 30000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),                             -- inv3: 25000*1
('INV-INT-3001', 1, 1, 1, 350000, 'Transfer', 'DRAFT', DATE_SUB(NOW(), INTERVAL 2 DAY), 6, FALSE, 6),  -- inv4: 30000*10
('INV-HN-2011', 6, 4, 3, 80000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 6, FALSE, 6),  -- inv5: 65000*1
('INV-HN-2012', 1, 7, 3, 110000, 'Transfer', 'PAID', NOW(), 6, FALSE, 6),                             -- inv6: 90000*1
('INV-HN-2013', 2, 1, 3, 110000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 6, FALSE, 6),  -- inv7: 90000*1
('INV-HN-2016', 5, 4, 3, 25000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 6, FALSE, 6),  -- inv8: 25000*1
('INV-HN-2018', NULL, 7, 3, 120000, 'Cash', 'PAID', NOW(), 6, FALSE, 6),                          -- inv6 dup: 90000*1

-- user_id 7 → shift_work_id {2,5,8}
('INV-HN-2002', 2, 2, 3, 110000, 'Transfer', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 7, FALSE, 7),  -- inv10: 90000*1
('INV-HN-2005', 4, 5, 3, 220000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 7, FALSE, 7), -- inv11: 90000*2
('INV-HN-2007', 2, 8, 3, 105000, 'Transfer', 'PAID', NOW(), 7, FALSE, 7),                             -- inv12: 30000*3
('INV-HN-2010', 5, 2, 3, 50000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 7, FALSE, 7),  -- inv13: 25000*2
('INV-INT-3002', 2, 5, 3, 625000, 'Transfer', 'DRAFT', DATE_SUB(NOW(), INTERVAL 1 DAY), 7, FALSE, 7),  -- inv14: 105000*5
('INV-HN-2014', 3, 8, 3, 30000, 'Cash', 'PAID', NOW(), 7, FALSE, 7),                             -- inv15: 25000*1
('INV-HN-2015', 4, 2, 3, 175000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 7, FALSE, 7), -- inv16: 140000*1
('INV-HN-2017', 6, 5, 3, 140000, 'Transfer', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 7, FALSE, 7), -- inv17: 115000*1

-- user_id 8 → shift_work_id {3,6,9}
('INV-HN-2003', 3, 3, 3, 175000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), 8, FALSE, 8), -- inv18: 140000*1
('INV-HN-2008', 3, 6, 3, 175000, 'Cash', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), 8, FALSE, 8), -- inv19: 140000*1
('INV-HN-2009', 4, 9, 3, 125000, 'Cash', 'PAID', NOW(), 8, FALSE, 8);                            -- inv20: 105000*1
-- Done: price = sale_price (đơn vị cơ bản), quantity = số lượng đơn vị cơ bản
INSERT INTO invoice_details (invoice_id, inventory_id, quantity, price, multiplier, created_at, deleted)
VALUES
    (1, 23,  20, 3500, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),     -- inv_id=23 (var 1, qty/pkg=10): 2 hộp → 20 viên, 35000/10=3500đ/viên
    (1, 24,  10, 2500, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),     -- inv_id=24 (var 2, qty/pkg=10): 1 hộp → 10 viên, 25000/10=2500đ/viên
    (2, 28,  10, 8000, 1.0,  DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),     -- inv_id=28 (var 6, qty/pkg=10): 1 hộp → 10 viên, 80000/10=8000đ/viên
    (3, 32,  1, 30000, 1.0,  NOW(), FALSE),                               -- inv_id=32 (var 10, qty/pkg=1): 1 chai → 1 chai, 30000/1=30000đ/chai
    (4, 23,  100, 3500, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),    -- inv_id=23 (var 1, qty/pkg=10): 10 hộp → 100 viên, 35000/10=3500đ/viên
    (5, 28,  10, 8000, 1.0,  DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),     -- inv_id=28 (var 6, qty/pkg=10): 1 hộp → 10 viên, 80000/10=8000đ/viên
    (6, 25,  10, 11000, 1.0,  NOW(), FALSE),                              -- inv_id=25 (var 3, qty/pkg=10): 1 hộp → 10 viên, 110000/10=11000đ/viên
    (7, 29,  1, 110000, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),    -- inv_id=29 (var 7, qty/pkg=1): 1 chai → 1 chai, 110000/1=110000đ/chai
    (8, 24,  10, 2500, 1.0, DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),      -- inv_id=24 (var 2, qty/pkg=10): 1 hộp → 10 viên, 25000/10=2500đ/viên
    (9, 31,  20, 6000, 1.0,  NOW(), FALSE),                               -- inv_id=31 (var 9, qty/pkg=10): 2 hộp → 20 viên, 60000/10=6000đ/viên
    (10, 25,  10, 11000, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),   -- inv_id=25 (var 3, qty/pkg=10): 1 hộp → 10 viên, 110000/10=11000đ/viên
    (11, 29,  2, 110000, 1.0,  DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),   -- inv_id=29 (var 7, qty/pkg=1): 2 chai → 2 chai, 110000/1=110000đ/chai
    (12, 23,  30, 3500, 1.0,  NOW(), FALSE),                               -- inv_id=23 (var 1, qty/pkg=10): 3 hộp → 30 viên, 35000/10=3500đ/viên
    (13, 24,  20, 2500, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),    -- inv_id=24 (var 2, qty/pkg=10): 2 hộp → 20 viên, 25000/10=2500đ/viên
    (14, 26,  50, 12500, 1.0,  DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),   -- inv_id=26 (var 4, qty/pkg=10): 5 hộp → 50 viên, 125000/10=12500đ/viên
    (15, 32,  1, 30000, 1.0,  NOW(), FALSE),                              -- inv_id=32 (var 10, qty/pkg=1): 1 chai → 1 chai, 30000/1=30000đ/chai
    (16, 27,  20, 8750, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),    -- inv_id=27 (var 5, qty/pkg=20): 1 hộp → 20 viên, 175000/20=8750đ/viên
    (17, 30,  1, 140000, 1.0,  DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),   -- inv_id=30 (var 8, qty/pkg=1): 1 chai → 1 chai, 140000/1=140000đ/chai
    (18, 27,  20, 8750, 1.0,  DATE_SUB(NOW(), INTERVAL 2 DAY), FALSE),    -- inv_id=27 (var 5, qty/pkg=20): 1 hộp → 20 viên, 175000/20=8750đ/viên
    (19, 27,  20, 8750, 1.0, DATE_SUB(NOW(), INTERVAL 1 DAY), FALSE),     -- inv_id=27 (var 5, qty/pkg=20): 1 hộp → 20 viên, 175000/20=8750đ/viên
    (20, 26,  10, 12500, 1.0,  NOW(), FALSE);                             -- inv_id=26 (var 4, qty/pkg=10): 1 hộp → 10 viên, 125000/10=12500đ/viên


-- =========================================================
-- 12) STOCK ADJUSTMENTS: vài dòng để test kiểm kê
-- =========================================================
-- 12) STOCK ADJUSTMENTS: vài dòng để test kiểm kê
-- =========================================================
INSERT INTO stock_adjustments (variant_id, batch_id, before_quantity, after_quantity, difference_quantity,
                               reason, created_at, deleted)
VALUES (1, 1, 2500, 2480, -20, 'Hư hỏng trong quá trình vận chuyển', NOW(), FALSE),    -- qty*10: 250*10, 248*10, -2*10
       (4, 4, 1200, 1150, -50, 'Trả kiểm tra chất lượng', NOW(), FALSE),               -- qty*10: 120*10, 115*10, -5*10
       (6, 6, 3000, 2950, -50, 'Hư hỏng do ẩm', NOW(), FALSE),                         -- qty*10: 300*10, 295*10, -5*10
       (7, 7, 130, 132, 2, 'Tồn thực tế hơn hệ thống', NOW(), FALSE),                  -- qty*1: 130*1, 132*1, 2*1
       (12, 12, 600, 520, -80, 'Hết hạn', NOW(), FALSE),                               -- qty*10: 60*10, 52*10, -8*10
       (5, 5, 3200, 3300, 100, 'Nhập nhầm', NOW(), FALSE),                             -- qty*20: 160*20, 165*20, 5*20
       (9, 9, 900, 880, -20, 'Kiểm kê', NOW(), FALSE),                                 -- qty*10: 90*10, 88*10, -2*10
       (10, 10, 100, 98, -2, 'Kiểm kê', NOW(), FALSE);                                 -- qty*1: 100*1, 98*1, -2*1



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
SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO unit_conversions (variant_id, unit_id, base_unit_quantity, deleted)
VALUES
       (1, 1, 1, FALSE),
       (1, 2, 10, FALSE),
       (1, 3, 100, FALSE),
       (2, 1, 1, FALSE),
       (2, 2, 10, FALSE),
       (2, 3, 100, FALSE);
