package com.carmanage.controller;

import com.carmanage.common.PageResult;
import com.carmanage.common.Result;
import com.carmanage.entity.BackupRecord;
import com.carmanage.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/create")
    public Result<BackupRecord> createBackup(@RequestBody Map<String, Object> params) {
        String backupType = (String) params.get("backupType");
        String description = (String) params.get("description");
        String tables = params.get("tables") != null ? params.get("tables").toString() : null;
        return Result.success("备份任务已创建", backupService.createBackup(backupType, description, tables));
    }

    @GetMapping("/list")
    public Result<PageResult<BackupRecord>> getBackupList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(backupService.getBackupList(page, pageSize, status));
    }

    @GetMapping("/{backupId}")
    public Result<BackupRecord> getBackupDetail(@PathVariable String backupId) {
        return Result.success(backupService.getBackupDetail(backupId));
    }

    @GetMapping("/{backupId}/download")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String backupId) {
        String filePath = backupService.downloadBackup(backupId);
        if (filePath == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/{backupId}/restore")
    public Result<Map<String, Object>> restore(@PathVariable String backupId, @RequestBody Map<String, Object> params) {
        String restoreType = (String) params.get("restoreType");
        String confirmPassword = (String) params.get("confirmPassword");
        String selectedTables = params.get("selectedTables") != null ? params.get("selectedTables").toString() : null;
        return Result.success("恢复任务已创建", backupService.restore(backupId, restoreType, confirmPassword, selectedTables));
    }

    @GetMapping("/restore/{restoreId}/status")
    public Result<Map<String, Object>> getRestoreStatus(@PathVariable String restoreId) {
        return Result.success(backupService.getRestoreStatus(restoreId));
    }

    @DeleteMapping("/{backupId}")
    public Result<Void> deleteBackup(@PathVariable String backupId, @RequestBody Map<String, String> params) {
        backupService.deleteBackup(backupId, params.get("confirmPassword"));
        return Result.success("删除成功", null);
    }
}
