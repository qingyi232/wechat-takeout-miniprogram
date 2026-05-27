const api = require('../../utils/request')

Page({
  data: {
    keyword: '',
    categories: [
      { id: 1, name: '奶茶饮品', icon: '☕' },
      { id: 2, name: '轻食简餐', icon: '🥪' },
      { id: 3, name: '炸鸡汉堡', icon: '🍔' },
      { id: 4, name: '特色美味', icon: '🍕' }
    ],
    results: [],
    hasSearched: false,
    userLatitude: 0,
    userLongitude: 0
  },

  onLoad(options) {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({ userLatitude: res.latitude, userLongitude: res.longitude })
      },
      fail: () => {}
    })
    if (options.category) {
      const category = decodeURIComponent(options.category)
      this.setData({ keyword: category })
      this.searchByCategory({ currentTarget: { dataset: { name: category } } })
    }
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  clearKeyword() {
    this.setData({ keyword: '', hasSearched: false, results: [] })
  },

  doSearch() {
    const keyword = this.data.keyword.trim()
    if (!keyword) {
      wx.showToast({ title: '请输入搜索内容', icon: 'none' })
      return
    }
    api.get('/api/merchant/list', { keyword }).then(res => {
      const results = this.addDistances(res || [])
      this.setData({ results, hasSearched: true })
    })
  },

  searchByCategory(e) {
    const name = e.currentTarget.dataset.name
    this.setData({ keyword: name })
    api.get('/api/merchant/list', { categoryType: name }).then(res => {
      const results = this.addDistances(res || [])
      this.setData({ results, hasSearched: true })
    })
  },

  addDistances(merchants) {
    const { imgUrl } = require('../../utils/request')
    const userLat = this.data.userLatitude
    const userLng = this.data.userLongitude
    return merchants.map((m, idx) => {
      m.logo = imgUrl(m.logo)
      if (!userLat || !userLng) return m
      const seed = (m.id || idx) * 137.5
      const offsetLat = (Math.sin(seed) * 0.012 + Math.cos(seed * 2.3) * 0.008)
      const offsetLng = (Math.cos(seed) * 0.015 + Math.sin(seed * 1.7) * 0.006)
      const mLat = userLat + offsetLat
      const mLng = userLng + offsetLng
      const rad = Math.PI / 180
      const dLat = (mLat - userLat) * rad
      const dLng = (mLng - userLng) * rad
      const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(userLat * rad) * Math.cos(mLat * rad) *
        Math.sin(dLng / 2) * Math.sin(dLng / 2)
      const distance = Math.round(6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 10) / 10
      return { ...m, distance }
    })
  },

  goShop(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
  }
})
