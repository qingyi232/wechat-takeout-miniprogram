<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>公告活动管理</span>
          <el-button type="primary" @click="openAdd">+ 新增公告</el-button>
        </div>
      </template>
      <div style="margin-bottom:16px;display:flex;gap:12px">
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width:140px" @change="loadData">
          <el-option label="公告" :value="1" />
          <el-option label="活动" :value="2" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width:140px" @change="loadData">
          <el-option label="上线" :value="1" />
          <el-option label="下线" :value="0" />
        </el-select>
      </div>
      <el-table :data="tableData" stripe border>
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? '' : 'warning'" size="small">{{ row.type === 1 ? '公告' : '活动' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上线' : '下线' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '下线' : '上线' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入公告内容" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="请选择类型" style="width:100%">
            <el-option label="公告" :value="1" />
            <el-option label="活动" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上线" inactive-text="下线" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filterType = ref('')
const filterStatus = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ title: '', content: '', type: 1, status: 1 })

const loadData = async () => {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (filterType.value !== '' && filterType.value !== null) params.type = filterType.value
  if (filterStatus.value !== '' && filterStatus.value !== null) params.status = filterStatus.value
  const res = await request.get('/announcement/page', { params })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
}

const openAdd = () => {
  isEdit.value = false
  form.value = { title: '', content: '', type: 1, status: 1 }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.title) { ElMessage.warning('请输入标题'); return }
  if (!form.value.content) { ElMessage.warning('请输入内容'); return }
  if (isEdit.value) {
    await request.put('/announcement', form.value)
    ElMessage.success('修改成功')
  } else {
    await request.post('/announcement', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/announcement/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const toggleStatus = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上线' : '下线'
  ElMessageBox.confirm(`确定${action}该公告？`, '提示', { type: 'warning' }).then(async () => {
    await request.put(`/announcement/status/${row.id}?status=${newStatus}`)
    ElMessage.success(`${action}成功`)
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
</style>
