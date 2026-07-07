package com.carmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("beauty_orders")
public class BeautyOrder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private Long vehicleId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payableAmount;
    private String status;
    private LocalDateTime appointmentTime;
    private LocalDateTime orderTime;
    private LocalDateTime completeTime;
    private String remark;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String customerName;
    @TableField(exist = false)
    private String customerPhone;
    @TableField(exist = false)
    private String plateNumber;
    @TableField(exist = false)
    private List<OrderItem> items;
    @TableField(exist = false)
    private Payment payment;
}
