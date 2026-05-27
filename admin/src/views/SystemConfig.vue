<template>
  <div>
    <el-card shadow="hover">
      <template #header><span>系统配置维护</span></template>
      <el-table :data="configs" stripe border>
        <el-table-column prop="configKey" label="配置项" width="200" />
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column prop="configValue" label="配置值" width="200">
          <template #default="{ row }">
            <el-input v-if="row.editing" v-model="row.configValue" size="small" @keyup.enter="saveConfig(row)" />
            <span v-else>{{ row.configValue }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="!row.editing" type="primary" size="small" link @click="row.editing = true">编辑</el-button>
            <template v-else>
              <el-button type="success" size="small" link @click="saveConfig(row)">保存</el-button>
              <el-button type="info" size="small" link @click="row.editing = false">取消</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const configs = ref([])

const loadConfigs = async () => {
  const res = await request.get('/admin/config/list')
  configs.value = (res.data || []).map(c => ({ ...c, editing: false }))
}

const saveConfig = async (row) => {
  await request.put('/admin/config', { id: row.id, configKey: row.configKey, configValue: row.configValue, description: row.description })
  row.editing = false
  ElMessage.success('配置已更新')
}

onMounted(loadConfigs)
</script>
