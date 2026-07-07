package com.carmanage.service;

import com.carmanage.common.PageResult;
import com.carmanage.entity.Customer;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    PageResult<Customer> getPage(Integer page, Integer pageSize, String name, String phone, String gender);
    Customer getDetail(Long id);
    void add(Customer customer);
    void update(Customer customer);
    void delete(Long id);
    boolean checkPhone(String phone, Long excludeId);
    List<Map<String, Object>> getYearlyCustomerCount(Integer year);
}
