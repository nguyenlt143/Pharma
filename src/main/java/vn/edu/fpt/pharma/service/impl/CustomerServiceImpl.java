package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.repository.CustomerRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CustomerService;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Long, CustomerRepository> implements CustomerService {

    public CustomerServiceImpl(CustomerRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public Customer getOrCreate(String name, String phoneNumber) {
        return repository.findByPhone(phoneNumber)
                .orElseGet(() -> {
                    // Nếu không có thì tạo mới
                    Customer c = new Customer();
                    c.setName(name);
                    c.setPhone(phoneNumber);
                    return repository.save(c);
                });
    }
}
