import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'orders', name: 'Orders', component: () => import('../views/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'categories', name: 'Categories', component: () => import('../views/CategoryManage.vue'), meta: { title: '类目管理' } },
      { path: 'dishes', name: 'Dishes', component: () => import('../views/DishManage.vue'), meta: { title: '菜品管理' } },
      { path: 'combos', name: 'Combos', component: () => import('../views/ComboManage.vue'), meta: { title: '套餐管理' } },
      { path: 'employees', name: 'Employees', component: () => import('../views/EmployeeManage.vue'), meta: { title: '员工管理' } },
      { path: 'settings', name: 'Settings', component: () => import('../views/Settings.vue'), meta: { title: '运营设置' } },
      { path: 'merchant-audit', name: 'MerchantAudit', component: () => import('../views/MerchantAudit.vue'), meta: { title: '商家审核', admin: true } },
      { path: 'announcements', name: 'Announcements', component: () => import('../views/AnnouncementManage.vue'), meta: { title: '公告管理', admin: true } },
      { path: 'system-config', name: 'SystemConfig', component: () => import('../views/SystemConfig.vue'), meta: { title: '系统配置', admin: true } },
      { path: 'data-monitor', name: 'DataMonitor', component: () => import('../views/DataMonitor.vue'), meta: { title: '数据监控', admin: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
