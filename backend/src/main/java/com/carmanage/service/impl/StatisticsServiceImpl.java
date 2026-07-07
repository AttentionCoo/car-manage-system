package com.carmanage.service.impl;

import com.carmanage.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> getMonthlyItemCount(Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();

        String sql = "SELECT bi.id AS itemId, bi.item_name AS projectName, bi.price AS unitPrice, " +
                "COUNT(oi.item_id) AS serviceCount, SUM(oi.quantity) AS totalQuantity, " +
                "SUM(oi.subtotal) AS totalRevenue " +
                "FROM beauty_items bi " +
                "LEFT JOIN order_items oi ON bi.id = oi.item_id " +
                "LEFT JOIN beauty_orders bo ON oi.order_id = bo.id " +
                "WHERE YEAR(bo.order_time) = ? AND MONTH(bo.order_time) = ? " +
                "AND bo.status IN ('COMPLETED', 'PAID') " +
                "GROUP BY bi.id, bi.item_name, bi.price ORDER BY serviceCount DESC";
        List<Map<String, Object>> statistics = jdbcTemplate.queryForList(sql, year, month);
        result.put("statistics", statistics);

        String summarySql = "SELECT COUNT(*) AS totalServices, COALESCE(SUM(oi.subtotal), 0) AS totalRevenue " +
                "FROM order_items oi JOIN beauty_orders bo ON oi.order_id = bo.id " +
                "WHERE YEAR(bo.order_time) = ? AND MONTH(bo.order_time) = ? AND bo.status IN ('COMPLETED','PAID')";
        Map<String, Object> summary = jdbcTemplate.queryForMap(summarySql, year, month);
        summary.put("period", year + "年" + month + "月");
        result.put("summary", summary);

        return result;
    }

    @Override
    public Map<String, Object> getYearlyCustomerCount(Integer year) {
        Map<String, Object> result = new HashMap<>();

        String sql = "SELECT c.id AS customerId, c.name AS customerName, c.phone AS customerPhone, c.gender, " +
                "COUNT(DISTINCT bo.id) AS totalOrders, SUM(oi.quantity) AS totalServiceCount, " +
                "COALESCE(SUM(bo.payable_amount), 0) AS totalSpent, MAX(bo.order_time) AS lastVisitDate " +
                "FROM customers c " +
                "LEFT JOIN beauty_orders bo ON c.id = bo.customer_id AND YEAR(bo.order_time) = ? AND bo.status IN ('COMPLETED','PAID') " +
                "LEFT JOIN order_items oi ON bo.id = oi.order_id " +
                "GROUP BY c.id, c.name, c.phone, c.gender ORDER BY totalSpent DESC";
        List<Map<String, Object>> statistics = jdbcTemplate.queryForList(sql, year);
        result.put("statistics", statistics);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", statistics.size());
        summary.put("activeCustomers", statistics.stream().filter(m -> m.get("totalOrders") != null && Long.parseLong(m.get("totalOrders").toString()) > 0).count());
        summary.put("year", year);
        result.put("summary", summary);

        return result;
    }

    @Override
    public Map<String, Object> getMonthlyRevenue(Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();

        String overviewSql = "SELECT COUNT(*) AS totalOrders, " +
                "SUM(CASE WHEN status IN ('COMPLETED','PAID') THEN 1 ELSE 0 END) AS completedOrders, " +
                "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelledOrders, " +
                "COALESCE(SUM(payable_amount), 0) AS grossRevenue, " +
                "COALESCE(SUM(discount_amount), 0) AS totalDiscount " +
                "FROM beauty_orders WHERE YEAR(order_time) = ? AND MONTH(order_time) = ?";
        Map<String, Object> overview = jdbcTemplate.queryForMap(overviewSql, year, month);
        overview.put("period", year + "年" + month + "月");
        result.put("overview", overview);

        String dailySql = "SELECT DAY(order_time) AS day, COUNT(*) AS dailyOrders, " +
                "COALESCE(SUM(payable_amount), 0) AS dailyRevenue " +
                "FROM beauty_orders WHERE YEAR(order_time) = ? AND MONTH(order_time) = ? " +
                "AND status IN ('COMPLETED','PAID') GROUP BY DAY(order_time) ORDER BY day";
        result.put("dailyTrend", jdbcTemplate.queryForList(dailySql, year, month));

        String itemSql = "SELECT bi.item_name AS itemName, COUNT(oi.id) AS orderCount, " +
                "SUM(oi.subtotal) AS itemRevenue FROM beauty_items bi " +
                "JOIN order_items oi ON bi.id = oi.item_id " +
                "JOIN beauty_orders bo ON oi.order_id = bo.id " +
                "WHERE YEAR(bo.order_time) = ? AND MONTH(bo.order_time) = ? AND bo.status IN ('COMPLETED','PAID') " +
                "GROUP BY bi.item_name ORDER BY itemRevenue DESC";
        result.put("itemRevenueDistribution", jdbcTemplate.queryForList(itemSql, year, month));

        return result;
    }
}
