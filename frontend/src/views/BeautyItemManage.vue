<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>美容项目管理</span>
          <div>
            <el-input v-model="search.keyword" placeholder="搜索项目名称" style="width: 200px; margin-right: 10px;" clearable @clear="loadData" />
            <el-select v-model="search.status" placeholder="状态" style="width: 120px; margin-right: 10px;" clearable @change="loadData">
              <el-option label="启用" :value="1" /><el-option label="停用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
            <el-button type="success" @click="openDialog()">新增项目</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="itemName" label="项目名称" width="150" />
        <el-table-column prop="itemCode" label="项目编码" width="120" />
        <el-table-column prop="price" label="价格(元)" width="100">
          <template #default="{ row }">{{ row.price ? row.price.toFixed(2) : '' }}</template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px; justify-content: flex-end;" v-model:current-page="page" v-model:page-size="pageSize"
        :total="total" layout="total, prev, pager, next" @current-change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑项目' : '新增项目'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称"><el-input v-model="form.itemName" /></el-form-item>
        <el-form-item label="项目编码"><el-input v-model="form.itemCode" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="form.duration" :min="1" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBeautyItemList, addBeautyItem, updateBeautyItem, deleteBeautyItem } from '../api/beautyItem'

const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const search = reactive({ keyword: '', status: null })
const dialogVisible = ref(false)
const form = reactive({ id: null, itemName: '', itemCode: '', price: 0, duration: 30, description: '', status: 1 })

const loadData = async () => {
  const res = await getBeautyItemList({ page: page.value, pageSize: pageSize.value, ...search })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, itemName: '', itemCode: '', price: 0, duration: 30, description: '', status: 1 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (form.id) {
    await updateBeautyItem(form.id, form)
  } else {
    await addBeautyItem(form)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该项目吗？', '提示', { type: 'warning' })
  await deleteBeautyItem(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
