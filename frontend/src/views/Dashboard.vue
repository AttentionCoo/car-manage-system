<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="color: #999; font-size: 14px;">{{ item.title }}</div>
              <div style="font-size: 28px; font-weight: bold; color: #333; margin-top: 8px;">{{ item.value }}</div>
            </div>
            <el-icon :size="48" :color="item.color"><component :is="item.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card>
      <template #header><span>今日待处理订单</span></template>
      <el-table :data="pendingOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="payableAmount" label="金额" width="100">
          <template #default="{ row }">{{ row.payableAmount ? row.payableAmount.toFixed(2) : '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getTodayPending, getOrderOverview } from '../api/order'

const pendingOrders = ref([])
const overview = reactive({})
const statusMap = {
  PENDING: { label: '待处理', type: 'info' },
  IN_PROGRESS: { label: '进行中', type: 'warning' },
  COMPLETED: { label: '已完成', type: 'success' },
  PAID: { label: '已支付', type: '' },
  CANCELLED: { label: '已取消', type: 'danger' }
}

const statCards = ref([
  { title: '今日订单', value: 0, icon: 'List', color: '#409EFF' },
  { title: '待处理', value: 0, icon: 'Clock', color: '#E6A23C' },
  { title: '今日收入', value: '0.00', icon: 'Money', color: '#67C23A' },
  { title: '本月收入', value: '0.00', icon: 'TrendCharts', color: '#F56C6C' }
])

onMounted(async () => {
  try {
    const res = await getOrderOverview()
    if (res.data) {
      overview.value = res.data
      statCards.value[0].value = res.data.todayOrders || 0
      statCards.value[1].value = res.data.pendingOrders || 0
      statCards.value[2].value = res.data.todayRevenue ? Number(res.data.todayRevenue).toFixed(2) : '0.00'
      statCards.value[3].value = res.data.monthRevenue ? Number(res.data.monthRevenue).toFixed(2) : '0.00'
    }
  } catch (e) {}
  try {
    const res = await getTodayPending()
    pendingOrders.value = res.data || []
  } catch (e) {}
})
</script>
