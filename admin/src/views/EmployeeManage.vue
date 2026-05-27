<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>员工管理</span>
          <el-button type="primary" @click="showDialog()">+ 新增员工</el-button>
        </div>
      </template>
      <el-table :data="employees" stripe border>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="username" label="登录账号" width="140" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'warning' : ''" size="small">{{ row.role === 'admin' ? '管理员' : '员工' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="showDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '新增员工'" width="460px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="登录账号"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="isEdit ? '不修改请留空' : '请输入密码'" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员" value="admin" />
            <el-option label="员工" value="staff" />
          </el-select>
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

const employees = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', username: '', password: '', phone: '', role: 'staff' })
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const loadEmployees = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const res = await request.get(`/employee/list/${merchantId}`)
  employees.value = res.data || []
}

const showDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.value = { ...row, password: '' }
  } else {
    isEdit.value = false
    form.value = { name: '', username: '', password: '', phone: '', role: 'staff', merchantId: getMerchantId() }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name || !form.value.username) { ElMessage.warning('请填写完整信息'); return }
  if (!isEdit.value && !form.value.password) { ElMessage.warning('请输入密码'); return }
  if (isEdit.value) {
    await request.put('/employee', form.value)
    ElMessage.success('修改成功')
  } else {
    form.value.merchantId = getMerchantId()
    await request.post('/employee', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadEmployees()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该员工？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/employee/${row.id}`)
    ElMessage.success('删除成功')
    loadEmployees()
  }).catch(() => {})
}

const toggleStatus = async (row) => {
  await request.put(`/employee/status/${row.id}`, null, { params: { status: row.status } })
  ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
}

onMounted(loadEmployees)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
</style>
