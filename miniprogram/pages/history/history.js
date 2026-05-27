const app = getApp()

Page({
  data: {
    historyList: []
  },

  onShow() {
    this.loadHistory()
  },

  loadHistory() {
    const history = (app.globalData.browseHistory || []).map(item => {
      const now = Date.now()
      const diff = now - item.browseTime
      let timeText = ''
      if (diff < 60000) {
        timeText = '刚刚'
      } else if (diff < 3600000) {
        timeText = Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) {
        timeText = Math.floor(diff / 3600000) + '小时前'
      } else {
        const d = new Date(item.browseTime)
        timeText = `${d.getMonth() + 1}-${d.getDate()}`
      }
      return { ...item, timeText }
    })
    this.setData({ historyList: history })
  },

  goShop(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
  },

  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空所有浏览记录吗？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.browseHistory = []
          wx.setStorageSync('browseHistory', [])
          this.setData({ historyList: [] })
          wx.showToast({ title: '已清空', icon: 'none' })
        }
      }
    })
  }
})
