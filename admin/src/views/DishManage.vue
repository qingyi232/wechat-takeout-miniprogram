<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>菜品管理</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索菜品" clearable style="width:200px;margin-right:12px" @clear="loadDishes" @keyup.enter="loadDishes" />
            <el-button type="primary" @click="showDialog()">+ 新增菜品</el-button>
          </div>
        </div>
      </template>
      <el-table :data="dishes" stripe border>
        <el-table-column prop="name" label="菜品名称" min-width="140" />
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }"><el-image :src="row.image" style="width:50px;height:50px" fit="cover" /></template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="80"><template #default="{ row }">¥{{ row.originalPrice }}</template></el-table-column>
        <el-table-column prop="currentPrice" label="现价" width="80"><template #default="{ row }"><span style="color:#FF5F3E;font-weight:bold">¥{{ row.currentPrice }}</span></template></el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="showDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @current-change="loadDishes" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '新增菜品'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="菜品描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="所属分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="折扣"><el-input-number v-model="form.discount" :min="0.1" :max="1" :step="0.05" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="菜品图片">
          <el-upload
            class="dish-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-image v-if="form.image" :src="form.image" style="width:120px;height:120px;cursor:pointer" fit="cover" />
            <div v-else class="upload-placeholder">
              <el-icon style="font-size:28px;color:#999"><Plus /></el-icon>
              <span style="font-size:12px;color:#999;margin-top:4px">点击上传</span>
            </div>
          </el-upload>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + localStorage.getItem('token')
}))

const uploadAction = '/api/file/upload'

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过5MB'); return false }
  return true
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败，请检查网络或登录状态')
}

const keyword = ref('')
const categories = ref([])
const dishes = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', description: '', categoryId: null, originalPrice: 0, discount: 0.9, stock: 0, image: '' })
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const loadCategories = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const res = await request.get(`/category/list/${merchantId}`)
  categories.value = res.data || []
}

const loadDishes = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const params = { merchantId, pageNum: pageNum.value, pageSize: pageSize.value }
  if (keyword.value) params.keyword = keyword.value
  const res = await request.get('/dish/page', { params })
  dishes.value = res.data.records || []
  total.value = res.data.total || 0
}

const showDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.value = { ...row }
  } else {
    isEdit.value = false
    form.value = { name: '', description: '', categoryId: null, originalPrice: 0, discount: 0.9, stock: 0, image: '', merchantId: getMerchantId() }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) { ElMessage.warning('请输入菜品名称'); return }
  if (!form.value.originalPrice || form.value.originalPrice <= 0) { ElMessage.warning('请设置菜品原价'); return }
  form.value.currentPrice = Math.round(form.value.originalPrice * form.value.discount * 100) / 100
  try {
    if (isEdit.value) {
      await request.put('/dish', form.value)
      ElMessage.success('修改成功')
    } else {
      form.value.merchantId = getMerchantId()
      form.value.status = 1
      form.value.sales = 0
      await request.post('/dish', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadDishes()
  } catch (e) {
    const msg = e?.response?.data?.msg || e?.message || '保存失败，请检查输入'
    ElMessage.error(msg)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该菜品？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/dish/${row.id}`)
    ElMessage.success('删除成功')
    loadDishes()
  }).catch(() => {})
}

const toggleStatus = async (row) => {
  await request.put(`/dish/status/${row.id}`, null, { params: { status: row.status } })
  ElMessage.success(row.status === 1 ? '已启售' : '已停售')
}

const getCategoryName = (id) => {
  const cat = categories.value.find(c => c.id === id)
  return cat ? cat.name : '-'
}

onMounted(() => { loadCategories(); loadDishes() })
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}
.upload-placeholder:hover {
  border-color: #2AAE67;
}
</style>
