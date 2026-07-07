package com.carmanage.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.common.PageResult;
import com.carmanage.entity.Vehicle;
import com.carmanage.mapper.VehicleMapper;
import com.carmanage.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public PageResult<Vehicle> getPage(Integer page, Integer pageSize, Long customerId, String plateNumber, String brand) {
        Page<Vehicle> pageParam = new Page<>(page, pageSize);
        IPage<Vehicle> result = vehicleMapper.selectPageWithCondition(pageParam, customerId, plateNumber, brand);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public Vehicle getById(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Override
    public void add(Vehicle vehicle) {
        vehicleMapper.insert(vehicle);
    }

    @Override
    public void update(Vehicle vehicle) {
        vehicleMapper.updateById(vehicle);
    }

    @Override
    public void delete(Long id) {
        vehicleMapper.deleteById(id);
    }

    @Override
    public List<Vehicle> getByCustomerId(Long customerId) {
        return vehicleMapper.selectByCustomerId(customerId);
    }
}
