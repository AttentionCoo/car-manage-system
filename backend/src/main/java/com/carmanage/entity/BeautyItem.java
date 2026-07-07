package com.carmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("beauty_items")
public class BeautyItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemName;
    private String itemCode;
    private BigDecimal price;
    private Integer duration;
    private String description;
    private Integer status;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
