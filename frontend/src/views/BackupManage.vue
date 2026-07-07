<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>数据备份与恢复</span>
          <el-button type="success" @click="openBackupDialog">创建备份</el-button>
        </div>
      </template>

      <el-table :data="backupList" stripe>
        <el-table-column prop="backupId" label="备份ID" width="200" />
        <el-table-column prop="backupType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.backupType === 'FULL' ? '' : row.backupType === 'INCREMENTAL' ? 'warning' : 'info'">
              {{ backupTypeMap[row.backupType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" width="220" />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="backupStatusMap[row.status]?.type">{{ backupStatusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleRestore(row)" :disabled="row.status !== 'COMPLETED'">恢复</el-button>
            <el-button size="small" @click="handleDownload(row)" :disabled="row.status !== 'COMPLETED'">下载</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px; justify-content: flex-end;" v-model:current-page="page" v-model:page-size="pageSize"
        :total="total" layout="total, prev, pager, next" @current-change="loadData" />
    </el-card>

    <!-- 创建备份弹窗 -->
    <el-dialog v-model="backupDialogVisible" title="创建数据备份" width="500px">
      <el-form :model="backupForm" label-width="100px">
        <el-form-item label="备份类型">
          <el-radio-group v-model="backupForm.backupType">
            <el-radio value="FULL">全量备份</el-radio>
            <el-radio value="INCREMENTAL">增量备份</el-radio>
            <el-radio value="PARTIAL">部分备份</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="backupForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="backupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateBackup">确认备份</el-button>
      </template>
    </el-dialog>

    <!-- 恢复确认弹窗 -->
    <el-dialog v-model="restoreDialogVisible" title="数据恢复确认" width="450px">
      <el-alert title="警告：此操作将覆盖当前数据，请谨慎操作！" type="warning" :closable="false" show-icon style="margin-bottom: 16px;" />
      <el-form :model="restoreForm" label-width="100px">
        <el-form-item label="恢复方式">
          <el-radio-group v-model="restoreForm.restoreType">
            <el-radio value="OVERWRITE">覆盖恢复</el-radio>
            <el-radio value="MERGE">合并恢复</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="restoreForm.confirmPassword" type="password" placeholder="请输入管理员密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restoreDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleConfirmRestore">确认恢复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createBackup, getBackupList, restoreBackup, deleteBackup } from '../api/backup'

const backupList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const backupDialogVisible = ref(false)
const restoreDialogVisible = ref(false)
const currentBackup = ref(null)

const backupTypeMap = { FULL: '全量备份', INCREMENTAL: '增量备份', PARTIAL: '部分备份' }
const backupStatusMap = {
  PENDING: { label: '等待中', type: 'info' },
  IN_PROGRESS: { label: '进行中', type: 'warning' },
  COMPLETED: { label: '已完成', type: 'success' },
  FAILED: { label: '失败', type: 'danger' },
  DELETED: { label: '已删除', type: 'info' }
}

const backupForm = reactive({ backupType: 'FULL', description: '' })
const restoreForm = reactive({ restoreType: 'OVERWRITE', confirmPassword: '' })

const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / 1048576).toFixed(2) + ' MB'
}

const loadData = async () => {
  const res = await getBackupList({ page: page.value, pageSize: pageSize.value })
  backupList.value = res.data.records || []
  total.value = res.data.total || 0
}

const openBackupDialog = () => {
  Object.assign(backupForm, { backupType: 'FULL', description: '' })
  backupDialogVisible.value = true
}

const handleCreateBackup = async () => {
  await createBackup(backupForm)
  ElMessage.success('备份任务已创建')
  backupDialogVisible.value = false
  loadData()
}

const handleRestore = (row) => {
  currentBackup.value = row
  Object.assign(restoreForm, { restoreType: 'OVERWRITE', confirmPassword: '' })
  restoreDialogVisible.value = true
}

const handleConfirmRestore = async () => {
  if (!restoreForm.confirmPassword) {
    ElMessage.warning('请输入确认密码')
    return
  }
  await restoreBackup(currentBackup.value.backupId, restoreForm)
  ElMessage.success('恢复任务已创建')
  restoreDialogVisible.value = false
}

const handleDownload = (row) => {
  window.open(`/api/v1/backup/${row.backupId}/download`, '_blank')
}

const handleDelete = (row) => {
  ElMessageBox.prompt('请输入管理员密码确认删除', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password'
  }).then(async ({ value }) => {
    await deleteBackup(row.backupId, { confirmPassword: value })
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>
