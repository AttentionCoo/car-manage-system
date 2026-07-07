package com.carmanage.controller;

import com.carmanage.common.Result;
import com.carmanage.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/monthly-item-count")
    public Result<Map<String, Object>> getMonthlyItemCount(
            @RequestParam Integer year, @RequestParam Integer month) {
        return Result.success(statisticsService.getMonthlyItemCount(year, month));
    }

    @GetMapping("/yearly-customer-count")
    public Result<Map<String, Object>> getYearlyCustomerCount(@RequestParam Integer year) {
        return Result.success(statisticsService.getYearlyCustomerCount(year));
    }

    @GetMapping("/monthly-revenue")
    public Result<Map<String, Object>> getMonthlyRevenue(
            @RequestParam Integer year, @RequestParam Integer month) {
        return Result.success(statisticsService.getMonthlyRevenue(year, month));
    }
}
