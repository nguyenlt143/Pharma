package vn.edu.fpt.pharma.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.constant.BatchStatus;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.warehouse.CreateReceiptRequest;
import vn.edu.fpt.pharma.dto.warehouse.MedicineVariantDTO;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailRequest;
import vn.edu.fpt.pharma.dto.warehouse.SupplierDTO;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.WarehouseReceiptService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseReceiptServiceImpl implements WarehouseReceiptService {

    private final InventoryMovementRepository movementRepository;
    private final InventoryMovementDetailRepository movementDetailRepository;
    private final BatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineVariantRepository variantRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public InventoryMovement createReceipt(CreateReceiptRequest request) {
        // 1. Tìm nhà cung cấp
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        // 2. Tạo InventoryMovement (Supplier -> Warehouse)
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.SUP_TO_WARE)
                .supplier(supplier)
                .sourceBranchId(null) // Supplier không có branch
                .destinationBranchId(1L) // Warehouse có branchId = 1
                .movementStatus(MovementStatus.RECEIVED) // Nhập trực tiếp, không cần phê duyệt
                .totalMoney(0.0)
                .build();

        movement = movementRepository.save(movement);

        double totalMoney = 0.0;

        // 3. Xử lý từng detail
        for (ReceiptDetailRequest detailReq : request.getDetails()) {
            // Tìm variant
            MedicineVariant variant = variantRepository.findById(detailReq.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể thuốc"));

            // Parse dates
            LocalDate mfgDate = LocalDate.parse(detailReq.getManufactureDate());
            LocalDate expiryDate = LocalDate.parse(detailReq.getExpiryDate());

            // 4. Tạo Batch mới (mỗi lần nhập tạo batch mới)
            Batch batch = Batch.builder()
                    .variant(variant)
                    .batchCode(detailReq.getBatchCode())
                    .mfgDate(mfgDate)
                    .expiryDate(expiryDate)
                    .supplier(supplier)
                    .batchStatus(BatchStatus.ACTIVE)
                    .sourceMovementId(movement.getId())
                    .totalReceived(detailReq.getQuantity().intValue())
                    .build();

            batch = batchRepository.save(batch);

            // 5. Tạo InventoryMovementDetail
            // Theo flow: price = snapCost khi nhập từ supplier
            InventoryMovementDetail detail = InventoryMovementDetail.builder()
                    .movement(movement)
                    .variant(variant)
                    .batch(batch)
                    .quantity(detailReq.getQuantity())
                    .price(detailReq.getPrice())      // Giá nhập kho
                    .snapCost(detailReq.getSnapCost() != null ? detailReq.getSnapCost() : detailReq.getPrice()) // Giá gốc từ NCC
                    .build();

            movementDetailRepository.save(detail);

            // 6. Cập nhật hoặc tạo Inventory tại warehouse (branchId = 1)
            Branch warehouse = branchRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho tổng"));

            // Tìm inventory hiện có với cùng variant, batch, branch và costPrice
            // Theo logic "Specific Identification", mỗi batch tạo ra một inventory record riêng
            Inventory inventory = inventoryRepository.findByBranchAndVariantAndBatch(warehouse, variant, batch)
                    .orElse(null);

            if (inventory != null && inventory.getCostPrice().equals(detailReq.getPrice())) {
                // Cộng dồn số lượng nếu trùng costPrice
                inventory.setQuantity(inventory.getQuantity() + detailReq.getQuantity());
            } else {
                // Tạo mới inventory record
                inventory = Inventory.builder()
                        .branch(warehouse)
                        .variant(variant)
                        .batch(batch)
                        .quantity(detailReq.getQuantity())
                        .costPrice(detailReq.getPrice())
                        .minStock(0L) // Có thể set sau
                        .build();
            }

            inventoryRepository.save(inventory);

            // Tính tổng tiền
            totalMoney += detailReq.getQuantity() * detailReq.getPrice();
        }

        // 7. Cập nhật tổng tiền cho movement
        movement.setTotalMoney(totalMoney);
        movementRepository.save(movement);

        return movement;
    }

    @Override
    public List<SupplierDTO> searchSuppliers(String query) {
        return supplierRepository.findAll().stream()
                .filter(s -> query == null || query.isEmpty() ||
                        s.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (s.getPhone() != null && s.getPhone().contains(query)))
                .map(SupplierDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineVariantDTO> searchMedicineVariants(String query) {
        return variantRepository.findAll().stream()
                .filter(v -> v.getMedicine() != null &&
                        (query == null || query.isEmpty() ||
                        v.getMedicine().getName().toLowerCase().contains(query.toLowerCase()) ||
                        (v.getBarcode() != null && v.getBarcode().contains(query))))
                .limit(20)
                .map(MedicineVariantDTO::new)
                .collect(Collectors.toList());
    }
}

