<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>订单管理</span>
          <div>
            <el-select v-model="queryStatus" placeholder="订单状态" clearable style="width:140px;margin-right:12px" @change="loadOrders">
              <el-option label="全部" :value="null" />
              <el-option label="待支付" :value="1" />
              <el-option label="待接单" :value="2" />
              <el-option label="配送中" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
              <el-option label="已退款" :value="6" />
            </el-select>
          </div>
        </div>
      </template>
      <el-table :data="orders" stripe border v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="200" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="addressDetail" label="收货地址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 2" type="primary" size="small" @click="changeStatus(row, 3, '接单')">接单</el-button>
            <el-button v-if="row.status === 3" type="success" size="small" @click="changeStatus(row, 4, '完成')">完成</el-button>
            <el-button v-if="row.status === 2 || row.status === 3" type="danger" size="small" @click="changeStatus(row, 6, '退款')">退款</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @current-change="loadOrders" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const orders = ref([])
const queryStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const userRole = computed(() => localStorage.getItem('userRole'))
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const getStatusType = (s) => ({ 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger', 6: 'danger' }[s] || '')
const getStatusText = (s) => ({ 1: '待支付', 2: '待接单', 3: '配送中', 4: '已完成', 5: '已取消', 6: '已退款' }[s] || '')

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (queryStatus.value !== null) params.status = queryStatus.value
    let res
    if (userRole.value === 'merchant') {
      params.merchantId = getMerchantId()
      res = await request.get('/order/merchant/list', { params })
    } else {
      res = await request.get('/order/admin/list', { params })
    }
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const changeStatus = (row, status, label) => {
  ElMessageBox.confirm(`确认${label}？`, '提示').then(async () => {
    await request.put(`/order/status/${row.id}`, null, { params: { status } })
    ElMessage.success(`已${label}`)
    loadOrders()
  }).catch(() => {})
}

onMounted(loadOrders)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
</style>
