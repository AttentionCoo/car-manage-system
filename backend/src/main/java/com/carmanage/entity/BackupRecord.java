package com.carmanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("backup_records")
public class BackupRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String backupId;
    private String backupType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String status;
    private String tables;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String creator;
    private String description;
    private String md5Checksum;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}