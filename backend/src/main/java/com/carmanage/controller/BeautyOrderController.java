package com.carmanage.controller;

import com.carmanage.common.PageResult;
import com.carmanage.common.Result;
import com.carmanage.entity.BeautyOrder;
import com.carmanage.entity.Payment;
import com.carmanage.service.BeautyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/beauty-orders")
public class BeautyOrderController {

    @Autowired
    private BeautyOrderService beautyOrderService;

    @GetMapping
    public Result<PageResult<BeautyOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status) {
        return Result.success(beautyOrderService.getPage(page, pageSize, orderNo, customerId, status));
    }

    @GetMapping("/{orderId}")
    public Result<BeautyOrder> getDetail(@PathVariable Long orderId) {
        return Result.success(beautyOrderService.getDetail(orderId));
    }

    @PostMapping
    public Result<BeautyOrder> createOrder(@RequestBody BeautyOrder order) {
        return Result.success("订单创建成功", beautyOrderService.createOrder(order));
    }

    @PutMapping("/{orderId}/status")
    public Result<Void> updateStatus(@PathVariable Long orderId, @RequestBody Map<String, String> params) {
        beautyOrderService.updateStatus(orderId, params.get("status"), params.get("remark"));
        return Result.success("状态更新成功", null);
    }

    @PostMapping("/{orderId}/payment")
    public Result<Payment> pay(@PathVariable Long orderId, @RequestBody Payment payment) {
        try {
            return Result.success("支付成功", beautyOrderService.pay(orderId, payment));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId, @RequestBody Map<String, String> params) {
        try {
            beautyOrderService.cancel(orderId, params.get("reason"));
            return Result.success("取消成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/today/pending")
    public Result<List<BeautyOrder>> getTodayPending() {
        return Result.success(beautyOrderService.getTodayPending());
    }

    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> getOverview(@RequestParam(required = false) String date) {
        return Result.success(beautyOrderService.getOverview(date));
    }
}
