<template>
  <div class="data-monitor">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ platformStats.merchantCount }}</div>
          <div class="stat-label">入驻商家数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ platformStats.totalOrders }}</div>
          <div class="stat-label">平台总订单</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">¥{{ platformStats.totalAmount }}</div>
          <div class="stat-label">平台总营业额</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>各商家订单量占比</span></template>
          <div ref="pieChartRef" style="height:350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>近7日订单趋势</span></template>
          <div ref="lineChartRef" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top:20px">
      <template #header>
        <div class="header-bar">
          <span>商家运营数据</span>
          <el-button type="primary" @click="exportData">导出报表</el-button>
        </div>
      </template>
      <el-table :data="merchantData" stripe border>
        <el-table-column prop="name" label="商家名称" />
        <el-table-column prop="orderCount" label="订单总量" width="100" />
        <el-table-column prop="totalAmount" label="营业额" width="120"><template #default="{ row }">¥{{ row.totalAmount }}</template></el-table-column>
        <el-table-column prop="avgRating" label="平均评分" width="100" />
        <el-table-column prop="monthlySales" label="月销量" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const pieChartRef = ref(null)
const lineChartRef = ref(null)
const platformStats = ref({ merchantCount: 0, totalOrders: 0, totalAmount: 0 })
const merchantData = ref([])

const loadData = async () => {
  const statsRes = await request.get('/admin/stats')
  platformStats.value.merchantCount = statsRes.data.merchantCount || 0

  const merchantRes = await request.get('/merchant/page', { params: { pageNum: 1, pageSize: 100 } })
  const merchants = merchantRes.data.records || []
  const dataList = []
  let totalAmt = 0
  let totalOrd = 0
  for (const m of merchants) {
    const mStats = await request.get('/order/merchant/stats', { params: { merchantId: m.id } })
    const mData = mStats.data || {}
    const amt = Number(mData.totalAmount) || 0
    const ord = Number(mData.totalOrders) || 0
    totalAmt += amt
    totalOrd += ord
    dataList.push({
      name: m.name,
      orderCount: ord,
      totalAmount: amt.toFixed(2),
      avgRating: m.rating || 0,
      monthlySales: m.monthlySales || 0
    })
  }
  merchantData.value = dataList
  platformStats.value.totalAmount = totalAmt.toFixed(0)
  platformStats.value.totalOrders = totalOrd

  await nextTick()
  if (pieChartRef.value) {
    const pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: dataList.map(m => ({ name: m.name, value: m.orderCount })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } },
        itemStyle: { borderRadius: 6 }
      }]
    })
    window.addEventListener('resize', () => pieChart.resize())
  }

  if (lineChartRef.value) {
    const now = new Date()
    const dates = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now); d.setDate(d.getDate() - i)
      dates.push((d.getMonth() + 1).toString().padStart(2, '0') + '-' + d.getDate().toString().padStart(2, '0'))
    }
    const ordersRes = await request.get('/order/admin/list', { params: { pageNum: 1, pageSize: 1000 } })
    const allOrders = ordersRes.data.records || []
    const countByDate = {}
    dates.forEach(d => { countByDate[d] = 0 })
    allOrders.forEach(o => {
      if (o.createTime) {
        const dateStr = o.createTime.slice(5, 10)
        if (countByDate[dateStr] !== undefined) countByDate[dateStr]++
      }
    })
    const lineChart = echarts.init(lineChartRef.value)
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        type: 'line', data: dates.map(d => countByDate[d] || 0),
        smooth: true, areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#2AAE67' }
      }]
    })
    window.addEventListener('resize', () => lineChart.resize())
  }
}

const exportData = () => {
  if (merchantData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  const headers = ['商家名称', '订单总量', '营业额', '平均评分', '月销量']
  const rows = merchantData.value.map(m => [m.name, m.orderCount, m.totalAmount, m.avgRating, m.monthlySales])
  const csvContent = '\uFEFF' + [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `商家运营报表_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success('报表导出成功')
}

onMounted(loadData)
</script>

<style scoped>
.stat-card { text-align: center; padding: 20px 0; }
.stat-value { font-size: 36px; font-weight: bold; color: #2AAE67; }
.stat-label { font-size: 14px; color: #999; margin-top: 8px; }
.header-bar { display: flex; justify-content: space-between; align-items: center; }
</style>
