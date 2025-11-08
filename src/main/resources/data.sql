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
UPDATE branchs SET user_id = NULL WHERE id = 1;
UPDATE branchs SET user_id = NULL WHERE id = 2;
-- Chi nhánh Hà Nội
UPDATE branchs SET user_id = 4 WHERE id = 3;
-- Chi nhánh TP.HCM
UPDATE branchs SET user_id = 9 WHERE id = 4;
-- Chi nhánh Đà Nẵng
UPDATE branchs SET user_id = 14 WHERE id = 5;

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

 
INSERT INTO CATEGORYS (name, description, parent_id, deleted)
VALUES
-- LEVEL 1 (PARENT)
    ('Thuốc điều trị', 'Nhóm thuốc điều trị bệnh phổ biến', NULL, FALSE),                                 -- ID = 1
    ('Thuốc theo bệnh lý', 'Thuốc điều trị theo nhóm bệnh và cơ quan', NULL, FALSE),                      -- ID = 2
    ('Chăm sóc sức khỏe và bổ trợ', 'Vitamin, khoáng chất và sản phẩm tăng cường sức khỏe', NULL, FALSE), -- ID = 3
    ('Sản phẩm cho trẻ em', 'Sản phẩm và thuốc dành riêng cho trẻ em', NULL, FALSE),
-- LEVEL 2 – CHILD OF: Thuốc điều trị (Parent = 1)
    ('Thuốc cảm cúm', 'Điều trị cảm lạnh, nghẹt mũi, sổ mũi', 1, FALSE),
    ('Thuốc ho – long đờm', 'Giảm ho và hỗ trợ tiêu đờm', 1, FALSE),
    ('Thuốc hạ sốt – giảm đau – kháng viêm', 'Hạ sốt, giảm đau và kháng viêm', 1, FALSE),
    ('Thuốc dị ứng – kháng histamin', 'Giảm dị ứng, mề đay, ngứa, nổi mẩn', 1, FALSE),
    ('Thuốc sát khuẩn – khử trùng', 'Sát khuẩn, vệ sinh và ngừa nhiễm trùng', 1, FALSE),
-- LEVEL 2 – CHILD OF: Thuốc theo bệnh lý (Parent = 2)
    ('Dạ dày – tiêu hóa', 'Điều trị các vấn đề về dạ dày và tiêu hóa', 2, FALSE),
    ('Tim mạch – huyết áp', 'Hỗ trợ và điều trị bệnh tim mạch và huyết áp', 2, FALSE),
    ('Xương khớp – đau nhức', 'Giảm đau nhức và hỗ trợ xương khớp', 2, FALSE),
    ('Gan – giải độc', 'Hỗ trợ chức năng gan và giải độc', 2, FALSE),
    ('Thần kinh – giấc ngủ', 'Giảm căng thẳng, lo âu và hỗ trợ giấc ngủ', 2, FALSE),
-- LEVEL 2 – CHILD OF: Chăm sóc sức khỏe và bổ trợ (Parent = 3)
    ('Vitamin và khoáng chất', 'Bổ sung vitamin và khoáng chất thiết yếu', 3, FALSE),
    ('Tăng đề kháng – miễn dịch', 'Tăng cường sức đề kháng và hệ miễn dịch', 3, FALSE),
    ('Điện giải – dinh dưỡng', 'Bù nước, bù điện giải và bổ sung dinh dưỡng', 3, FALSE),
    ('Hỗ trợ tiêu hóa – men vi sinh', 'Men vi sinh và sản phẩm hỗ trợ tiêu hóa', 3, FALSE),
    ('Sức khỏe phụ nữ', 'Sản phẩm chăm sóc và tăng cường sức khỏe cho phụ nữ', 3, FALSE),
-- LEVEL 2 – CHILD OF: Sản phẩm cho trẻ em (Parent = 4)
    ('Thuốc cảm – ho – sốt cho trẻ', 'Điều trị cảm, ho và sốt cho trẻ em', 4, FALSE),
    ('Vitamin và khoáng chất trẻ em', 'Bổ sung vitamin và khoáng chất cho trẻ em', 4, FALSE),
    ('Điện giải và tiêu hóa cho trẻ', 'Bù điện giải và hỗ trợ tiêu hóa cho trẻ em', 4, FALSE),
    ('Xịt mũi – nhỏ mũi trẻ em', 'Vệ sinh và hỗ trợ thông mũi cho trẻ', 4, FALSE),
    ('Dinh dưỡng trẻ em', 'Sản phẩm dinh dưỡng và phát triển cho trẻ em', 4, FALSE);

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

-- ====== NHÓM THUỐC CẢM CÚM ======
INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, category_id, created_at, deleted)
VALUES ('Paracetamol', 'Paracetamol', 'Panadol', 'GlaxoSmithKline', 'Anh', 2, NOW(), FALSE),
       ('Decolgen', 'Paracetamol + Phenylephrine HCl + Chlorpheniramine Maleate', 'Decolgen', 'United Pharma',
        'Philippines', 2, NOW(), FALSE),
       ('Tiffy', 'Paracetamol + Chlorpheniramine Maleate', 'Tiffy', 'Medica Laboratories', 'Thái Lan', 2, NOW(), FALSE),
       ('Aspirin', 'Acetylsalicylic Acid', 'Aspirin Bayer', 'Bayer AG', 'Đức', 2, NOW(), FALSE),
       ('Coldrex MaxGrip', 'Paracetamol + Phenylephrine HCl + Vitamin C', 'Coldrex', 'GlaxoSmithKline', 'Anh', 2, NOW(),
        FALSE),
       ('Vicks Formula 44', 'Dextromethorphan HBr 15mg/5ml', 'Vicks', 'Procter & Gamble', 'Mỹ', 2, NOW(), FALSE);

-- ====== NHÓM THUỐC HO ======
INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, category_id, created_at, deleted)
VALUES ('Prospan Syrup ', 'Hedera Helix Extract (Lá thường xuân) 7mg/ml', 'Prospan', 'Engelhard Arzneimittel', 'Đức', 3,
        NOW(), FALSE),
       ('Atussin Syrup ', 'Guaifenesin 100mg/5ml + Dextromethorphan HBr 10mg/5ml + Chlorpheniramine Maleate 2mg/5ml',
        'Atussin', 'DHG Pharma', 'Việt Nam', 3, NOW(), FALSE),
       ('Bromhexine ', 'Bromhexine Hydrochloride 8mg', 'Bromhexine Stella', 'Stella Pharma', 'Việt Nam', 3, NOW(),
        FALSE),
       ('Terpin Codein', 'Codeine Phosphate 10mg + Terpin Hydrate 100mg', 'Terpin Codein', 'Imexpharm', 'Việt Nam', 3,
        NOW(), FALSE),
       ('Ho Pha Viên', 'Dextromethorphan HBr + Guaifenesin', 'Ho Pha', 'Medipharm', 'Việt Nam', 3, NOW(), FALSE);

-- ====== NHÓM THUỐC HẠ SỐT – GIẢM ĐAU ======
INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, category_id, created_at, deleted)
VALUES ('Panadol Extra ', 'Paracetamol + Caffeine', 'Panadol', 'GlaxoSmithKline', 'Anh', 4, NOW(), FALSE),
       ('Efferalgan ', 'Paracetamol', 'Efferalgan', 'Bristol-Myers', 'Pháp', 4, NOW(), FALSE),
       ('Ibuprofen ', 'Ibuprofen', 'Nurofen', 'Reckitt Benckiser', 'Anh', 4, NOW(), FALSE);

-- ====== NHÓM KHÁNG SINH phổ biến ======
INSERT INTO medicines (name, active_ingredient, brand_name, manufacturer, country, category_id, created_at, deleted)
VALUES ('Amoxicillin ', 'Amoxicillin', 'Amoxil', 'GlaxoSmithKline', 'Anh', 5, NOW(), FALSE),
       ('Azithromycin ', 'Azithromycin', 'Zithromax', 'Pfizer', 'Mỹ', 5, NOW(), FALSE),
       ('Cefixime ', 'Cefixime', 'Suprax', 'Sanofi', 'Pháp', 5, NOW(), FALSE);


 ===
-- Medicine_variant batch
 ===

-- 1. Paracetamol (OTC + 1 variant kê đơn)
INSERT INTO medicine_variant
(dosage_form, dosage, strength, package_unit_id_id, base_unit_id_id, quantity_per_package, barcode, registration_number,
 storage_conditions, indications, contraindications, side_effects, instructions, prescription_require, uses,
 medicine_id, created_at, deleted)
VALUES ('Viên nén', 'Uống 1–2 viên mỗi 4–6 giờ nếu cần', '500mg', 3, 1, 10, '8935000000011', 'VN-10001',
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
-- 15. Amoxicillin 500mg (Kê đơn)
       ('Viên nén', 'Uống 1 viên mỗi 8 giờ', '500mg', 3, 1, 10, '8935000000151', 'VN-10019',
        'Bảo quản nơi khô, dưới 30°C', 'Kháng sinh', 'Mẫn cảm với Amoxicillin', 'Buồn nôn, tiêu chảy',
        'Uống với nước đầy', TRUE, 'Sau bữa ăn', 15, NOW(), FALSE),
-- 16. Azithromycin 250mg (Kê đơn)
       ('Viên nén', 'Uống 1 viên mỗi 12 giờ', '250mg', 3, 1, 10, '8935000000161', 'VN-10020',
        'Bảo quản nơi khô, dưới 30°C', 'Kháng sinh', 'Mẫn cảm với Azithromycin', 'Buồn nôn, tiêu chảy',
        'Uống với nước đầy', TRUE, 'Sau bữa ăn', 16, NOW(), FALSE),
-- 17. Cefixime 200mg (Kê đơn)
       ('Viên nén', 'Uống 1 viên mỗi 12 giờ', '200mg', 3, 1, 10, '8935000000171', 'VN-10021',
        'Bảo quản nơi khô, dưới 30°C', 'Kháng sinh', 'Mẫn cảm với Cefixime', 'Buồn nôn, tiêu chảy', 'Uống với nước đầy',
        TRUE, 'Sau bữa ăn', 17, NOW(), FALSE);

INSERT INTO batches
(batch_code, mfg_date, expiry_date, source_movement_id, total_received, total_issued, batch_status, variant_id,
 supplier_id, created_at, deleted)
VALUES
-- ===== Paracetamol variants =====
('BATCH-PARA-500-2024-01', '2024-01-10', '2026-01-10', NULL, 5000, 1200, 'ACTIVE', 1, 1, NOW(), FALSE),
('BATCH-PARA-650-2024-02', '2024-02-05', '2026-02-05', NULL, 3000, 500, 'ACTIVE', 2, 1, NOW(), FALSE),
('BATCH-PARA-SF-2024-03', '2024-03-01', '2026-03-01', NULL, 2000, 300, 'ACTIVE', 3, 1, NOW(), FALSE),

-- ===== Decolgen variants =====
('BATCH-DECO-2024-01', '2024-03-15', '2026-03-15', NULL, 4000, 800, 'ACTIVE', 4, 2, NOW(), FALSE),
('BATCH-DECO-2024-02', '2024-04-10', '2026-04-10', NULL, 3000, 500, 'ACTIVE', 5, 2, NOW(), FALSE),

-- ===== Tiffy variants =====
('BATCH-TIFFY-500-2023-12', '2023-12-01', '2025-12-01', NULL, 3000, 2900, 'EXHAUSTED', 6, 2, NOW(), FALSE),
('BATCH-TIFFY-SF-2024-01', '2024-01-10', '2026-01-10', NULL, 1500, 200, 'ACTIVE', 7, 2, NOW(), FALSE),

-- ===== Aspirin 500mg =====
('BATCH-ASP-2023-06', '2023-06-01', '2025-06-01', NULL, 2000, 1800, 'ACTIVE', 8, 1, NOW(), FALSE),

-- ===== Coldrex MaxGrip =====
('BATCH-COLD-2024-01', '2024-01-15', '2026-01-15', NULL, 3500, 1200, 'ACTIVE', 9, 1, NOW(), FALSE),

-- ===== Vicks Formula 44 =====
('BATCH-VICKS-2023-11', '2023-11-01', '2025-11-01', NULL, 2500, 400, 'ACTIVE', 10, 3, NOW(), FALSE),

-- ===== Prospan Syrup =====
('BATCH-PRO-2023-04', '2023-04-20', '2025-04-20', NULL, 1200, 1180, 'EXHAUSTED', 11, 3, NOW(), FALSE),

-- ===== Atussin Syrup =====
('BATCH-ATUS-2022-09', '2022-09-01', '2024-09-01', NULL, 1000, 950, 'EXPIRED', 12, 3, NOW(), FALSE),

-- ===== Bromhexine 8mg =====
('BATCH-BROM-2024-02', '2024-02-10', '2026-02-10', NULL, 2500, 400, 'ACTIVE', 13, 2, NOW(), FALSE),

-- ===== Terpin Codein =====
('BATCH-TERP-2023-05', '2023-05-01', '2025-05-01', NULL, 1500, 1450, 'DISPOSED', 14, 2, NOW(), FALSE),

-- ===== Ho Pha Viên =====
('BATCH-HOPHA-2024-01', '2024-01-20', '2026-01-20', NULL, 2000, 300, 'ACTIVE', 15, 3, NOW(), FALSE),

-- ===== Panadol Extra 500mg =====
('BATCH-PANA-2024-01', '2024-01-10', '2026-01-10', NULL, 3000, 1000, 'ACTIVE', 16, 1, NOW(), FALSE),

-- ===== Efferalgan 500mg =====
('BATCH-EFFER-2024-01', '2024-01-15', '2026-01-15', NULL, 2500, 700, 'ACTIVE', 17, 1, NOW(), FALSE),

-- ===== Ibuprofen 400mg =====
('BATCH-IBU-2024-01', '2024-01-05', '2026-01-05', NULL, 2000, 400, 'ACTIVE', 18, 2, NOW(), FALSE),

-- ===== Amoxicillin 500mg =====
('BATCH-AMOX-2024-01', '2024-01-10', '2026-01-10', NULL, 1500, 200, 'ACTIVE', 19, 1, NOW(), FALSE),

-- ===== Azithromycin 250mg =====
('BATCH-AZI-2024-01', '2024-01-12', '2026-01-12', NULL, 1200, 100, 'ACTIVE', 20, 1, NOW(), FALSE),

-- ===== Cefixime 200mg =====
('BATCH-CEF-2024-01', '2024-01-15', '2026-01-15', NULL, 1000, 50, 'ACTIVE', 21, 1, NOW(), FALSE);



INSERT INTO request_forms (branch_id, request_type, request_status, note, created_at, deleted)
VALUES
-- Nhập hàng chi nhánh Hà Nội
(1, 'IMPORT', 'REQUESTED', 'Yêu cầu nhập lô Paracetamol 500mg đợt 10/2024', NOW(), FALSE),

-- Nhập hàng chi nhánh TP.HCM
(2, 'IMPORT', 'CONFIRMED', 'Đã xác nhận nhập lô Decolgen 3/2024', NOW(), FALSE),

-- Nhập hàng kho trung tâm
(3, 'IMPORT', 'RECEIVED', 'Kho trung tâm đã nhận đủ hàng đợt 2024-05', NOW(), FALSE),

-- Trả hàng chi nhánh Đà Nẵng
(4, 'RETURN', 'REQUESTED', 'Chi nhánh Đà Nẵng trả lại 50 hộp Prospan do lỗi nhãn', NOW(), FALSE),

-- Trả hàng chi nhánh Hà Nội
(1, 'RETURN', 'CONFIRMED', 'Đã xác nhận trả lại 20 lọ Atussin hết hạn', NOW(), FALSE),

-- Nhập hàng tổng công ty
(5, 'IMPORT', 'CANCELLED', 'Đơn nhập lô Aspirin bị hủy do sai đơn giá', NOW(), FALSE),

-- Nhập hàng kho miền Tây
(6, 'IMPORT', 'RECEIVED', 'Kho miền Tây đã nhập đủ lô Bromhexine 8mg', NOW(), FALSE),

-- Trả hàng kho miền Tây
(6, 'RETURN', 'CONFIRMED', 'Trả lại 10 hộp Terpin Codein do sai quy cách', NOW(), FALSE);

INSERT INTO request_details (request_form_id, variant_id, quantity, is_accepted, created_at, deleted)
VALUES
-- 1. Phiếu nhập Hà Nội - Paracetamol
(1, 1, 1000, FALSE, NOW(), FALSE),

-- 2. Phiếu nhập TP.HCM - Decolgen
(2, 2, 800, TRUE, NOW(), FALSE),

-- 3. Phiếu nhập Kho trung tâm - nhiều thuốc
(3, 1, 500, TRUE, NOW(), FALSE),
(3, 3, 600, TRUE, NOW(), FALSE),
(3, 4, 400, TRUE, NOW(), FALSE),

-- 4. Phiếu trả hàng Đà Nẵng - Prospan lỗi
(4, 5, 50, FALSE, NOW(), FALSE),

-- 5. Phiếu trả hàng Hà Nội - Atussin hết hạn
(5, 6, 20, TRUE, NOW(), FALSE),

-- 6. Phiếu nhập Tổng công ty - Aspirin (bị hủy)
(6, 4, 300, FALSE, NOW(), FALSE),

-- 7. Phiếu nhập Kho miền Tây - Bromhexine
(7, 7, 400, TRUE, NOW(), FALSE),

-- 8. Phiếu trả hàng Kho miền Tây - Terpin Codein
(8, 8, 10, TRUE, NOW(), FALSE);

INSERT INTO inventory_movements (movement_type, supplier_id, source_branch_id, destination_branch_id,
                                 request_form_id, approved_by_id, movement_status, created_at, deleted)
VALUES
-- 1. Nhập hàng từ nhà cung cấp vào chi nhánh Hà Nội
('SUP_TO_WARE', 1, NULL, 1, 1, 5, 'RECEIVED', NOW(), FALSE),

-- 2. Nhập hàng từ nhà cung cấp vào chi nhánh TP.HCM
('SUP_TO_WARE', 2, NULL, 2, 2, 5, 'CLOSED', NOW(), FALSE),

-- 3. Nhập hàng từ nhà cung cấp vào kho trung tâm
('SUP_TO_WARE', 1, NULL, 3, 3, 5, 'RECEIVED', NOW(), FALSE),

-- 4. Trả hàng từ chi nhánh Đà Nẵng về kho trung tâm
('BR_TO_WARE', NULL, 4, 3, 4, 5, 'SHIPPED', NOW(), FALSE),

-- 5. Trả hàng từ chi nhánh Hà Nội về kho trung tâm
('BR_TO_WARE', NULL, 1, 3, 5, 5, 'APPROVED', NOW(), FALSE),

-- 6. Đơn nhập bị hủy tại tổng công ty
('SUP_TO_WARE', 3, NULL, 5, 6, 5, 'CANCELLED', NOW(), FALSE),

-- 7. Xuất hàng từ kho trung tâm sang kho miền Tây
('WARE_TO_BR', NULL, 3, 6, 7, 5, 'SHIPPED', NOW(), FALSE),

-- 8. Xử lý hàng hủy tại kho miền Tây
('DISPOSAL', NULL, 6, NULL, 8, 5, 'CLOSED', NOW(), FALSE);

INSERT INTO inventory_movement_details (movement_id, variant_id, batch_id, quantity, price, received_quantity,
                                        return_quantity, cost, created_at, deleted)
VALUES
-- 1. SUPPLIER_IN → Hà Nội (Paracetamol)
(1, 1, 1, 1000, 1200.00, 1000, 0, 1200000.00, NOW(), FALSE),

-- 2. SUPPLIER_IN → TP.HCM (Decolgen)
(2, 2, 2, 800, 1500.00, 800, 0, 1200000.00, NOW(), FALSE),

-- 3. SUPPLIER_IN → Kho trung tâm (3 loại thuốc)
(3, 1, 1, 500, 1200.00, 500, 0, 600000.00, NOW(), FALSE),
(3, 3, 3, 600, 1800.00, 600, 0, 1080000.00, NOW(), FALSE),
(3, 4, 4, 400, 1000.00, 400, 0, 400000.00, NOW(), FALSE),

-- 4. BRANCH_RETURN → Đà Nẵng trả hàng về kho trung tâm (Prospan)
(4, 5, 5, 50, 25000.00, 0, 50, 1250000.00, NOW(), FALSE),

-- 5. BRANCH_RETURN → Hà Nội trả Atussin hết hạn
(5, 6, 6, 20, 22000.00, 0, 20, 440000.00, NOW(), FALSE),

-- 6. SUPPLIER_IN → Tổng công ty (đơn bị hủy - Aspirin)
(6, 4, 4, 300, 1000.00, 0, 0, 0.00, NOW(), FALSE),

-- 7. WAREHOUSE_OUT → Kho trung tâm xuất Bromhexine cho miền Tây
(7, 7, 7, 400, 1800.00, 400, 0, 720000.00, NOW(), FALSE),

-- 8. DISPOSAL → Kho miền Tây hủy Terpin Codein lỗi
(8, 8, 8, 10, 2000.00, 0, 10, 20000.00, NOW(), FALSE);

INSERT INTO inventory (branch_id, variant_id, batch_id, quantity, min_stock, last_movement_id, created_at, deleted)
VALUES
-- 1️ Hà Nội - Paracetamol (đã nhập 1000)
(1, 1, 1, 1000, 200, 1, NOW(), FALSE),

-- 2️ TP.HCM - Decolgen (đã nhập 800)
(2, 2, 2, 800, 150, 2, NOW(), FALSE),

-- 3️ Kho Trung tâm - Nhiều thuốc (Paracetamol, Tiffy, Aspirin)
(3, 1, 1, 500, 300, 3, NOW(), FALSE),
(3, 3, 3, 600, 200, 3, NOW(), FALSE),
(3, 4, 4, 400, 200, 3, NOW(), FALSE),

-- 4️ Đà Nẵng - Trả lại Prospan (đã xuất 50)
(4, 5, 5, 950, 100, 4, NOW(), FALSE),

-- 5️ Hà Nội - Trả Atussin hết hạn (đã xuất 20)
(1, 6, 6, 480, 100, 5, NOW(), FALSE),

-- 6️ Tổng công ty - Đơn nhập bị hủy (Aspirin)
(5, 4, 4, 0, 50, 6, NOW(), FALSE),

-- 7️ Kho Trung tâm - Xuất Bromhexine cho miền Tây (giảm 400)
(3, 7, 7, 2100, 500, 7, NOW(), FALSE),

-- 8️ Kho Miền Tây - Nhập Bromhexine từ trung tâm, sau đó hủy Terpin
(6, 7, 7, 400, 150, 7, NOW(), FALSE),
(6, 8, 8, 0, 50, 8, NOW(), FALSE);

INSERT INTO shifts (branch_id, start_time, end_time, name, note, created_at, deleted)
VALUES
-- Chi nhánh Hà Nội (branch_id = 1)
(1, '2025-10-27 07:00:00', '2025-10-27 11:30:00', 'Ca sáng Hà Nội', 'Ca sáng tại chi nhánh Hà Nội', NOW(), FALSE),
(1, '2025-10-27 13:00:00', '2025-10-27 17:30:00', 'Ca chiều Hà Nội', 'Ca chiều tại chi nhánh Hà Nội', NOW(), FALSE),

-- Chi nhánh TP.HCM (branch_id = 2)
(2, '2025-10-27 07:00:00', '2025-10-27 11:30:00', 'Ca sáng TP.HCM', 'Ca sáng tại chi nhánh TP.HCM', NOW(), FALSE),
(2, '2025-10-27 13:00:00', '2025-10-27 17:30:00', 'Ca chiều TP.HCM', 'Ca chiều tại chi nhánh TP.HCM', NOW(), FALSE),

-- Kho Trung tâm (branch_id = 3)
(3, '2025-10-27 06:00:00', '2025-10-27 12:00:00', 'Ca sáng Kho trung tâm', 'Ca sáng tại kho trung tâm', NOW(), FALSE),
(3, '2025-10-27 13:00:00', '2025-10-27 19:00:00', 'Ca chiều Kho trung tâm', 'Ca chiều tại kho trung tâm', NOW(), FALSE);



INSERT INTO shift_works (branch_id, shift_id, user_id, work_date, work_type, created_at, deleted)
VALUES
-- Chi nhánh Hà Nội (user_id = 1)
(1, 1, 1, '2025-10-25', 'DONE', NOW(), FALSE),
(1, 2, 1, '2025-10-26', 'IN_WORK', NOW(), FALSE),
(1, 1, 1, '2025-10-27', 'NOT_STARTED', NOW(), FALSE),

-- Chi nhánh TP.HCM (user_id = 2)
(2, 3, 2, '2025-10-25', 'IN_WORK', NOW(), FALSE),
(2, 4, 2, '2025-10-26', 'DONE', NOW(), FALSE),
(2, 3, 2, '2025-10-27', 'NOT_STARTED', NOW(), FALSE),

-- Kho Trung tâm (user_id = 3)
(3, 5, 3, '2025-10-25', 'IN_WORK', NOW(), FALSE),
(3, 6, 3, '2025-10-26', 'DONE', NOW(), FALSE),
(3, 5, 3, '2025-10-27', 'NOT_STARTED', NOW(), FALSE);



INSERT INTO invoices (invoice_code, customer_id, shift_work_id, branch_id,
                      total_price, payment_method, invoice_type, created_at, created_by, deleted)
VALUES
--  Hóa đơn tại Chi nhánh Hà Nội
('INV-20251025-001', 1, 1, 1, 350000.00, 'Cash', 'PAID', NOW(), 5, FALSE),
('INV-20251025-002', 2, 2, 1, 120000.00, 'Card', 'PAID', NOW(), 5, FALSE),

--  Hóa đơn tại Chi nhánh TP.HCM
('INV-20251025-003', 3, 4, 2, 560000.00, 'Cash', 'PAID', NOW(), 5, FALSE),
('INV-20251025-004', 4, 5, 2, 98000.00, 'Transfer', 'CANCELLED', NOW(), 5, FALSE),

--  Hóa đơn tại Kho Trung tâm (xuất nội bộ)
('INV-20251025-005', NULL, 7, 3, 1850000.00, 'Transfer', 'DRAFT', NOW(), 5, FALSE),

--  Hóa đơn tại Chi nhánh Đà Nẵng
('INV-20251025-006', 5, 9, 4, 255000.00, 'Cash', 'PAID', NOW(), 5, FALSE),

--  Hóa đơn tại Tổng công ty (xuất điều phối nội bộ)
('INV-20251025-007', NULL, 12, 5, 520000.00, 'Transfer', 'DRAFT', NOW(), 5, FALSE),

--  Hóa đơn tại Kho Miền Tây
('INV-20251025-008', 6, 14, 6, 310000.00, 'Cash', 'PAID', NOW(), 5, FALSE);

INSERT INTO invoice_details (invoice_id, batch_id, variant_id, quantity, price, created_at, deleted)
VALUES
--  INV-20251025-001: Chi nhánh Hà Nội
(1, 1, 1, 2, 35000.00, NOW(), FALSE),
(1, 2, 2, 1, 28000.00, NOW(), FALSE),

--  INV-20251025-002: Chi nhánh Hà Nội
(2, 3, 3, 1, 120000.00, NOW(), FALSE),

--  INV-20251025-003: Chi nhánh TP.HCM
(3, 4, 4, 2, 280000.00, NOW(), FALSE),

--  INV-20251025-004: Chi nhánh TP.HCM (bị hủy)
(4, 4, 4, 1, 98000.00, NOW(), FALSE),

--  INV-20251025-005: Kho trung tâm (phiếu điều chuyển nội bộ)
(5, 5, 5, 10, 185000.00, NOW(), FALSE),

--  INV-20251025-006: Chi nhánh Đà Nẵng
(6, 6, 6, 3, 85000.00, NOW(), FALSE),

--  INV-20251025-007: Tổng công ty (chuyển nội bộ)
(7, 7, 7, 5, 104000.00, NOW(), FALSE),

--  INV-20251025-008: Kho miền Tây
(8, 8, 8, 2, 155000.00, NOW(), FALSE);

INSERT INTO prices (variant_id, sale_price, branch_price, start_date, end_date, created_at, deleted)
VALUES
-- Paracetamol 500mg
(1, 35000.00, 34000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Terpin Codein 100mg
(2, 28000.00, 27000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Decolgen Forte
(3, 120000.00, 115000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Aspirin 81mg
(4, 140000.00, 135000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Vitamin C 500mg
(5, 185000.00, 180000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Panadol Extra
(6, 85000.00, 83000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Amoxicillin 500mg
(7, 104000.00, 99000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE),

-- Efferalgan 500mg
(8, 155000.00, 150000.00, '2025-10-01 00:00:00', NULL, NOW(), FALSE);



INSERT INTO stock_adjustments (variant_id, brand_id, batch_id, before_quantity, after_quantity, difference_quantity,
                               reason, created_at, deleted)
VALUES
--  Paracetamol 500mg: thiếu 2 hộp do hư hỏng
(1, 1, 1, 100, 98, -2, 'Hư hỏng bao bì trong kho Hà Nội', NOW(), FALSE),

--  Terpin Codein 100mg: dư 5 hộp do nhập nhầm
(2, 1, 2, 50, 55, 5, 'Nhập nhầm thêm 5 hộp', NOW(), FALSE),

--  Decolgen Forte: thiếu 3 hộp do hết hạn
(3, 2, 3, 80, 77, -3, 'Hết hạn sử dụng', NOW(), FALSE),

--  Aspirin 81mg: đúng tồn kho
(4, 2, 4, 200, 200, 0, 'Đối chiếu kho TP.HCM, khớp đúng', NOW(), FALSE),

--  Vitamin C 500mg: dư 10 hộp do chưa cập nhật nhập hàng
(5, 3, 5, 150, 160, 10, 'Kho trung tâm chưa cập nhật phiếu nhập', NOW(), FALSE),

--  Panadol Extra: thiếu 1 hộp do thất lạc
(6, 4, 6, 120, 119, -1, 'Thất lạc trong quá trình kiểm kê', NOW(), FALSE),

--  Amoxicillin 500mg: dư 2 hộp do ghi nhầm đơn xuất
(7, 5, 7, 90, 92, 2, 'Đã ghi trùng đơn xuất nội bộ', NOW(), FALSE),

--  Efferalgan 500mg: thiếu 4 hộp do khách trả không hợp lệ
(8, 6, 8, 60, 56, -4, 'Khách trả hàng nhưng không nhập lại kho', NOW(), FALSE);


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
