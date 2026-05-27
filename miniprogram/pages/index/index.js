const api = require('../../utils/request')

Page({
  data: {
    banners: [
      { id: 1, image: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=400&fit=crop' },
      { id: 2, image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800&h=400&fit=crop' },
      { id: 3, image: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&h=400&fit=crop' }
    ],
    announcements: [],
    categories: [
      { id: 1, name: '奶茶饮品', icon: '☕' },
      { id: 2, name: '轻食简餐', icon: '🥪' },
      { id: 3, name: '炸鸡汉堡', icon: '🍔' },
      { id: 4, name: '特色美味', icon: '🍕' }
    ],
    merchants: [],
    allMerchants: [],
    currentSort: 'default',
    sortOptions: [
      { key: 'default', label: '综合排序' },
      { key: 'speed', label: '配送最快' },
      { key: 'rating', label: '评分最高' },
      { key: 'sales', label: '销量最高' }
    ],
    showSortDropdown: false,
    userLatitude: 0,
    userLongitude: 0,
    locationName: '定位中...',
    hasLocation: false
  },

  onLoad() {
    this.getLocation()
    this.loadMerchants()
    this.loadAnnouncements()
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
  },

  getLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        const saved = wx.getStorageSync('locationName')
        this.setData({
          userLatitude: res.latitude,
          userLongitude: res.longitude,
          hasLocation: true,
          locationName: saved || '点击选择地址'
        })
        this.calcDistances()
      },
      fail: () => {
        this.setData({
          locationName: '点击定位',
          hasLocation: false
        })
      }
    })
  },

  relocate() {
    const openMap = (lat, lng) => {
      const opts = {}
      if (lat && lng) { opts.latitude = lat; opts.longitude = lng }
      wx.chooseLocation({
        ...opts,
        success: (res) => {
          if (res.name || res.address) {
            const locName = res.name || res.address
            wx.setStorageSync('locationName', locName)
            this.setData({
              userLatitude: res.latitude,
              userLongitude: res.longitude,
              hasLocation: true,
              locationName: locName
            })
            this.calcDistances()
          }
        },
        fail: () => {
          if (!this.data.hasLocation) {
            wx.getLocation({
              type: 'gcj02',
              success: (res) => {
                this.setData({
                  userLatitude: res.latitude,
                  userLongitude: res.longitude,
                  hasLocation: true,
                  locationName: '已定位·点击选地址'
                })
                this.calcDistances()
              },
              fail: () => {
                wx.showToast({ title: '请在设置中授权位置权限', icon: 'none' })
              }
            })
          }
        }
      })
    }
    if (this.data.userLatitude && this.data.userLongitude) {
      openMap(this.data.userLatitude, this.data.userLongitude)
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => openMap(res.latitude, res.longitude),
        fail: () => openMap()
      })
    }
  },

  calcDistance(lat1, lng1, lat2, lng2) {
    const rad = Math.PI / 180
    const R = 6371
    const dLat = (lat2 - lat1) * rad
    const dLng = (lng2 - lng1) * rad
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * rad) * Math.cos(lat2 * rad) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return Math.round(R * c * 10) / 10
  },

  calcDistances() {
    if (!this.data.hasLocation || this.data.allMerchants.length === 0) return
    const userLat = this.data.userLatitude
    const userLng = this.data.userLongitude
    const merchants = this.data.allMerchants.map((m, idx) => {
      // 以用户位置为中心，给每个商家生成一个固定的近距离偏移（0.5~3km范围）
      // 用商家id做种子，保证同一商家每次偏移一致
      const seed = (m.id || idx) * 137.5
      const offsetLat = (Math.sin(seed) * 0.012 + Math.cos(seed * 2.3) * 0.008)
      const offsetLng = (Math.cos(seed) * 0.015 + Math.sin(seed * 1.7) * 0.006)
      const mLat = userLat + offsetLat
      const mLng = userLng + offsetLng
      const distance = this.calcDistance(userLat, userLng, mLat, mLng)
      return { ...m, distance }
    })
    this.setData({ allMerchants: merchants })
    this.applySortFilter()
  },

  loadMerchants() {
    const { imgUrl } = require('../../utils/request')
    api.get('/api/merchant/list', { sortBy: this.data.currentSort }).then(res => {
      const merchants = (res || []).map(m => ({ ...m, logo: imgUrl(m.logo), distance: null }))
      this.setData({ allMerchants: merchants })
      if (this.data.hasLocation) {
        this.calcDistances()
      } else {
        this.applySortFilter()
      }
    })
  },

  loadAnnouncements() {
    api.get('/api/announcement/list').then(res => {
      const list = (res || []).map(a => a.title + '：' + a.content)
      if (list.length > 0) {
        this.setData({ announcements: list })
      }
    }).catch(() => {})
  },

  toggleSortDropdown() {
    this.setData({ showSortDropdown: !this.data.showSortDropdown })
  },

  closeSortDropdown() {
    this.setData({ showSortDropdown: false })
  },

  selectSort(e) {
    const key = e.currentTarget.dataset.key
    this.setData({
      currentSort: key,
      showSortDropdown: false
    })
    this.applySortFilter()
  },

  applySortFilter() {
    let merchants = [...this.data.allMerchants]
    switch (this.data.currentSort) {
      case 'speed':
        merchants.sort((a, b) => (a.deliveryTime || 99) - (b.deliveryTime || 99))
        break
      case 'rating':
        merchants.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        break
      case 'sales':
        merchants.sort((a, b) => (b.monthlySales || 0) - (a.monthlySales || 0))
        break
      case 'distance':
        merchants.sort((a, b) => (a.distance || 999) - (b.distance || 999))
        break
      default:
        merchants.sort((a, b) => (b.monthlySales || 0) - (a.monthlySales || 0))
    }
    this.setData({ merchants })
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  goCategory(e) {
    const name = e.currentTarget.dataset.name
    wx.navigateTo({ url: '/pages/search/search?category=' + encodeURIComponent(name) })
  },

  goShop(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
  },

  onPullDownRefresh() {
    this.getLocation()
    this.loadMerchants()
    this.loadAnnouncements()
    wx.stopPullDownRefresh()
  }
})
