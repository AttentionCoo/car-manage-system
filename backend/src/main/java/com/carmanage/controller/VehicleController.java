package com.carmanage.controller;

import com.carmanage.common.PageResult;
import com.carmanage.common.Result;
import com.carmanage.entity.Vehicle;
import com.carmanage.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public Result<PageResult<Vehicle>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String brand) {
        return Result.success(vehicleService.getPage(page, pageSize, customerId, plateNumber, brand));
    }

    @GetMapping("/{id}")
    public Result<Vehicle> getById(@PathVariable Long id) {
        return Result.success(vehicleService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Vehicle vehicle) {
        vehicleService.add(vehicle);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        vehicleService.update(vehicle);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return Result.success("删除成功", null);
    }
}
