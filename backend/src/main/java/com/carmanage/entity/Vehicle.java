package com.carmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("vehicles")
public class Vehicle implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String plateNumber;
    private String vin;
    private String brand;
    private String model;
    private String color;
    private Integer year;
    private String engineNumber;
    private LocalDate registerDate;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String customerName;
}
