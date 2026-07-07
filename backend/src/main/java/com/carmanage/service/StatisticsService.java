package com.carmanage.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getMonthlyItemCount(Integer year, Integer month);
    Map<String, Object> getYearlyCustomerCount(Integer year);
    Map<String, Object> getMonthlyRevenue(Integer year, Integer month);
}
