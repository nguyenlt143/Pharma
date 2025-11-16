//package vn.edu.fpt.pharma.service.impl;
//
//import org.springframework.stereotype.Service;
//import vn.edu.fpt.pharma.base.BaseServiceImpl;
//import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
//import vn.edu.fpt.pharma.entity.InventoryMovement;
//import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
//import vn.edu.fpt.pharma.service.AuditService;
//import vn.edu.fpt.pharma.service.InventoryMovementService;
//
//import java.util.List;
//
//
//@Service
//public class InventoryMovementServiceImpl extends BaseServiceImpl<InventoryMovement, Long, InventoryMovementRepository>
//        implements InventoryMovementService {
//
//    private final InventoryMovementRepository movementRepository;
//
//    public InventoryMovementServiceImpl(
//            InventoryMovementRepository repository,
//            AuditService auditService,
//            InventoryMovementRepository movementRepository
//    ) {
//        super(repository, auditService);
//        this.movementRepository = movementRepository;
//    }
//
//    @Override
//    public List<InventoryMovementVM> getAllMovements() {
////        return movementRepository.findAll().stream()
////                .map(InventoryMovementVM::new)
////                .toList();
//    }
//
//    @Override
//    public InventoryMovementVM getMovementById(Long id) {
////        return movementRepository.findById(id)
////                .map(InventoryMovementVM::new)
////                .orElseThrow(() -> new RuntimeException("Movement not found"));
//    }
//}
//
//
