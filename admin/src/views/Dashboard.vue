<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#2AAE67"><el-icon><List /></el-icon></div>
          <div class="stat-info"><div class="stat-value">{{ stats.totalOrders || 0 }}</div><div class="stat-label">{{ isAdmin ? '平台总订单' : '总订单数' }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#52C41A"><el-icon><Money /></el-icon></div>
          <div class="stat-info"><div class="stat-value">¥{{ stats.totalAmount || 0 }}</div><div class="stat-label">{{ isAdmin ? '平台总营业额' : '总营业额' }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#1890FF"><el-icon><Timer /></el-icon></div>
          <div class="stat-info"><div class="stat-value">{{ stats.pendingOrders || 0 }}</div><div class="stat-label">待处理订单</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#FF5F3E"><el-icon><Dish /></el-icon></div>
          <div class="stat-info"><div class="stat-value">{{ stats.dishCount || 0 }}</div><div class="stat-label">{{ isAdmin ? '入驻商家数' : '在售菜品' }}</div></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span>近7日营业数据</span></template>
          <div ref="chartRef" style="height:350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>最新订单</span></template>
          <div v-if="recentOrders.length === 0" class="empty-text">暂无订单</div>
          <div v-for="order in recentOrders" :key="order.id" class="order-item">
            <div class="order-no">{{ order.orderNo }}</div>
            <div class="order-amount">¥{{ order.payAmount }}</div>
            <el-tag :type="getStatusType(order.status)" size="small">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'

const chartRef = ref(null)
const stats = ref({ totalOrders: 0, totalAmount: 0, pendingOrders: 0, dishCount: 0 })
const recentOrders = ref([])
const userRole = computed(() => localStorage.getItem('userRole'))
const isAdmin = computed(() => userRole.value === 'admin')
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const getStatusType = (s) => ({ 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger', 6: 'danger' }[s] || '')
const getStatusText = (s) => ({ 1: '待支付', 2: '待接单', 3: '配送中', 4: '已完成', 5: '已取消', 6: '已退款' }[s] || '')

const loadStats = async () => {
  const merchantId = getMerchantId()
  if (userRole.value === 'merchant' && merchantId) {
    const statsRes = await request.get('/order/merchant/stats', { params: { merchantId } })
    stats.value.totalOrders = statsRes.data.totalOrders || 0
    stats.value.totalAmount = statsRes.data.totalAmount || 0
    const pendingRes = await request.get('/order/merchant/list', { params: { merchantId, pageNum: 1, pageSize: 1, status: 2 } })
    stats.value.pendingOrders = pendingRes.data.total || 0
    const dishRes = await request.get('/dish/page', { params: { merchantId, pageNum: 1, pageSize: 1 } })
    stats.value.dishCount = dishRes.data.total || 0
    const ordersRes = await request.get('/order/merchant/list', { params: { merchantId, pageNum: 1, pageSize: 5 } })
    recentOrders.value = ordersRes.data.records || []
  } else {
    const adminStatsRes = await request.get('/admin/stats')
    stats.value.dishCount = adminStatsRes.data.merchantCount || 0
    const ordersRes = await request.get('/order/admin/list', { params: { pageNum: 1, pageSize: 1000 } })
    const allOrders = ordersRes.data.records || []
    stats.value.totalOrders = 0
    let totalAmt = 0
    let pending = 0
    allOrders.forEach(o => {
      // 只统计有效订单(待接单2、配送中3、已完成4)，与数据监控保持一致
      if ([2, 3, 4].includes(o.status)) {
        stats.value.totalOrders++
        totalAmt += Number(o.payAmount) || 0
      }
      if (o.status === 2) pending++
    })
    stats.value.totalAmount = totalAmt.toFixed(0)
    stats.value.pendingOrders = pending
    recentOrders.value = allOrders.slice(0, 5)
  }
}

const loadChart = async () => {
  const merchantId = getMerchantId()
  let dates = [], counts = [], amounts = []
  const now = new Date()
  for (let i = 6; i >= 0; i--) {
    const d = new Date(now); d.setDate(d.getDate() - i)
    dates.push((d.getMonth() + 1).toString().padStart(2, '0') + '-' + d.getDate().toString().padStart(2, '0'))
  }
  counts = new Array(7).fill(0)
  amounts = new Array(7).fill(0)

  if (userRole.value === 'merchant' && merchantId) {
    try {
      const res = await request.get('/order/merchant/recent', { params: { merchantId } })
      const data = res.data || []
      data.forEach(d => {
        const dateStr = String(d.date).slice(5)
        const idx = dates.indexOf(dateStr)
        if (idx !== -1) {
          counts[idx] = d.count || 0
          amounts[idx] = d.amount || 0
        }
      })
    } catch (e) {}
  } else {
    try {
      const ordersRes = await request.get('/order/admin/list', { params: { pageNum: 1, pageSize: 1000 } })
      const allOrders = ordersRes.data.records || []
      allOrders.forEach(o => {
        if (o.createTime) {
          const dateStr = o.createTime.slice(5, 10)
          const idx = dates.indexOf(dateStr)
          if (idx !== -1) {
            counts[idx]++
            amounts[idx] += Number(o.payAmount) || 0
          }
        }
      })
      amounts = amounts.map(a => Math.round(a * 100) / 100)
    } catch (e) {}
  }

  await nextTick()
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单量', '营业额'] },
    xAxis: { type: 'category', data: dates },
    yAxis: [{ type: 'value', name: '订单量', minInterval: 1 }, { type: 'value', name: '营业额(元)' }],
    series: [
      { name: '订单量', type: 'bar', data: counts, itemStyle: { color: '#2AAE67' } },
      { name: '营业额', type: 'line', yAxisIndex: 1, data: amounts, itemStyle: { color: '#52C41A' }, smooth: true }
    ]
  })
  window.addEventListener('resize', () => chart.resize())
}

onMounted(async () => {
  await loadStats()
  await loadChart()
})
</script>

<style scoped>
.stat-card { cursor: pointer; }
.stat-card .el-card__body { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24px; }
.stat-value { font-size: 28px; font-weight: bold; color: #333; }
.stat-label { font-size: 14px; color: #999; margin-top: 4px; }
.order-item { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.order-no { font-size: 13px; color: #666; }
.order-amount { font-size: 14px; font-weight: bold; color: #FF5F3E; }
.empty-text { text-align: center; color: #999; padding: 40px 0; }
</style>
