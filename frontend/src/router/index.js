import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'beauty-items', name: 'BeautyItems', component: () => import('../views/BeautyItemManage.vue'), meta: { title: '美容项目管理' } },
      { path: 'customers', name: 'Customers', component: () => import('../views/CustomerManage.vue'), meta: { title: '客户管理' } },
      { path: 'vehicles', name: 'Vehicles', component: () => import('../views/VehicleManage.vue'), meta: { title: '车辆管理' } },
      { path: 'orders', name: 'Orders', component: () => import('../views/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/Statistics.vue'), meta: { title: '统计报表' } },
      { path: 'backup', name: 'Backup', component: () => import('../views/BackupManage.vue'), meta: { title: '数据备份' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
