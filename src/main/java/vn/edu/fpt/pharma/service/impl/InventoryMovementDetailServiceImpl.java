package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryMovementDetailService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryMovementDetailServiceImpl extends BaseServiceImpl<InventoryMovementDetail, Long, InventoryMovementDetailRepository> implements InventoryMovementDetailService {

    public InventoryMovementDetailServiceImpl(InventoryMovementDetailRepository repository, AuditService auditService, InventoryMovementDetailRepository detailRepository) {
        super(repository, auditService);
        this.detailRepository = detailRepository;
    }
    private final InventoryMovementDetailRepository detailRepository;


    @Override
    public List<InventoryMovementDetailVM> getDetailsByMovementId(Long movementId) {
        return repository.findByMovementId(movementId)
                .stream()
                .map(InventoryMovementDetailVM::new)
                .toList();
    }


}
