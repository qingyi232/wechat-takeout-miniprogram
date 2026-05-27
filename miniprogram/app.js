App({
  onLaunch() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token && userInfo) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
      this.globalData.isLogin = true
    }
    const browseHistory = wx.getStorageSync('browseHistory')
    if (browseHistory) {
      this.globalData.browseHistory = browseHistory
    }
  },

  globalData: {
    isLogin: false,
    token: '',
    userInfo: null,
    cart: {},
    browseHistory: []
  },

  checkLogin() {
    if (!this.globalData.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' })
      return false
    }
    return true
  },

  addToCart(merchantId, item) {
    if (!this.globalData.cart[merchantId]) {
      this.globalData.cart[merchantId] = []
    }
    const cart = this.globalData.cart[merchantId]
    const type = item.type || 'dish'
    const specKey = item.specKey || ''
    const cartKey = type + '_' + item.id + (specKey ? '_' + specKey : '')
    const idx = cart.findIndex(c => {
      const cKey = (c.type || 'dish') + '_' + c.id + (c.specKey ? '_' + c.specKey : '')
      return cKey === cartKey
    })
    if (idx > -1) {
      cart[idx].quantity += 1
    } else {
      cart.push({ ...item, type, quantity: 1 })
    }
  },

  removeFromCart(merchantId, itemId, itemType, specKey) {
    if (!this.globalData.cart[merchantId]) return
    const cart = this.globalData.cart[merchantId]
    const type = itemType || 'dish'
    const sk = specKey || ''
    const cartKey = type + '_' + itemId + (sk ? '_' + sk : '')
    const idx = cart.findIndex(c => {
      const cKey = (c.type || 'dish') + '_' + c.id + (c.specKey ? '_' + c.specKey : '')
      return cKey === cartKey
    })
    if (idx > -1) {
      if (cart[idx].quantity > 1) {
        cart[idx].quantity -= 1
      } else {
        cart.splice(idx, 1)
      }
    }
    if (cart.length === 0) {
      delete this.globalData.cart[merchantId]
    }
  },

  clearCart(merchantId) {
    delete this.globalData.cart[merchantId]
  },

  getCart(merchantId) {
    return this.globalData.cart[merchantId] || []
  },

  getCartTotal(merchantId) {
    const cart = this.globalData.cart[merchantId] || []
    let total = 0
    let count = 0
    cart.forEach(item => {
      total += item.currentPrice * item.quantity
      count += item.quantity
    })
    return { total: Math.round(total * 100) / 100, count }
  },

  addBrowseHistory(merchant) {
    this.globalData.browseHistory = this.globalData.browseHistory.filter(h => h.id !== merchant.id)
    this.globalData.browseHistory.unshift({ ...merchant, browseTime: Date.now() })
    if (this.globalData.browseHistory.length > 50) {
      this.globalData.browseHistory = this.globalData.browseHistory.slice(0, 50)
    }
    wx.setStorageSync('browseHistory', this.globalData.browseHistory)
  }
})
