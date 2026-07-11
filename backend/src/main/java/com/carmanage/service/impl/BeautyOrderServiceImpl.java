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
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BeautyOrderServiceImpl implements BeautyOrderService {

    private static final Set<String> VALID_STATUS = new HashSet<>(
            Arrays.asList("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED", "PAID"));

    private static final Set<String> VALID_PAYMENT_METHODS = new HashSet<>(
            Arrays.asList("CASH", "WECHAT", "ALIPAY", "CARD", "OTHER"));

    private static final Map<String, Set<String>> STATUS_TRANSITIONS = new HashMap<>();

    static {
        STATUS_TRANSITIONS.put("PENDING", new HashSet<>(Arrays.asList("IN_PROGRESS", "CANCELLED")));
        STATUS_TRANSITIONS.put("IN_PROGRESS", new HashSet<>(Arrays.asList("COMPLETED", "CANCELLED")));
        STATUS_TRANSITIONS.put("COMPLETED", new HashSet<>(Collections.singletonList("PAID")));
        STATUS_TRANSITIONS.put("PAID", Collections.emptySet());
        STATUS_TRANSITIONS.put("CANCELLED", Collections.emptySet());
    }

    @Autowired
    private BeautyOrderMapper beautyOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private BeautyItemMapper beautyItemMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private VehicleMapper vehicleMapper;
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
        BeautyOrder order = beautyOrderMapper.selectDetailById(orderId);
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
        if (order.getCustomerId() == null) {
            throw new RuntimeException("客户ID不能为空");
        }
        if (order.getVehicleId() == null) {
            throw new RuntimeException("车辆ID不能为空");
        }

        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        Vehicle vehicle = vehicleMapper.selectById(order.getVehicleId());
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }
        if (!vehicle.getCustomerId().equals(order.getCustomerId())) {
            throw new RuntimeException("该车辆不属于所选客户");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("订单项目不能为空");
        }

        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> validItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            if (item.getItemId() == null) {
                throw new RuntimeException("项目ID不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("项目数量必须大于0");
            }

            BeautyItem bi = beautyItemMapper.selectById(item.getItemId());
            if (bi == null) {
                throw new RuntimeException("美容项目不存在，ID: " + item.getItemId());
            }
            if (bi.getStatus() != null && bi.getStatus() == 0) {
                throw new RuntimeException("美容项目已停用: " + bi.getItemName());
            }

            item.setUnitPrice(bi.getPrice());
            item.setSubtotal(bi.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setItemName(bi.getItemName());
            totalAmount = totalAmount.add(item.getSubtotal());
            validItems.add(item);
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayableAmount(totalAmount);

        beautyOrderMapper.insert(order);

        for (OrderItem item : validItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        order.setItems(validItems);
        return order;
    }

    @Override
    public void updateStatus(Long orderId, String status, String remark) {
        if (!VALID_STATUS.contains(status)) {
            throw new RuntimeException("无效的订单状态: " + status);
        }

        BeautyOrder current = beautyOrderMapper.selectById(orderId);
        if (current == null) {
            throw new RuntimeException("订单不存在");
        }

        Set<String> allowed = STATUS_TRANSITIONS.get(current.getStatus());
        if (allowed == null || !allowed.contains(status)) {
            throw new RuntimeException("订单状态不允许从 " + current.getStatus() + " 变更为 " + status);
        }

        BeautyOrder update = new BeautyOrder();
        update.setId(orderId);
        update.setStatus(status);
        if ("COMPLETED".equals(status)) {
            update.setCompleteTime(LocalDateTime.now());
        }
        if (remark != null && !remark.trim().isEmpty()) {
            String newRemark = (current.getRemark() != null ? current.getRemark() + "\n" : "") + remark;
            update.setRemark(newRemark.trim());
        }
        beautyOrderMapper.updateById(update);
    }

    @Override
    @Transactional
    public Payment pay(Long orderId, Payment payment) {
        BeautyOrder order = beautyOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if ("PAID".equals(order.getStatus())) {
            throw new RuntimeException("该订单已支付，不能重复支付");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("只有已完成的订单才能支付");
        }

        if (payment.getPaymentMethod() == null || !VALID_PAYMENT_METHODS.contains(payment.getPaymentMethod())) {
            throw new RuntimeException("无效的支付方式");
        }

        String paymentNo = generatePaymentNo();
        payment.setOrderId(orderId);
        payment.setPaymentNo(paymentNo);
        payment.setTotalAmount(order.getTotalAmount());
        payment.setPayTime(LocalDateTime.now());

        BigDecimal discountAmount = payment.getDiscountAmount() != null ? payment.getDiscountAmount() : BigDecimal.ZERO;
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("折扣金额不能为负数");
        }
        if (discountAmount.compareTo(order.getTotalAmount()) > 0) {
            throw new RuntimeException("折扣金额不能超过总金额");
        }
        payment.setDiscountAmount(discountAmount);

        BigDecimal payableAmount = order.getTotalAmount().subtract(discountAmount);

        String method = payment.getPaymentMethod();
        if ("WECHAT".equals(method) || "ALIPAY".equals(method) || "CARD".equals(method)) {
            payment.setPaidAmount(payableAmount);
            payment.setChangeAmount(BigDecimal.ZERO);
        } else {
            BigDecimal paidAmount = payment.getPaidAmount() != null ? payment.getPaidAmount() : payableAmount;
            if (paidAmount.compareTo(payableAmount) < 0) {
                throw new RuntimeException("实付金额不足，还需支付 ¥" + payableAmount.subtract(paidAmount));
            }
            payment.setPaidAmount(paidAmount);
            payment.setChangeAmount(paidAmount.subtract(payableAmount));
        }

        paymentMapper.insert(payment);

        BeautyOrder updateOrder = new BeautyOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus("PAID");
        updateOrder.setDiscountAmount(discountAmount);
        updateOrder.setPayableAmount(payableAmount);
        beautyOrderMapper.updateById(updateOrder);

        return payment;
    }

    @Override
    public void cancel(Long orderId, String reason) {
        BeautyOrder order = beautyOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus()) && !"IN_PROGRESS".equals(order.getStatus())) {
            throw new RuntimeException("当前状态不允许取消");
        }

        BeautyOrder update = new BeautyOrder();
        update.setId(orderId);
        update.setStatus("CANCELLED");
        if (reason != null && !reason.trim().isEmpty()) {
            String newRemark = (order.getRemark() != null ? order.getRemark() + "\n" : "") + "取消原因: " + reason;
            update.setRemark(newRemark.trim());
        }
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
                "FROM beauty_orders WHERE DATE(order_time) = ? AND is_deleted = 0";
        Map<String, Object> todayStats = jdbcTemplate.queryForMap(sql, today);
        result.putAll(todayStats);

        String monthSql = "SELECT COALESCE(SUM(payable_amount), 0) AS monthRevenue FROM beauty_orders " +
                "WHERE YEAR(order_time) = ? AND MONTH(order_time) = ? AND status IN ('COMPLETED','PAID') AND is_deleted = 0";
        Map<String, Object> monthStats = jdbcTemplate.queryForMap(monthSql, today.getYear(), today.getMonthValue());
        result.putAll(monthStats);

        return result;
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }
}