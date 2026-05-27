<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="header-bar">
          <span>套餐管理</span>
          <el-button type="primary" @click="showDialog()">+ 新增套餐</el-button>
        </div>
      </template>
      <el-table :data="combos" stripe border>
        <el-table-column prop="name" label="套餐名称" min-width="140" />
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }"><el-image v-if="row.image" :src="row.image" style="width:50px;height:50px" fit="cover" /><span v-else>-</span></template>
        </el-table-column>
        <el-table-column prop="price" label="售价" width="100"><template #default="{ row }"><span style="color:#FF5F3E;font-weight:bold">¥{{ row.price }}</span></template></el-table-column>
        <el-table-column prop="description" label="套餐描述" min-width="200" show-overflow-tooltip />
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑套餐' : '新增套餐'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="套餐名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="套餐描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="套餐售价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="套餐图片">
          <el-upload
            class="combo-uploader"
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
        <el-form-item label="包含菜品">
          <el-checkbox-group v-model="form.dishIds">
            <el-checkbox v-for="d in allDishes" :key="d.id" :label="d.id">{{ d.name }} (¥{{ d.currentPrice }})</el-checkbox>
          </el-checkbox-group>
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

const allDishes = ref([])
const combos = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', description: '', price: 0, image: '', dishIds: [] })
const getMerchantId = () => {
  try { return JSON.parse(localStorage.getItem('userInfo')).id } catch { return null }
}

const loadDishes = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const res = await request.get(`/dish/list/${merchantId}`)
  allDishes.value = res.data || []
}

const loadCombos = async () => {
  const merchantId = getMerchantId()
  if (!merchantId) return
  const res = await request.get(`/combo/list/${merchantId}`)
  combos.value = res.data || []
}

const showDialog = async (row) => {
  if (row) {
    isEdit.value = true
    const dishesRes = await request.get(`/combo/dishes/${row.id}`)
    const comboDishes = dishesRes.data || []
    form.value = { ...row, dishIds: comboDishes.map(d => d.dishId) }
  } else {
    isEdit.value = false
    form.value = { name: '', description: '', price: 0, image: '', dishIds: [] }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) { ElMessage.warning('请输入套餐名称'); return }
  if (!form.value.price || form.value.price <= 0) { ElMessage.warning('请设置套餐售价'); return }
  const dishes = form.value.dishIds.map(id => ({ dishId: id, quantity: 1 }))
  try {
    if (isEdit.value) {
      await request.put('/combo', { id: form.value.id, name: form.value.name, description: form.value.description, image: form.value.image, price: form.value.price, dishes })
      ElMessage.success('修改成功')
    } else {
      await request.post('/combo', { merchantId: getMerchantId(), name: form.value.name, description: form.value.description, image: form.value.image, price: form.value.price, dishes })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCombos()
  } catch (e) {
    const msg = e?.response?.data?.msg || e?.message || '保存失败，请检查输入'
    ElMessage.error(msg)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该套餐？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/combo/${row.id}`)
    ElMessage.success('删除成功')
    loadCombos()
  }).catch(() => {})
}

const toggleStatus = async (row) => {
  await request.put(`/combo/status/${row.id}`, null, { params: { status: row.status } })
  ElMessage.success(row.status === 1 ? '已启售' : '已停售')
}

onMounted(() => { loadDishes(); loadCombos() })
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
