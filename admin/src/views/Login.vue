<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">食刻外卖管理后台</h2>
      <el-tabs v-model="loginType" class="login-tabs">
        <el-tab-pane label="商家登录" name="merchant" />
        <el-tab-pane label="管理员登录" name="admin" />
      </el-tabs>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="account">
          <el-input v-model="form.account" :prefix-icon="User" :placeholder="loginType === 'merchant' ? '商家账号' : '管理员账号'" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :prefix-icon="Lock" placeholder="登录密码" type="password" show-password size="large" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" size="large" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tips">
        <p>商家测试账号: merchant1 / 123456</p>
        <p>管理员账号: admin / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const loginType = ref('merchant')
const form = ref({ account: '', password: '' })
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const url = loginType.value === 'merchant' ? '/merchant/login' : '/admin/login'
    const params = loginType.value === 'merchant'
      ? { account: form.value.account, password: form.value.password }
      : { username: form.value.account, password: form.value.password }
    const res = await request.post(url, params)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userRole', loginType.value)
    localStorage.setItem('userInfo', JSON.stringify(res.data[loginType.value] || res.data.admin))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 50%, #E0F2F1 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 12px 40px rgba(42, 174, 103, 0.12);
  border: 1px solid rgba(42, 174, 103, 0.08);
}
.login-title {
  text-align: center;
  font-size: 24px;
  color: #2D3436;
  margin-bottom: 24px;
}
.login-tabs { margin-bottom: 16px; }
.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #2AAE67 0%, #20C374 100%) !important;
  border: none !important;
  color: #fff !important;
  border-radius: 10px;
}
.login-tips {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 16px;
}
.login-tips p { margin: 4px 0; }
</style>
