package com.carmanage.service;

import com.carmanage.common.PageResult;
import com.carmanage.entity.Vehicle;
import java.util.List;

public interface VehicleService {
    PageResult<Vehicle> getPage(Integer page, Integer pageSize, Long customerId, String plateNumber, String brand);
    Vehicle getById(Long id);
    void add(Vehicle vehicle);
    void update(Vehicle vehicle);
    void delete(Long id);
    List<Vehicle> getByCustomerId(Long customerId);
}
