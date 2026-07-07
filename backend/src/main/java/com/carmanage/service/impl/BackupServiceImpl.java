package com.carmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.common.PageResult;
import com.carmanage.entity.BackupRecord;
import com.carmanage.mapper.BackupRecordMapper;
import com.carmanage.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BackupServiceImpl implements BackupService {

    @Autowired
    private BackupRecordMapper backupRecordMapper;
    @Value("${car-manage.backup.path:./backups}")
    private String backupPath;

    @Override
    public BackupRecord createBackup(String backupType, String description, String tables) {
        String backupId = "BK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
        String filePath = backupPath + File.separator + fileName;

        BackupRecord record = new BackupRecord();
        record.setBackupId(backupId);
        record.setBackupType(backupType);
        record.setFileName(fileName);
        record.setFilePath(filePath);
        record.setStatus("IN_PROGRESS");
        record.setTables(tables);
        record.setStartTime(LocalDateTime.now());
        record.setCreator("admin");
        record.setDescription(description);
        backupRecordMapper.insert(record);

        try {
            Path dir = Paths.get(backupPath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String dumpCmd = String.format("mysqldump -u root -proot car_beauty > %s", filePath);
            Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", dumpCmd});
            process.waitFor();

            File file = new File(filePath);
            record.setFileSize(file.length());
            record.setStatus("COMPLETED");
            record.setEndTime(LocalDateTime.now());
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setEndTime(LocalDateTime.now());
            record.setDescription(description + " [ERROR: " + e.getMessage() + "]");
        }
        backupRecordMapper.updateById(record);
        return record;
    }

    @Override
    public PageResult<BackupRecord> getBackupList(Integer page, Integer pageSize, String status) {
        Page<BackupRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<BackupRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(BackupRecord::getStatus, status);
        }
        wrapper.orderByDesc(BackupRecord::getCreateTime);
        IPage<BackupRecord> result = backupRecordMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public BackupRecord getBackupDetail(String backupId) {
        LambdaQueryWrapper<BackupRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BackupRecord::getBackupId, backupId);
        return backupRecordMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> restore(String backupId, String restoreType, String confirmPassword, String selectedTables) {
        BackupRecord record = getBackupDetail(backupId);
        if (record == null || !"COMPLETED".equals(record.getStatus())) {
            throw new RuntimeException("备份记录不存在或未完成");
        }

        Map<String, Object> result = new HashMap<>();
        String restoreId = "RS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        result.put("restoreId", restoreId);
        result.put("sourceBackupId", backupId);
        result.put("restoreType", restoreType);
        result.put("status", "PENDING");

        try {
            String restoreCmd = String.format("mysql -u root -proot car_beauty < %s", record.getFilePath());
            Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", restoreCmd});
            process.waitFor();
            result.put("status", "COMPLETED");
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getRestoreStatus(String restoreId) {
        Map<String, Object> result = new HashMap<>();
        result.put("restoreId", restoreId);
        result.put("status", "COMPLETED");
        result.put("progress", 100);
        return result;
    }

    @Override
    public void deleteBackup(String backupId, String confirmPassword) {
        BackupRecord record = getBackupDetail(backupId);
        if (record == null) {
            throw new RuntimeException("备份记录不存在");
        }
        File file = new File(record.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        backupRecordMapper.deleteById(record.getId());
    }

    @Override
    public String downloadBackup(String backupId) {
        BackupRecord record = getBackupDetail(backupId);
        return record != null ? record.getFilePath() : null;
    }
}
