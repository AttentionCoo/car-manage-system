package com.carmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.common.PageResult;
import com.carmanage.entity.Customer;
import com.carmanage.entity.Vehicle;
import com.carmanage.mapper.CustomerMapper;
import com.carmanage.mapper.VehicleMapper;
import com.carmanage.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PageResult<Customer> getPage(Integer page, Integer pageSize, String name, String phone, String gender) {
        Page<Customer> pageParam = new Page<>(page, pageSize);
        IPage<Customer> result = customerMapper.selectPageWithCondition(pageParam, name, phone, gender);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public Customer getDetail(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public void add(Customer customer) {
        if (!"男".equals(customer.getGender()) && !"女".equals(customer.getGender())) {
            throw new RuntimeException("性别必须为'男'或'女'");
        }
        if (checkPhone(customer.getPhone(), null)) {
            throw new RuntimeException("手机号已存在");
        }
        customerMapper.insert(customer);
    }

    @Override
    public void update(Customer customer) {
        if (customer.getGender() != null && !"男".equals(customer.getGender()) && !"女".equals(customer.getGender())) {
            throw new RuntimeException("性别必须为'男'或'女'");
        }
        if (customer.getPhone() != null && checkPhone(customer.getPhone(), customer.getId())) {
            throw new RuntimeException("手机号已存在");
        }
        customerMapper.updateById(customer);
    }

    @Override
    public void delete(Long id) {
        customerMapper.deleteById(id);
    }

    @Override
    public boolean checkPhone(String phone, Long excludeId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(Customer::getId, excludeId);
        }
        return customerMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Map<String, Object>> getYearlyCustomerCount(Integer year) {
        String sql = "SELECT c.id AS customerId, c.name AS customerName, c.phone AS customerPhone, c.gender, " +
                "COUNT(DISTINCT bo.id) AS totalOrders, SUM(oi.quantity) AS totalServiceCount, " +
                "SUM(bo.payable_amount) AS totalSpent, MAX(bo.order_time) AS lastVisitDate " +
                "FROM customers c " +
                "LEFT JOIN beauty_orders bo ON c.id = bo.customer_id AND YEAR(bo.order_time) = ? " +
                "LEFT JOIN order_items oi ON bo.id = oi.order_id " +
                "GROUP BY c.id, c.name, c.phone, c.gender ORDER BY totalSpent DESC";
        return jdbcTemplate.queryForList(sql, year);
    }
}
