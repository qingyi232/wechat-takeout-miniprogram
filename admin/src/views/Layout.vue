<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <span v-if="!isCollapse">食刻外卖</span>
        <span v-else>食</span>
      </div>
      <el-menu :default-active="$route.path" router :collapse="isCollapse" background-color="#1B2A3D" text-color="#ffffffa6" active-text-color="#2AAE67">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
          <el-badge v-if="newOrderCount > 0" :value="newOrderCount" class="order-badge" />
        </el-menu-item>
        <template v-if="userRole === 'merchant'">
          <el-menu-item index="/categories">
            <el-icon><Menu /></el-icon>
            <span>类目管理</span>
          </el-menu-item>
          <el-menu-item index="/dishes">
            <el-icon><Dish /></el-icon>
            <span>菜品管理</span>
          </el-menu-item>
          <el-menu-item index="/combos">
            <el-icon><TakeawayBox /></el-icon>
            <span>套餐管理</span>
          </el-menu-item>
          <el-menu-item index="/employees">
            <el-icon><User /></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/settings">
            <el-icon><Setting /></el-icon>
            <span>运营设置</span>
          </el-menu-item>
        </template>
        <template v-if="userRole === 'admin'">
          <el-menu-item index="/merchant-audit">
            <el-icon><Shop /></el-icon>
            <span>商家审核</span>
          </el-menu-item>
          <el-menu-item index="/announcements">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/system-config">
            <el-icon><Setting /></el-icon>
            <span>系统配置</span>
          </el-menu-item>
          <el-menu-item index="/data-monitor">
            <el-icon><TrendCharts /></el-icon>
            <span>数据监控</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
          <Fold v-if="!isCollapse" /><Expand v-else />
        </el-icon>
        <div class="header-right">
          <div v-if="wsConnected" class="ws-status connected">实时连接中</div>
          <div v-else-if="userRole === 'merchant'" class="ws-status disconnected">连接断开</div>
          <span class="user-name">{{ userInfo?.name || userInfo?.account || '管理员' }}</span>
          <el-button type="primary" link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>

    <!-- 新订单提醒弹窗 -->
    <el-dialog v-model="orderDialogVisible" title="新订单提醒" width="420px" :close-on-click-modal="false" class="order-notify-dialog">
      <div class="notify-content">
        <div class="notify-icon">📋</div>
        <div class="notify-text">
          <p class="notify-title">您有新的外卖订单！</p>
          <p class="notify-order">订单号：{{ latestOrder.orderNo }}</p>
          <p class="notify-amount">支付金额：<span class="amount">¥{{ latestOrder.payAmount }}</span></p>
        </div>
      </div>
      <template #footer>
        <el-button @click="orderDialogVisible = false">稍后处理</el-button>
        <el-button type="primary" @click="goToOrder">立即查看</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElNotification } from 'element-plus'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const userRole = computed(() => localStorage.getItem('userRole') || 'merchant')
const userInfo = computed(() => {
  try { return JSON.parse(localStorage.getItem('userInfo')) } catch { return {} }
})

let ws = null
let reconnectTimer = null
const wsConnected = ref(false)
const newOrderCount = ref(0)
const orderDialogVisible = ref(false)
const latestOrder = ref({ orderNo: '', payAmount: '' })
let notifyAudio = null

const initAudio = () => {
  try {
    notifyAudio = new Audio('data:audio/wav;base64,UklGRlQFAABXQVZFZm10IBAAAAABAAEAESsAABErAAABAAgAZGF0YTAFAACAgICAgICAgICBgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/v///////v79/Pv6+fj39vX08/Lx8O/u7ezr6uno5+bl5OPi4eDf3t3c29rZ2NfW1dTT0tHQz87NzMvKycjHxsXEw8LBwL++vby7urm4t7a1tLOysbCvrq2sq6qpqKempaSjoqGgn56dnJuamZiXlpWUk5KRkI+OjYyLiomIh4aFhIOCgYCAgA==')
  } catch (e) { /* audio not critical */ }
}

const connectWebSocket = () => {
  if (userRole.value !== 'merchant') return
  const merchantId = userInfo.value?.id
  if (!merchantId) return

  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${location.host}/ws/order/${merchantId}`

  try {
    ws = new WebSocket(wsUrl)

    ws.onopen = () => {
      wsConnected.value = true
      console.log('[WebSocket] 连接成功，商家ID:', merchantId)
    }

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        if (data.type === 'new_order') {
          newOrderCount.value++
          latestOrder.value = {
            orderNo: data.orderNo || '',
            payAmount: data.payAmount || ''
          }
          orderDialogVisible.value = true
          if (notifyAudio) {
            notifyAudio.play().catch(() => {})
          }
          ElNotification({
            title: '新订单提醒',
            message: `订单 ${data.orderNo}，金额 ¥${data.payAmount}`,
            type: 'success',
            duration: 6000
          })
        }
      } catch (e) {
        console.error('[WebSocket] 消息解析失败:', e)
      }
    }

    ws.onclose = () => {
      wsConnected.value = false
      console.log('[WebSocket] 连接关闭，5秒后重连...')
      scheduleReconnect()
    }

    ws.onerror = () => {
      wsConnected.value = false
    }
  } catch (e) {
    console.error('[WebSocket] 连接失败:', e)
    scheduleReconnect()
  }
}

const scheduleReconnect = () => {
  if (reconnectTimer) clearTimeout(reconnectTimer)
  reconnectTimer = setTimeout(() => {
    if (userRole.value === 'merchant') {
      connectWebSocket()
    }
  }, 5000)
}

const goToOrder = () => {
  orderDialogVisible.value = false
  newOrderCount.value = 0
  router.push('/orders')
}

watch(() => route.path, (path) => {
  if (path === '/orders') {
    newOrderCount.value = 0
  }
})

onMounted(() => {
  initAudio()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) { ws.close(); ws = null }
  if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null }
})

const handleLogout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
    if (ws) { ws.close(); ws = null }
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container { min-height: 100vh; }
.layout-aside {
  background-color: #1B2A3D;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.el-menu { border-right: none; }
.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 0 20px;
}
.collapse-btn { font-size: 20px; cursor: pointer; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-name { font-size: 14px; color: #636E72; }
.layout-main { background: #F5F7FA; padding: 20px; }

.ws-status {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 10px;
  line-height: 20px;
}
.ws-status.connected {
  color: #2AAE67;
  background: #F0FFF6;
  border: 1px solid #C0E8CC;
}
.ws-status.disconnected {
  color: #FF4D4F;
  background: #FFF2F0;
  border: 1px solid #FFCCC7;
}

.order-badge { margin-left: 6px; }

.notify-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}
.notify-icon { font-size: 48px; }
.notify-title {
  font-size: 18px;
  font-weight: bold;
  color: #2D3436;
  margin-bottom: 8px;
}
.notify-order {
  font-size: 14px;
  color: #636E72;
  margin-bottom: 4px;
}
.notify-amount {
  font-size: 14px;
  color: #636E72;
}
.notify-amount .amount {
  font-size: 20px;
  font-weight: bold;
  color: #FF5F3E;
}
</style>
