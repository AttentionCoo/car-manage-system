package com.carmanage.service;

import com.carmanage.common.PageResult;
import com.carmanage.entity.BackupRecord;
import java.util.Map;

public interface BackupService {
    BackupRecord createBackup(String backupType, String description, String tables);
    PageResult<BackupRecord> getBackupList(Integer page, Integer pageSize, String status);
    BackupRecord getBackupDetail(String backupId);
    Map<String, Object> restore(String backupId, String restoreType, String confirmPassword, String selectedTables);
    Map<String, Object> getRestoreStatus(String restoreId);
    void deleteBackup(String backupId, String confirmPassword);
    String downloadBackup(String backupId);
}
