<template>
  <div>
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 月度项目统计 -->
      <el-tab-pane label="月度项目统计" name="item">
        <el-form :inline="true" style="margin-bottom: 16px;">
          <el-form-item label="年份"><el-input-number v-model="itemQuery.year" :min="2020" :max="2030" /></el-form-item>
          <el-form-item label="月份"><el-input-number v-model="itemQuery.month" :min="1" :max="12" /></el-form-item>
          <el-button type="primary" @click="loadItemStats">查询</el-button>
        </el-form>
        <el-table :data="itemStats" border stripe>
          <el-table-column prop="projectName" label="项目名称" />
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="serviceCount" label="服务次数" width="100" />
          <el-table-column prop="totalQuantity" label="总数量" width="80" />
          <el-table-column prop="totalRevenue" label="总收入" width="120">
            <template #default="{ row }">¥{{ Number(row.totalRevenue).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        <el-descriptions :column="3" border style="margin-top: 16px;" v-if="itemSummary.totalServices">
          <el-descriptions-item label="统计周期">{{ itemSummary.period }}</el-descriptions-item>
          <el-descriptions-item label="总服务次数">{{ itemSummary.totalServices }}</el-descriptions-item>
          <el-descriptions-item label="总收入">¥{{ Number(itemSummary.totalRevenue).toFixed(2) }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 年度客户统计 -->
      <el-tab-pane label="年度客户统计" name="customer">
        <el-form :inline="true" style="margin-bottom: 16px;">
          <el-form-item label="年份"><el-input-number v-model="customerQuery.year" :min="2020" :max="2030" /></el-form-item>
          <el-button type="primary" @click="loadCustomerStats">查询</el-button>
        </el-form>
        <el-table :data="customerStats" border stripe>
          <el-table-column prop="customerName" label="客户姓名" width="120" />
          <el-table-column prop="gender" label="性别" width="60" />
          <el-table-column prop="customerPhone" label="手机号" width="130" />
          <el-table-column prop="totalOrders" label="订单数" width="80" />
          <el-table-column prop="totalServiceCount" label="服务次数" width="90" />
          <el-table-column prop="totalSpent" label="总消费" width="120">
            <template #default="{ row }">¥{{ Number(row.totalSpent || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="lastVisitDate" label="最近到店" width="170" />
        </el-table>
        <el-descriptions :column="3" border style="margin-top: 16px;" v-if="customerSummary.totalCustomers">
          <el-descriptions-item label="年份">{{ customerSummary.year }}</el-descriptions-item>
          <el-descriptions-item label="客户总数">{{ customerSummary.totalCustomers }}</el-descriptions-item>
          <el-descriptions-item label="活跃客户">{{ customerSummary.activeCustomers }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 月度收入统计 -->
      <el-tab-pane label="月度收入统计" name="revenue">
        <el-form :inline="true" style="margin-bottom: 16px;">
          <el-form-item label="年份"><el-input-number v-model="revenueQuery.year" :min="2020" :max="2030" /></el-form-item>
          <el-form-item label="月份"><el-input-number v-model="revenueQuery.month" :min="1" :max="12" /></el-form-item>
          <el-button type="primary" @click="loadRevenueStats">查询</el-button>
        </el-form>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>收入概览</template>
              <el-descriptions :column="2" border v-if="revenueOverview.period">
                <el-descriptions-item label="统计周期">{{ revenueOverview.period }}</el-descriptions-item>
                <el-descriptions-item label="总订单数">{{ revenueOverview.totalOrders }}</el-descriptions-item>
                <el-descriptions-item label="已完成">{{ revenueOverview.completedOrders }}</el-descriptions-item>
                <el-descriptions-item label="已取消">{{ revenueOverview.cancelledOrders }}</el-descriptions-item>
                <el-descriptions-item label="总收入">¥{{ Number(revenueOverview.grossRevenue || 0).toFixed(2) }}</el-descriptions-item>
                <el-descriptions-item label="总折扣">¥{{ Number(revenueOverview.totalDiscount || 0).toFixed(2) }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>项目收入占比</template>
              <el-table :data="itemRevenueDist" border size="small">
                <el-table-column prop="itemName" label="项目" />
                <el-table-column prop="orderCount" label="订单数" width="80" />
                <el-table-column prop="itemRevenue" label="收入" width="100">
                  <template #default="{ row }">¥{{ Number(row.itemRevenue || 0).toFixed(2) }}</template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="hover" style="margin-top: 20px;">
          <template #header>日收入趋势</template>
          <el-table :data="dailyTrend" border size="small">
            <el-table-column prop="day" label="日期(号)" width="100" />
            <el-table-column prop="dailyOrders" label="订单数" width="100" />
            <el-table-column prop="dailyRevenue" label="收入">
              <template #default="{ row }">¥{{ Number(row.dailyRevenue || 0).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMonthlyItemCount, getYearlyCustomerCount, getMonthlyRevenue } from '../api/statistics'

const activeTab = ref('item')

const itemQuery = reactive({ year: new Date().getFullYear(), month: new Date().getMonth() + 1 })
const itemStats = ref([])
const itemSummary = reactive({})

const customerQuery = reactive({ year: new Date().getFullYear() })
const customerStats = ref([])
const customerSummary = reactive({})

const revenueQuery = reactive({ year: new Date().getFullYear(), month: new Date().getMonth() + 1 })
const revenueOverview = reactive({})
const dailyTrend = ref([])
const itemRevenueDist = ref([])

const loadItemStats = async () => {
  const res = await getMonthlyItemCount(itemQuery.year, itemQuery.month)
  itemStats.value = res.data?.statistics || []
  Object.assign(itemSummary, res.data?.summary || {})
}

const loadCustomerStats = async () => {
  const res = await getYearlyCustomerCount(customerQuery.year)
  customerStats.value = res.data?.statistics || []
  Object.assign(customerSummary, res.data?.summary || {})
}

const loadRevenueStats = async () => {
  const res = await getMonthlyRevenue(revenueQuery.year, revenueQuery.month)
  Object.assign(revenueOverview, res.data?.overview || {})
  dailyTrend.value = res.data?.dailyTrend || []
  itemRevenueDist.value = res.data?.itemRevenueDistribution || []
}

onMounted(() => { loadItemStats(); loadCustomerStats(); loadRevenueStats() })
</script>
