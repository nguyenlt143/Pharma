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
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.WarehouseReceiptService;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final UnitConversionRepository unitConversionRepository;

    // Helper method to get display unit from variant's unit conversions
    private String getDisplayUnitFromVariant(MedicineVariant variant) {
        if (variant == null) return "";
        List<UnitConversion> conversions = unitConversionRepository.findByVariantIdId(variant.getId());
        if (conversions.isEmpty()) return "";

        // Strategy: Use conversion with smallest multiplier (typically the base unit)
        return conversions.stream()
                .min(java.util.Comparator.comparing(UnitConversion::getMultiplier))
                .map(uc -> uc.getUnitId().getName())
                .orElse("");
    }

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
                .movementStatus(MovementStatus.CLOSED) // Nhập trực tiếp, hoàn tất ngay
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
            LocalDate currentDate = LocalDate.now();

            // DATE VALIDATION 1: NSX không được lớn hơn ngày hiện tại
            if (mfgDate.isAfter(currentDate)) {
                throw new RuntimeException("Ngày sản xuất của thuốc " + variant.getMedicine().getName()
                    + " không được lớn hơn ngày hiện tại.");
            }

            // DATE VALIDATION 2: HSD phải sau NSX
            if (!expiryDate.isAfter(mfgDate)) {
                throw new RuntimeException("Hạn sử dụng của thuốc " + variant.getMedicine().getName()
                    + " phải sau ngày sản xuất.");
            }

            // DATE VALIDATION 3: HSD tối đa 20 năm tính từ NSX
            LocalDate maxExpiryDate = mfgDate.plusYears(20);
            if (expiryDate.isAfter(maxExpiryDate)) {
                throw new RuntimeException("Hạn sử dụng của thuốc " + variant.getMedicine().getName()
                    + " không được vượt quá 20 năm kể từ ngày sản xuất.");
            }

            // VALIDATION 1: Kiểm tra số lô đã tồn tại chưa
            if (batchRepository.existsByVariantIdAndBatchCode(variant.getId(), detailReq.getBatchCode())) {
                throw new RuntimeException("Số lô " + detailReq.getBatchCode() + " đã tồn tại cho thuốc "
                    + variant.getMedicine().getName() + ". Vui lòng kiểm tra lại.");
            }



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
    public List<SupplierResponse> searchSuppliers(String query) {
        return supplierRepository.findAll().stream()
                .filter(s -> query == null || query.isEmpty() ||
                        s.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (s.getPhone() != null && s.getPhone().contains(query)))
                .map(SupplierResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineVariantDTO> searchMedicineVariants(String query) {
        try {
            List<MedicineVariant> allVariants = variantRepository.findAll();
            System.out.println("Total variants in database: " + allVariants.size());

            List<MedicineVariantDTO> results = allVariants.stream()
                    .filter(v -> {
                        // Check if medicine is null
                        if (v.getMedicine() == null) {
                            System.out.println("Variant " + v.getId() + " has null medicine");
                            return false;
                        }

                        // If no query, return all
                        if (query == null || query.isEmpty()) {
                            return true;
                        }

                        // Search by medicine name or barcode
                        String medicineName = v.getMedicine().getName();
                        String barcode = v.getBarcode();

                        boolean nameMatch = medicineName != null &&
                                medicineName.toLowerCase().contains(query.toLowerCase());
                        boolean barcodeMatch = barcode != null &&
                                barcode.contains(query);

                        return nameMatch || barcodeMatch;
                    })
                    .limit(20)
                    .map(v -> {
                        try {
                            MedicineVariantDTO dto = new MedicineVariantDTO(v);

                            // Load unit conversions
                            List<UnitConversion> conversions = unitConversionRepository
                                    .findByVariantIdId(v.getId())
                                    .stream()
                                    .sorted(java.util.Comparator.comparing(UnitConversion::getMultiplier).reversed())
                                    .collect(java.util.stream.Collectors.toList());

                            if (!conversions.isEmpty()) {
                                // Base unit = smallest multiplier
                                UnitConversion baseConversion = conversions.get(conversions.size() - 1);
                                dto.setBaseUnit(baseConversion.getUnitId().getName());

                                // Import unit = largest multiplier
                                UnitConversion importConversion = conversions.get(0);
                                dto.setImportUnit(importConversion.getUnitId().getName());

                                // Conversion ratio
                                dto.setConversionRatio(importConversion.getMultiplier() / baseConversion.getMultiplier());

                                // Generate packaging spec
                                StringBuilder spec = new StringBuilder();
                                for (int i = 0; i < conversions.size() - 1; i++) {
                                    UnitConversion current = conversions.get(i);
                                    UnitConversion next = conversions.get(i + 1);
                                    double ratio = current.getMultiplier() / next.getMultiplier();

                                    if (i > 0) spec.append(" x ");
                                    spec.append(current.getUnitId().getName())
                                        .append(" x ")
                                        .append(Math.round(ratio));
                                }
                                // Add the last (base) unit
                                if (conversions.size() > 1) {
                                    spec.append(" x ").append(baseConversion.getUnitId().getName());
                                } else {
                                    spec.append(baseConversion.getUnitId().getName());
                                }
                                dto.setPackagingSpec(spec.toString());

                                // Map unit conversions to DTOs
                                List<MedicineVariantDTO.UnitConversionInfo> ucInfoList = conversions.stream()
                                        .map(uc -> new MedicineVariantDTO.UnitConversionInfo(
                                                uc.getUnitId().getId(),
                                                uc.getUnitId().getName(),
                                                uc.getMultiplier(),
                                                uc.getIsSale()
                                        ))
                                        .collect(java.util.stream.Collectors.toList());
                                dto.setUnitConversions(ucInfoList);
                            } else {
                                dto.setBaseUnit("N/A");
                                dto.setImportUnit("N/A");
                                dto.setConversionRatio(1.0);
                                dto.setPackagingSpec("N/A");
                                dto.setUnitConversions(new ArrayList<>());
                            }

                            return dto;
                        } catch (Exception e) {
                            System.err.println("Error mapping variant " + v.getId() + ": " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(java.util.stream.Collectors.toList());

            System.out.println("Returning " + results.size() + " medicine variants");
            return results;
        } catch (Exception e) {
            System.err.println("Error in searchMedicineVariants: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

