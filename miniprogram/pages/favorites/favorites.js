const api = require('../../utils/request')

Page({
  data: {
    favorites: []
  },

  onShow() {
    this.loadFavorites()
  },

  loadFavorites() {
    api.get('/api/favorite/list').then(res => {
      this.setData({ favorites: res || [] })
    }).catch(() => {})
  },

  goShop(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
  },

  removeFavorite(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定取消收藏吗？',
      success: (res) => {
        if (res.confirm) {
          api.del('/api/favorite/' + id).then(() => {
            wx.showToast({ title: '已取消收藏', icon: 'none' })
            this.loadFavorites()
          })
        }
      }
    })
  }
})
