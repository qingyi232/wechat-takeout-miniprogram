<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="onTabChange">
      <el-tab-pane label="入驻申请" name="apply">
        <el-card shadow="hover">
          <template #header>
            <div class="header-bar">
              <span>商家入驻申请</span>
              <el-select v-model="applyQueryStatus" placeholder="申请状态" clearable style="width:140px" @change="loadApplyList">
                <el-option label="全部" :value="null" />
                <el-option label="待审核" :value="0" />
                <el-option label="已通过" :value="1" />
                <el-option label="已拒绝" :value="2" />
              </el-select>
            </div>
          </template>
          <el-table :data="applyList" stripe border v-loading="applyLoading">
            <el-table-column prop="name" label="商家名称" min-width="120" />
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="categoryType" label="经营类型" width="110" />
            <el-table-column prop="address" label="地址" min-width="160" show-overflow-tooltip />
            <el-table-column prop="description" label="简介" min-width="140" show-overflow-tooltip />
            <el-table-column prop="businessHours" label="营业时间" width="120" />
            <el-table-column prop="createTime" label="申请时间" width="170" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <template v-if="row.status === 0">
                  <el-button type="success" size="small" @click="approveApply(row)">通过</el-button>
                  <el-button type="danger" size="small" @click="rejectApply(row)">拒绝</el-button>
                </template>
                <template v-if="row.status === 1">
                  <el-tooltip :content="'账号: ' + row.merchantAccount" placement="top">
                    <el-button type="primary" size="small" plain>查看账号</el-button>
                  </el-tooltip>
                </template>
                <template v-if="row.status === 2">
                  <el-tag type="info" size="small">{{ row.rejectReason || '不符合条件' }}</el-tag>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="applyPageNum" :page-size="applyPageSize" :total="applyTotal"
            layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @current-change="loadApplyList" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="商家管理" name="merchant">
        <el-card shadow="hover">
          <template #header>
            <div class="header-bar">
              <span>已入驻商家</span>
              <el-select v-model="queryStatus" placeholder="商家状态" clearable style="width:140px" @change="loadMerchants">
                <el-option label="全部" :value="null" />
                <el-option label="营业中" :value="1" />
                <el-option label="已关闭" :value="0" />
              </el-select>
            </div>
          </template>
          <el-table :data="merchants" stripe border v-loading="loading">
            <el-table-column prop="name" label="商家名称" min-width="120" />
            <el-table-column prop="account" label="登录账号" width="130" />
            <el-table-column prop="categoryType" label="经营类型" width="100" />
            <el-table-column prop="address" label="地址" min-width="160" show-overflow-tooltip />
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="rating" label="评分" width="80" />
            <el-table-column prop="monthlySales" label="月销量" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '营业中' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.status === 1" type="warning" size="small" @click="auditMerchant(row, 0)">停业</el-button>
                <el-button v-if="row.status === 0" type="primary" size="small" @click="auditMerchant(row, 1)">启用</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="pageNum" :page-size="pageSize" :total="total"
            layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @current-change="loadMerchants" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="rejectDialogVisible" title="拒绝申请" width="420px">
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejectLoading">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('apply')

const applyQueryStatus = ref(null)
const applyPageNum = ref(1)
const applyPageSize = ref(10)
const applyTotal = ref(0)
const applyList = ref([])
const applyLoading = ref(false)

const queryStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const merchants = ref([])
const loading = ref(false)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectLoading = ref(false)
let currentRejectRow = null

const onTabChange = () => {
  if (activeTab.value === 'apply') {
    loadApplyList()
  } else {
    loadMerchants()
  }
}

const loadApplyList = async () => {
  applyLoading.value = true
  try {
    const params = { pageNum: applyPageNum.value, pageSize: applyPageSize.value }
    if (applyQueryStatus.value !== null) params.status = applyQueryStatus.value
    const res = await request.get('/merchant-apply/admin/list', { params })
    applyList.value = res.data.records || []
    applyTotal.value = res.data.total || 0
  } finally {
    applyLoading.value = false
  }
}

const approveApply = (row) => {
  ElMessageBox.confirm(`确定通过「${row.name}」的入驻申请？通过后将自动生成商家账号`, '审批确认', {
    type: 'success',
    confirmButtonText: '通过',
    cancelButtonText: '取消'
  }).then(async () => {
    await request.put(`/merchant-apply/admin/audit/${row.id}`, null, { params: { status: 1 } })
    ElMessage.success('已通过，商家账号已生成')
    loadApplyList()
  }).catch(() => {})
}

const rejectApply = (row) => {
  currentRejectRow = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!currentRejectRow) return
  rejectLoading.value = true
  try {
    await request.put(`/merchant-apply/admin/audit/${currentRejectRow.id}`, null, {
      params: { status: 2, rejectReason: rejectReason.value || '不符合入驻条件' }
    })
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadApplyList()
  } finally {
    rejectLoading.value = false
  }
}

const loadMerchants = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (queryStatus.value !== null) params.status = queryStatus.value
    const res = await request.get('/merchant/page', { params })
    merchants.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const auditMerchant = (row, status) => {
  const action = status === 1 ? '启用' : '停业'
  ElMessageBox.confirm(`确定${action}「${row.name}」？`, '提示', { type: 'warning' }).then(async () => {
    await request.put(`/merchant/status/${row.id}`, null, { params: { status } })
    ElMessage.success('操作成功')
    loadMerchants()
  }).catch(() => {})
}

onMounted(() => {
  loadApplyList()
})
</script>

<style scoped>
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
