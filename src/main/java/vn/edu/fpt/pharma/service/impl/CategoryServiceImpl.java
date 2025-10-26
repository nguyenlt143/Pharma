package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CategoryService;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long, CategoryRepository> implements CategoryService {

    public CategoryServiceImpl(CategoryRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
