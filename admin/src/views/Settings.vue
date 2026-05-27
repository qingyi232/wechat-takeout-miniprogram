<template>
  <div>
    <el-card shadow="hover">
      <template #header><span>运营设置</span></template>
      <el-form :model="settings" label-width="120px" style="max-width:600px">
        <el-form-item label="商家名称">
          <el-input v-model="settings.name" />
        </el-form-item>
        <el-form-item label="商家Logo">
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-image v-if="settings.logo" :src="settings.logo" style="width:100px;height:100px;cursor:pointer;border-radius:8px" fit="cover" />
            <div v-else class="upload-placeholder">
              <el-icon style="font-size:24px;color:#999"><Plus /></el-icon>
              <span style="font-size:12px;color:#999;margin-top:4px">上传Logo</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="营业时间">
          <el-time-picker v-model="startTime" placeholder="开始时间" format="HH:mm" style="width:45%" />
          <span style="margin:0 8px">至</span>
          <el-time-picker v-model="endTime" placeholder="结束时间" format="HH:mm" style="width:45%" />
        </el-form-item>
        <el-form-item label="配送费(元)">
          <el-input-number v-model="settings.deliveryFee" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="免配送费">
          <el-switch v-model="settings.freeDelivery" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="配送时间(分钟)">
          <el-input-number v-model="settings.deliveryTime" :min="10" :max="120" />
        </el-form-item>
        <el-form-item label="配送范围(公里)">
          <el-input-number v-model="settings.deliveryRange" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="商家地址">
          <el-input v-model="settings.address" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="settings.phone" />
        </el-form-item>
        <el-form-item label="商家描述">
          <el-input v-model="settings.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const uploadAction = '/api/file/upload'
const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + localStorage.getItem('token')
}))

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过5MB'); return false }
  return true
}

const handleLogoSuccess = (res) => {
  if (res.code === 200) {
    settings.value.logo = res.data
    ElMessage.success('Logo上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const settings = ref({
  id: null,
  name: '',
  logo: '',
  businessHours: '08:00-22:00',
  deliveryFee: 0,
  freeDelivery: 0,
  deliveryTime: 30,
  deliveryRange: 5,
  address: '',
  phone: '',
  description: ''
})
const startTime = ref(null)
const endTime = ref(null)

const loadSettings = async () => {
  const res = await request.get('/merchant/info')
  const m = res.data || {}
  settings.value.id = m.id
  settings.value.name = m.name || ''
  settings.value.logo = m.logo || ''
  settings.value.deliveryFee = m.deliveryFee || 0
  settings.value.freeDelivery = m.freeDelivery || 0
  settings.value.deliveryTime = m.deliveryTime || 30
  settings.value.deliveryRange = m.deliveryRange || 5
  settings.value.address = m.address || ''
  settings.value.phone = m.phone || ''
  settings.value.description = m.description || ''
  const hours = (m.businessHours || '08:00-22:00').split('-')
  const [sh, sm] = (hours[0] || '08:00').split(':').map(Number)
  const [eh, em] = (hours[1] || '22:00').split(':').map(Number)
  startTime.value = new Date(2026, 0, 1, sh, sm)
  endTime.value = new Date(2026, 0, 1, eh, em)
}

const handleSave = async () => {
  const st = startTime.value ? `${startTime.value.getHours().toString().padStart(2,'0')}:${startTime.value.getMinutes().toString().padStart(2,'0')}` : '08:00'
  const et = endTime.value ? `${endTime.value.getHours().toString().padStart(2,'0')}:${endTime.value.getMinutes().toString().padStart(2,'0')}` : '22:00'
  await request.put('/merchant/update', {
    id: settings.value.id,
    name: settings.value.name,
    logo: settings.value.logo,
    businessHours: `${st}-${et}`,
    deliveryFee: settings.value.deliveryFee,
    freeDelivery: settings.value.freeDelivery,
    deliveryTime: settings.value.deliveryTime,
    deliveryRange: settings.value.deliveryRange,
    address: settings.value.address,
    phone: settings.value.phone,
    description: settings.value.description
  })
  ElMessage.success('设置已保存')
}

onMounted(loadSettings)
</script>

<style scoped>
.upload-placeholder {
  width: 100px;
  height: 100px;
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
