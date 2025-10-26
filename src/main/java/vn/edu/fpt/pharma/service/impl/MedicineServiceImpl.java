package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.repository.MedicineRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.MedicineService;

@Service
public class MedicineServiceImpl extends BaseServiceImpl<Medicine, Long, MedicineRepository> implements MedicineService {

    public MedicineServiceImpl(MedicineRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
