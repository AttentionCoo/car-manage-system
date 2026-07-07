package com.carmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.common.PageResult;
import com.carmanage.entity.*;
import com.carmanage.mapper.*;
import com.carmanage.service.BeautyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BeautyOrderServiceImpl implements BeautyOrderService {

    @Autowired
    private BeautyOrderMapper beautyOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private BeautyItemMapper beautyItemMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PageResult<BeautyOrder> getPage(Integer page, Integer pageSize, String orderNo, Long customerId, String status) {
        Page<BeautyOrder> pageParam = new Page<>(page, pageSize);
        IPage<BeautyOrder> result = beautyOrderMapper.selectPageWithCondition(pageParam, orderNo, customerId, status);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public BeautyOrder getDetail(Long orderId) {
        BeautyOrder order = beautyOrderMapper.selectById(orderId);
        if (order != null) {
            order.setItems(orderItemMapper.selectByOrderId(orderId));
            LambdaQueryWrapper<Payment> pw = new LambdaQueryWrapper<>();
            pw.eq(Payment::getOrderId, orderId);
            List<Payment> payments = paymentMapper.selectList(pw);
            if (!payments.isEmpty()) {
                order.setPayment(payments.get(0));
            }
        }
        return order;
    }

    @Override
    @Transactional
    public BeautyOrder createOrder(BeautyOrder order) {
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
        order.setOrderNo(orderNo);
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                BeautyItem bi = beautyItemMapper.selectById(item.getItemId());
                if (bi != null) {
                    item.setUnitPrice(bi.getPrice());
                    item.setSubtotal(bi.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalAmount = totalAmount.add(item.getSubtotal());
                    item.setItemName(bi.getItemName());
                }
            }
        }
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayableAmount(totalAmount);

        beautyOrderMapper.insert(order);

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrderId(order.getId());
                orderItemMapper.insert(item);
            }
        }
        return order;
    }

    @Override
    public void updateStatus(Long orderId, String status, String remark) {
        BeautyOrder order = new BeautyOrder();
        order.setId(orderId);
        order.setStatus(status);
        if ("COMPLETED".equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }
        beautyOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public Payment pay(Long orderId, Payment payment) {
        BeautyOrder order = beautyOrderMapper.selectById(orderId);
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("只有已完成的订单才能支付");
        }

        String paymentNo = "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
        payment.setOrderId(orderId);
        payment.setPaymentNo(paymentNo);
        payment.setTotalAmount(order.getTotalAmount());
        payment.setPayTime(LocalDateTime.now());

        BigDecimal payable = order.getTotalAmount().subtract(payment.getDiscountAmount() != null ? payment.getDiscountAmount() : BigDecimal.ZERO);
        payment.setPaidAmount(payable);
        payment.setChangeAmount(BigDecimal.ZERO);
        paymentMapper.insert(payment);

        BeautyOrder updateOrder = new BeautyOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus("PAID");
        updateOrder.setDiscountAmount(payment.getDiscountAmount());
        updateOrder.setPayableAmount(payable);
        beautyOrderMapper.updateById(updateOrder);

        return payment;
    }

    @Override
    public void cancel(Long orderId, String reason) {
        BeautyOrder order = beautyOrderMapper.selectById(orderId);
        if (!"PENDING".equals(order.getStatus()) && !"IN_PROGRESS".equals(order.getStatus())) {
            throw new RuntimeException("当前状态不允许取消");
        }
        BeautyOrder update = new BeautyOrder();
        update.setId(orderId);
        update.setStatus("CANCELLED");
        beautyOrderMapper.updateById(update);
    }

    @Override
    public List<BeautyOrder> getTodayPending() {
        LambdaQueryWrapper<BeautyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BeautyOrder::getStatus, "PENDING")
                .ge(BeautyOrder::getOrderTime, LocalDate.now().atStartOfDay())
                .orderByAsc(BeautyOrder::getAppointmentTime);
        return beautyOrderMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getOverview(String date) {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = date != null ? LocalDate.parse(date) : LocalDate.now();

        String sql = "SELECT " +
                "COUNT(*) AS todayOrders, " +
                "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pendingOrders, " +
                "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgressOrders, " +
                "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedOrders, " +
                "COALESCE(SUM(CASE WHEN status IN ('COMPLETED','PAID') THEN payable_amount ELSE 0 END), 0) AS todayRevenue " +
                "FROM beauty_orders WHERE DATE(order_time) = ?";
        Map<String, Object> todayStats = jdbcTemplate.queryForMap(sql, today);
        result.putAll(todayStats);

        String monthSql = "SELECT COALESCE(SUM(payable_amount), 0) AS monthRevenue FROM beauty_orders " +
                "WHERE YEAR(order_time) = ? AND MONTH(order_time) = ? AND status IN ('COMPLETED','PAID')";
        Map<String, Object> monthStats = jdbcTemplate.queryForMap(monthSql, today.getYear(), today.getMonthValue());
        result.putAll(monthStats);

        return result;
    }
}
