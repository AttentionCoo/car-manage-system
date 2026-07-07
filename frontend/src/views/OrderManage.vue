<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>订单管理</span>
          <div>
            <el-input v-model="search.orderNo" placeholder="订单号" style="width: 180px; margin-right: 10px;" clearable />
            <el-select v-model="search.status" placeholder="状态" style="width: 120px; margin-right: 10px;" clearable @change="loadData">
              <el-option label="待处理" value="PENDING" /><el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" /><el-option label="已支付" value="PAID" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
            <el-button type="success" @click="openCreateDialog()">创建订单</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">{{ row.totalAmount ? Number(row.totalAmount).toFixed(2) : '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="payableAmount" label="应付金额" width="100">
          <template #default="{ row }">{{ row.payableAmount ? Number(row.payableAmount).toFixed(2) : '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status] || ''">{{ statusLabelMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetailDialog(row)">详情</el-button>
            <el-button size="small" type="warning" @click="handleStartWork(row)" v-if="row.status === 'PENDING'">开始施工</el-button>
            <el-button size="small" type="success" @click="openPayDialog(row)" v-if="row.status === 'COMPLETED'">收款</el-button>
            <el-button size="small" type="danger" @click="handleCancel(row.id)" v-if="['PENDING','IN_PROGRESS'].includes(row.status)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px; justify-content: flex-end;" v-model:current-page="page" v-model:page-size="pageSize"
        :total="total" layout="total, prev, pager, next" @current-change="loadData" />
    </el-card>

    <!-- 创建订单弹窗 -->
    <el-dialog v-model="createVisible" title="创建美容订单" width="650px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="客户">
          <el-select v-model="createForm.customerId" placeholder="选择客户" filterable @change="onCustomerChange" style="width: 100%;">
            <el-option v-for="c in customerList" :key="c.id" :label="c.name + '(' + c.phone + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆">
          <el-select v-model="createForm.vehicleId" placeholder="选择车辆" style="width: 100%;">
            <el-option v-for="v in vehicleList" :key="v.id" :label="v.plateNumber + '-' + (v.brand || '') + (v.model || '')" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="美容项目">
          <div style="width: 100%;">
            <div v-for="(item, idx) in createForm.items" :key="idx" style="display: flex; gap: 8px; margin-bottom: 8px;">
              <el-select v-model="item.itemId" placeholder="选择项目" @change="onItemChange(item)" style="flex: 1;">
                <el-option v-for="bi in beautyItemList" :key="bi.id" :label="bi.itemName + '(¥' + bi.price + ')'" :value="bi.id" />
              </el-select>
              <el-input-number v-model="item.quantity" :min="1" style="width: 80px;" />
              <el-button size="small" type="danger" @click="removeItem(idx)" v-if="createForm.items.length > 1"><el-icon><Delete /></el-icon></el-button>
            </div>
            <el-button size="small" type="primary" plain @click="addItem">+ 添加项目</el-button>
          </div>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="createForm.appointmentTime" type="datetime" placeholder="选择时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="createForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateOrder">创建订单</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTypeMap[detailData.status]">{{ statusLabelMap[detailData.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ detailData.customerName }} ({{ detailData.customerPhone }})</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ detailData.plateNumber }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detailData.totalAmount ? Number(detailData.totalAmount).toFixed(2) : '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="应付金额">¥{{ detailData.payableAmount ? Number(detailData.payableAmount).toFixed(2) : '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ detailData.orderTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detailData.completeTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 16px;">项目明细</h4>
      <el-table :data="detailData.items || []" border size="small">
        <el-table-column prop="itemName" label="项目名称" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="subtotal" label="小计" width="110">
          <template #default="{ row }">¥{{ Number(row.subtotal).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <h4 v-if="detailData.payment" style="margin-top: 16px;">支付信息</h4>
      <el-descriptions v-if="detailData.payment" :column="2" border>
        <el-descriptions-item label="支付方式">{{ paymentMethodMap[detailData.payment.paymentMethod] }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ Number(detailData.payment.paidAmount).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detailData.payment.payTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 收款弹窗 -->
    <el-dialog v-model="payVisible" title="订单收款" width="450px">
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="应付金额"><span style="font-size: 18px; font-weight: bold;">¥{{ currentRow ? Number(currentRow.payableAmount || 0).toFixed(2) : '0.00' }}</span></el-form-item>
        <el-form-item label="折扣金额"><el-input-number v-model="payForm.discountAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="payForm.paymentMethod">
            <el-radio value="CASH">现金</el-radio>
            <el-radio value="WECHAT">微信</el-radio>
            <el-radio value="ALIPAY">支付宝</el-radio>
            <el-radio value="CARD">刷卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="payForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="success" @click="handlePay">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, createOrder, updateOrderStatus, payOrder, cancelOrder, getOrderDetail } from '../api/order'
import { getCustomerList } from '../api/customer'
import { getVehiclesByCustomer } from '../api/vehicle'
import { getAllItems } from '../api/beautyItem'

const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const search = reactive({ orderNo: '', status: '' })
const createVisible = ref(false)
const detailVisible = ref(false)
const payVisible = ref(false)
const currentRow = ref(null)
const detailData = ref({})
const customerList = ref([])
const vehicleList = ref([])
const beautyItemList = ref([])

const statusLabelMap = {
  PENDING: '待处理', IN_PROGRESS: '进行中', COMPLETED: '已完成', PAID: '已支付', CANCELLED: '已取消'
}
const statusTypeMap = {
  PENDING: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success', PAID: '', CANCELLED: 'danger'
}
const paymentMethodMap = { CASH: '现金', WECHAT: '微信支付', ALIPAY: '支付宝', CARD: '刷卡', OTHER: '其他' }

const createForm = reactive({
  customerId: null, vehicleId: null,
  items: [{ itemId: null, quantity: 1 }],
  appointmentTime: null, remark: ''
})

const payForm = reactive({ discountAmount: 0, paymentMethod: 'CASH', remark: '' })

const loadData = async () => {
  const res = await getOrderList({ page: page.value, pageSize: pageSize.value, ...search })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadBaseData = async () => {
  const [custRes, itemRes] = await Promise.all([
    getCustomerList({ page: 1, pageSize: 1000 }),
    getAllItems()
  ])
  customerList.value = custRes.data.records || []
  beautyItemList.value = itemRes.data || []
}

const openCreateDialog = () => {
  Object.assign(createForm, { customerId: null, vehicleId: null, items: [{ itemId: null, quantity: 1 }], appointmentTime: null, remark: '' })
  createVisible.value = true
}

const onCustomerChange = async (customerId) => {
  createForm.vehicleId = null
  if (customerId) {
    const res = await getVehiclesByCustomer(customerId)
    vehicleList.value = res.data || []
  }
}

const onItemChange = (item) => {
  const bi = beautyItemList.value.find(b => b.id === item.itemId)
  if (bi) item.itemName = bi.itemName
}

const addItem = () => {
  createForm.items.push({ itemId: null, quantity: 1 })
}

const removeItem = (idx) => {
  createForm.items.splice(idx, 1)
}

const handleCreateOrder = async () => {
  await createOrder(createForm)
  ElMessage.success('订单创建成功')
  createVisible.value = false
  loadData()
}

const openDetailDialog = async (row) => {
  const res = await getOrderDetail(row.id)
  detailData.value = res.data
  detailVisible.value = true
}

const handleStartWork = (row) => {
  ElMessageBox.confirm('确认开始施工？', '提示', { type: 'info' }).then(async () => {
    await updateOrderStatus(row.id, { status: 'IN_PROGRESS', remark: '开始施工' })
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

const openPayDialog = (row) => {
  currentRow.value = row
  Object.assign(payForm, { discountAmount: 0, paymentMethod: 'CASH', remark: '' })
  payVisible.value = true
}

const handlePay = async () => {
  payForm.discountAmount = payForm.discountAmount || 0
  await payOrder(currentRow.value.id, payForm)
  ElMessage.success('收款成功')
  payVisible.value = false
  loadData()
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' }).then(async () => {
    await cancelOrder(id, { reason: '手动取消' })
    ElMessage.success('订单已取消')
    loadData()
  }).catch(() => {})
}

onMounted(() => { loadData(); loadBaseData() })
</script>
