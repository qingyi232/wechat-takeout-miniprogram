<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>类目管理</span>
          <el-button type="primary" @click="showDialog()">+ 新增分类</el-button>
        </div>
      </template>
      <el-table :data="categories" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="showDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称"><el-input v-model="form.name" placeholder="请输入分类名称" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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

const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', sort: 0, status: 1 })
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const loadCategories = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const res = await request.get(`/category/list/${merchantId}`)
  categories.value = res.data || []
}

const showDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.value = { ...row }
  } else {
    isEdit.value = false
    form.value = { name: '', sort: 0, status: 1, merchantId: getMerchantId() }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) { ElMessage.warning('请输入分类名称'); return }
  if (isEdit.value) {
    await request.put('/category', form.value)
    ElMessage.success('修改成功')
  } else {
    form.value.merchantId = getMerchantId()
    await request.post('/category', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadCategories()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/category/${row.id}`)
    ElMessage.success('删除成功')
    loadCategories()
  }).catch(() => {})
}

onMounted(loadCategories)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
</style>
