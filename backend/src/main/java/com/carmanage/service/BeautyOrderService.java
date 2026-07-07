package com.carmanage.service;

import com.carmanage.common.PageResult;
import com.carmanage.entity.BeautyOrder;
import com.carmanage.entity.Payment;
import java.util.List;
import java.util.Map;

public interface BeautyOrderService {
    PageResult<BeautyOrder> getPage(Integer page, Integer pageSize, String orderNo, Long customerId, String status);
    BeautyOrder getDetail(Long orderId);
    BeautyOrder createOrder(BeautyOrder order);
    void updateStatus(Long orderId, String status, String remark);
    Payment pay(Long orderId, Payment payment);
    void cancel(Long orderId, String reason);
    List<BeautyOrder> getTodayPending();
    Map<String, Object> getOverview(String date);
}
