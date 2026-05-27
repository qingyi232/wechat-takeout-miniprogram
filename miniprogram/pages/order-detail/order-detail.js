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
    order: null,
    orderId: ''
  },

  onLoad(options) {
    this.setData({ orderId: options.id })
  },

  onShow() {
    this.loadOrder()
  },

  loadOrder() {
    api.get('/api/order/detail/' + this.data.orderId).then(res => {
      if (!res || !res.order) {
        wx.showToast({ title: '订单不存在', icon: 'none' })
        setTimeout(() => wx.navigateBack(), 1500)
        return
      }
      const o = res.order
      const s = STATUS_MAP[o.status] || { status: 'unknown', text: '未知' }
      const items = (res.items || []).map(item => ({
        id: item.dishId || item.comboId,
        name: item.name,
        image: imgUrl(item.image),
        price: item.price,
        quantity: item.quantity
      }))
      const order = {
        id: o.id,
        merchantId: o.merchantId,
        merchantName: res.merchantName || '商家',
        merchantLogo: '',
        items: items,
        totalPrice: o.payAmount,
        discount: o.discountAmount || 0,
        deliveryFee: o.deliveryFee || 0,
        status: s.status,
        statusText: s.text,
        createTime: o.createTime,
        address: {
          name: o.contactName,
          phone: o.contactPhone,
          address: o.addressDetail
        },
        remark: o.remark || ''
      }
      this.setData({ order })
    })
  },

  copyOrderId() {
    wx.setClipboardData({
      data: String(this.data.order.id),
      success: () => wx.showToast({ title: '已复制', icon: 'success' })
    })
  },

  payOrder() {
    wx.showModal({
      title: '虚拟支付',
      content: '确认支付 ¥' + this.data.order.totalPrice + '？(虚拟支付)',
      confirmText: '确认支付',
      confirmColor: '#2AAE67',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/pay/' + this.data.orderId).then(() => {
            wx.showToast({ title: '支付成功', icon: 'success' })
            this.loadOrder()
          })
        }
      }
    })
  },

  cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + this.data.orderId + '?status=5').then(() => {
            wx.showToast({ title: '已取消', icon: 'none' })
            this.loadOrder()
          })
        }
      }
    })
  },

  confirmReceive() {
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: (res) => {
        if (res.confirm) {
          api.put('/api/order/status/' + this.data.orderId + '?status=4').then(() => {
            wx.showToast({ title: '已确认收货', icon: 'success' })
            this.loadOrder()
          })
        }
      }
    })
  }
})
