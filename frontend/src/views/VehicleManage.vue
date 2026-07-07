<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>车辆管理</span>
          <div>
            <el-input v-model="search.plateNumber" placeholder="车牌号" style="width: 140px; margin-right: 10px;" clearable />
            <el-input v-model="search.brand" placeholder="品牌" style="width: 120px; margin-right: 10px;" clearable />
            <el-button type="primary" @click="loadData">搜索</el-button>
            <el-button type="success" @click="openDialog()">新增车辆</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="车主" width="100" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="model" label="型号" width="100" />
        <el-table-column prop="color" label="颜色" width="80" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="vin" label="车架号" width="180" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑车辆' : '新增车辆'" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户" required>
          <el-select v-model="form.customerId" placeholder="选择客户" filterable style="width: 100%;">
            <el-option v-for="c in customerList" :key="c.id" :label="c.name + ' (' + c.phone + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="车牌号"><el-input v-model="form.plateNumber" /></el-form-item>
        <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
        <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
        <el-form-item label="颜色"><el-input v-model="form.color" /></el-form-item>
        <el-form-item label="年份"><el-input-number v-model="form.year" :min="1990" :max="2030" /></el-form-item>
        <el-form-item label="车架号"><el-input v-model="form.vin" maxlength="17" /></el-form-item>
        <el-form-item label="发动机号"><el-input v-model="form.engineNumber" /></el-form-item>
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
import { getVehicleList, addVehicle, updateVehicle, deleteVehicle } from '../api/vehicle'
import { getCustomerList } from '../api/customer'

const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const search = reactive({ plateNumber: '', brand: '' })
const dialogVisible = ref(false)
const customerList = ref([])
const form = reactive({ id: null, customerId: null, plateNumber: '', brand: '', model: '', color: '', year: null, vin: '', engineNumber: '' })

const loadData = async () => {
  const res = await getVehicleList({ page: page.value, pageSize: pageSize.value, ...search })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadCustomers = async () => {
  const res = await getCustomerList({ page: 1, pageSize: 1000 })
  customerList.value = res.data.records || []
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, customerId: null, plateNumber: '', brand: '', model: '', color: '', year: null, vin: '', engineNumber: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (form.id) {
    await updateVehicle(form.id, form)
  } else {
    await addVehicle(form)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该车辆吗？', '提示', { type: 'warning' })
  await deleteVehicle(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => { loadData(); loadCustomers() })
</script>
