package com.carmanage.controller;

import com.carmanage.common.PageResult;
import com.carmanage.common.Result;
import com.carmanage.entity.Customer;
import com.carmanage.entity.Vehicle;
import com.carmanage.service.CustomerService;
import com.carmanage.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public Result<PageResult<Customer>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String gender) {
        return Result.success(customerService.getPage(page, pageSize, name, phone, gender));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("customer", customerService.getDetail(id));
        data.put("vehicles", vehicleService.getByCustomerId(id));
        return Result.success(data);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Customer customer) {
        try {
            customerService.add(customer);
            return Result.success("新增成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            customerService.update(customer);
            return Result.success("修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(@RequestParam String phone, @RequestParam(required = false) Long excludeId) {
        return Result.success(customerService.checkPhone(phone, excludeId));
    }

    @GetMapping("/{customerId}/vehicles")
    public Result<List<Vehicle>> getVehiclesByCustomer(@PathVariable Long customerId) {
        return Result.success(vehicleService.getByCustomerId(customerId));
    }
}