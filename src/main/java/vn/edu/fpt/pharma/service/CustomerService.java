package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.entity.Customer;

public interface CustomerService extends BaseService<Customer, Long> {
    Customer getOrCreate(String name, String phoneNumber);
}
