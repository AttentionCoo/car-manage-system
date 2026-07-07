package com.carmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("customers")
public class Customer implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private String address;
    private String email;
    private String remark;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
