const app = getApp()

Page({
  data: {
    isLogin: false,
    userInfo: {}
  },

  onLoad() {},

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
    this.setData({
      isLogin: app.globalData.isLogin,
      userInfo: app.globalData.userInfo || {}
    })
  },

  goLogin() {
    if (!app.globalData.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' })
    }
  },

  goAddress() {
    if (!app.checkLogin()) return
    wx.navigateTo({ url: '/pages/address/address' })
  },

  goFavorites() {
    if (!app.checkLogin()) return
    wx.navigateTo({ url: '/pages/favorites/favorites' })
  },

  goCommentCenter() {
    if (!app.checkLogin()) return
    app.globalData.orderTabFilter = 'to_comment'
    wx.switchTab({ url: '/pages/order/order' })
  },

  goHistory() {
    wx.navigateTo({ url: '/pages/history/history' })
  },

  goMerchantApply() {
    wx.navigateTo({ url: '/pages/merchant-apply/merchant-apply' })
  },

  goPersonalInfo() {
    if (!app.checkLogin()) return
    wx.navigateTo({ url: '/pages/personal-info/personal-info' })
  },

  goAbout() {
    wx.navigateTo({ url: '/pages/about/about' })
  },

  goAgreement() {
    wx.navigateTo({ url: '/pages/agreement/agreement' })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.isLogin = false
          app.globalData.token = ''
          app.globalData.userInfo = null
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          this.setData({
            isLogin: false,
            userInfo: {}
          })
          wx.showToast({ title: '已退出登录', icon: 'none' })
        }
      }
    })
  }
})
