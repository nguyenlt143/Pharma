package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.repository.CustomerRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CustomerService;

import java.util.Optional;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Long, CustomerRepository> implements CustomerService {

    public CustomerServiceImpl(CustomerRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    @Transactional
    public Customer getOrCreate(String name, String phoneNumber) {
        // Tìm customer hiện có
        Optional<Customer> existing = repository.findByPhone(phoneNumber);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Tạo mới nếu không tồn tại
        try {
            Customer c = new Customer();
            c.setName(name);
            c.setPhone(phoneNumber);
            return repository.save(c);
        } catch (Exception e) {
            // Nếu có lỗi constraint violation, thử tìm lại
            // (có thể do race condition)
            Optional<Customer> retryFind = repository.findByPhone(phoneNumber);
            if (retryFind.isPresent()) {
                return retryFind.get();
            }
            // Nếu vẫn không tìm thấy, throw exception
            throw new RuntimeException("Không thể tạo customer với số điện thoại: " + phoneNumber + ". Lỗi: " + e.getMessage());
        }
    }
}
