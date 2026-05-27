const app = getApp()
const api = require('../../utils/request')

Page({
  data: {
    merchant: null,
    cartItems: [],
    totalPrice: 0,
    discount: 0,
    deliveryFee: 0,
    payAmount: 0,
    address: null,
    remark: '',
    merchantId: ''
  },

  onLoad(options) {
    const merchantId = options.merchantId
    this.setData({ merchantId })
    const cartItems = app.getCart(merchantId)
    if (cartItems.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      wx.navigateBack()
      return
    }
    api.get('/api/merchant/detail/' + merchantId).then(merchant => {
      let originalTotal = 0
      let currentTotal = 0
      cartItems.forEach(item => {
        originalTotal += item.originalPrice * item.quantity
        currentTotal += item.currentPrice * item.quantity
      })
      originalTotal = Math.round(originalTotal * 100) / 100
      currentTotal = Math.round(currentTotal * 100) / 100
      const discount = Math.round((originalTotal - currentTotal) * 100) / 100
      // 配送费计算
      let deliveryFee = 0
      if (merchant.freeDelivery !== 1) {
        deliveryFee = Number(merchant.deliveryFee) || 0
      }
      const payAmount = Math.round((currentTotal + deliveryFee) * 100) / 100
      this.setData({
        merchant,
        cartItems,
        totalPrice: currentTotal.toFixed(1),
        discount: discount.toFixed(1),
        deliveryFee: deliveryFee.toFixed(1),
        payAmount: payAmount.toFixed(1)
      })
    })
  },

  onShow() {
    const selectedAddress = wx.getStorageSync('selectedAddress')
    if (selectedAddress) {
      this.setData({ address: selectedAddress })
      wx.removeStorageSync('selectedAddress')
      return
    }
    if (!this.data.address) {
      api.get('/api/address/default').then(addr => {
        if (addr) this.setData({ address: addr })
      }).catch(() => {})
    }
  },

  chooseAddress() {
    wx.navigateTo({ url: '/pages/address/address?select=1' })
  },

  calcDistance(lat1, lng1, lat2, lng2) {
    const rad = Math.PI / 180
    const dLat = (lat2 - lat1) * rad
    const dLng = (lng2 - lng1) * rad
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * rad) * Math.cos(lat2 * rad) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2)
    return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  },

  editRemark() {
    wx.showModal({
      title: '备注',
      editable: true,
      placeholderText: '请输入口味偏好或其他要求',
      content: this.data.remark,
      success: (res) => {
        if (res.confirm && res.content) {
          this.setData({ remark: res.content })
        }
      }
    })
  },

  submitOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
    // 配送距离校验（如果商家坐标是示例数据则跳过）
    const merchant = this.data.merchant
    const addr = this.data.address
    if (merchant && merchant.latitude && merchant.longitude && addr.latitude && addr.longitude) {
      const dist = this.calcDistance(merchant.latitude, merchant.longitude, addr.latitude, addr.longitude)
      const range = Number(merchant.deliveryRange) || 5
      // 距离超过100km说明商家坐标是示例数据，跳过校验
      if (dist <= 100 && dist > range) {
        wx.showModal({
          title: '超出配送范围',
          content: `当前收货地址距离商家${dist.toFixed(1)}公里，超出商家配送范围(${range}公里)，请更换收货地址。`,
          showCancel: false,
          confirmText: '我知道了',
          confirmColor: '#2AAE67'
        })
        return
      }
    }
    wx.showModal({
      title: '虚拟支付',
      content: `订单金额: ¥${this.data.payAmount}\n(含配送费 ¥${this.data.deliveryFee})\n确认支付？(虚拟支付，无需真实付款)`,
      confirmText: '确认支付',
      confirmColor: '#2AAE67',
      success: (res) => {
        if (res.confirm) {
          this.createOrder()
        }
      }
    })
  },

  createOrder() {
    const addr = this.data.address
    const params = {
      merchantId: this.data.merchantId,
      addressId: addr.id || null,
      addressDetail: addr.address || addr.detail || '',
      contactName: addr.name || addr.contactName || '',
      contactPhone: addr.phone || addr.contactPhone || '',
      remark: this.data.remark,
      items: this.data.cartItems.map(item => ({
        dishId: item.type === 'combo' ? null : item.id,
        comboId: item.type === 'combo' ? item.id : null,
        name: item.name,
        image: item.image,
        price: item.currentPrice,
        quantity: item.quantity
      }))
    }
    wx.showLoading({ title: '下单中...' })
    api.post('/api/order/create', params).then(order => {
      wx.hideLoading()
      api.put('/api/order/pay/' + order.id).then(() => {
        app.clearCart(this.data.merchantId)
        wx.showToast({
          title: '支付成功',
          icon: 'success',
          duration: 1500,
          success: () => {
            setTimeout(() => { wx.switchTab({ url: '/pages/order/order' }) }, 1500)
          }
        })
      })
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
