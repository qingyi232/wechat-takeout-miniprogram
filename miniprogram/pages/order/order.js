const app = getApp()
const api = require('../../utils/request')
const { imgUrl } = require('../../utils/request')

const STATUS_MAP = {
  1: { status: 'pending_pay', text: '待支付' },
  2: { status: 'pending_accept', text: '待接单' },
  3: { status: 'delivering', text: '配送中' },
  4: { status: 'completed', text: '已完成' },
  5: { status: 'cancelled', text: '已取消' },
  6: { status: 'refunded', text: '已退款' }
}

Page({
  data: {
    currentStatus: 'all',
    orders: [],
    filteredOrders: []
  },

  onLoad() {},

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
    const filter = app.globalData.orderTabFilter
    if (filter) {
      this.setData({ currentStatus: filter })
      app.globalData.orderTabFilter = null
    }
    this.loadOrders()
  },

  loadOrders() {
    if (!app.globalData.isLogin) {
      this.setData({ orders: [], filteredOrders: [] })
      return
    }
    api.get('/api/order/user/list').then(res => {
      const orders = (res || []).map(o => {
        const s = STATUS_MAP[o.status] || { status: 'unknown', text: '未知' }
        return {
          id: o.id,
          merchantId: o.merchantId,
          merchantName: o.merchantName || '商家',
          merchantLogo: o.merchantLogo || '',
          totalPrice: o.payAmount || o.totalAmount,
          status: s.status,
          statusText: s.text,
          createTime: o.createTime,
          itemsDesc: o.itemsDesc || o.remark || '',
          firstItemImage: imgUrl(o.firstItemImage || o.merchantLogo || ''),
          commented: o.commented || false,
          itemCount: o.itemCount || 0
        }
      })
      this.setData({ orders })
      this.filterOrders()
    })
  },

  switchStatus(e) {
    const status = e.currentTarget.dataset.status
    this.setData({ currentStatus: status })
    this.filterOrders()
  },

  filterOrders() {
    const { currentStatus, orders } = this.data
    let filtered = orders
    if (currentStatus === 'ongoing') {
      filtered = orders.filter(o => ['pending_pay', 'pending_accept', 'delivering'].includes(o.status))
    } else if (currentStatus === 'to_comment') {
      filtered = orders.filter(o => o.status === 'completed' && !o.commented)
    } else if (currentStatus === 'refunded') {
      filtered = orders.filter(o => o.status === 'refunded')
    }
    this.setData({ filteredOrders: filtered })
  },

  goOrderDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
  },

  payOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '虚拟支付',
      content: '确认支付该订单？(虚拟支付)',
      confirmText: '确认支付',
      confirmColor: '#2AAE67',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/pay/' + id).then(() => {
            wx.showToast({ title: '支付成功', icon: 'success' })
            this.loadOrders()
          })
        }
      }
    })
  },

  cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + id + '?status=5').then(() => {
            wx.showToast({ title: '已取消', icon: 'none' })
            this.loadOrders()
          })
        }
      }
    })
  },

  refundOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '申请退款',
      content: '确定申请退款吗？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + id + '?status=6').then(() => {
            wx.showToast({ title: '退款成功', icon: 'success' })
            this.loadOrders()
          })
        }
      }
    })
  },

  confirmReceive(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + id + '?status=4').then(() => {
            wx.showToast({ title: '已确认收货', icon: 'success' })
            this.loadOrders()
          })
        }
      }
    })
  },

  deleteOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该订单吗？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + id + '?status=5').then(() => {
            wx.showToast({ title: '已删除', icon: 'none' })
            this.loadOrders()
          })
        }
      }
    })
  },

  goComment(e) {
    const id = e.currentTarget.dataset.id
    const merchantId = e.currentTarget.dataset.merchantId
    wx.navigateTo({ url: '/pages/comment/comment?orderId=' + id + '&merchantId=' + merchantId })
  }
})
