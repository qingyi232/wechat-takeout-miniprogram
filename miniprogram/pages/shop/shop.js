const app = getApp()
const api = require('../../utils/request')

Page({
  data: {
    merchant: null,
    categories: [],
    dishes: [],
    combos: [],
    currentTab: 'goods',
    activeCategoryId: 0,
    showCombo: false,
    filteredDishes: [],
    cartItems: [],
    cartCount: 0,
    cartTotal: 0,
    showCartPopup: false,
    isFavorite: false,
    comments: [],
    scrollCategoryId: '',
    scrollDishId: '',
    showSpecPopup: false,
    specDish: null,
    specGroups: [],
    selectedSpecs: {},
    specPrice: 0
  },

  onLoad(options) {
    this.merchantId = options.id
    this.loadMerchant()
    this.loadDishes()
    this.loadComments()
    this.checkFavorite()
  },

  loadMerchant() {
    const { imgUrl } = require('../../utils/request')
    api.get('/api/merchant/detail/' + this.merchantId).then(merchant => {
      merchant.logo = imgUrl(merchant.logo)
      this.setData({ merchant })
      app.addBrowseHistory({
        id: merchant.id,
        name: merchant.name,
        logo: merchant.logo,
        rating: merchant.rating,
        description: merchant.description
      })
    })
  },

  loadDishes() {
    Promise.all([
      api.get('/api/category/list/' + this.merchantId),
      api.get('/api/dish/list/' + this.merchantId),
      api.get('/api/combo/list/' + this.merchantId)
    ]).then(([categories, dishes, combos]) => {
      const { imgUrl } = require('../../utils/request')
      const allDishes = (dishes || []).map(d => ({
        id: d.id,
        type: 'dish',
        name: d.name,
        description: d.description,
        image: imgUrl(d.image),
        originalPrice: d.originalPrice,
        currentPrice: d.currentPrice || d.originalPrice,
        discount: d.discount || 1,
        sales: d.sales || 0,
        categoryId: d.categoryId,
        stock: d.stock || 999
      }))
      const allCombos = (combos || []).map(c => ({
        id: c.id,
        type: 'combo',
        name: c.name,
        description: c.description,
        image: imgUrl(c.image),
        originalPrice: c.price,
        currentPrice: c.price,
        categoryId: c.categoryId
      }))
      const cats = categories || []
      const firstCategoryId = cats[0] ? cats[0].id : 0
      this.setData({
        categories: cats,
        dishes: allDishes,
        combos: allCombos,
        activeCategoryId: firstCategoryId,
        showCombo: false,
        filteredDishes: this.filterDishes(allDishes, firstCategoryId)
      })
    })
  },

  loadComments() {
    api.get('/api/comment/list/' + this.merchantId).then(comments => {
      const list = (comments || []).map(c => ({
        avatar: c.avatarUrl || '',
        nickname: c.nickName || '匿名用户',
        time: (c.createTime || '').substring(0, 10),
        stars: '★'.repeat(c.rating || 5) + '☆'.repeat(5 - (c.rating || 5)),
        content: c.content || ''
      }))
      this.setData({ comments: list })
    })
  },

  checkFavorite() {
    if (!app.globalData.isLogin) return
    api.get('/api/favorite/check/' + this.merchantId).then(res => {
      this.setData({ isFavorite: !!res })
    }).catch(() => {})
  },

  onShow() {
    this.updateCart()
  },

  filterDishes(dishes, categoryId) {
    if (!categoryId) return dishes
    return dishes.filter(d => d.categoryId === categoryId)
  },

  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.tab })
  },

  selectCategory(e) {
    const categoryId = e.currentTarget.dataset.id
    if (categoryId === 'combo') {
      this.setData({ showCombo: true, activeCategoryId: 'combo' })
    } else {
      this.setData({
        showCombo: false,
        activeCategoryId: categoryId,
        filteredDishes: this.filterDishes(this.data.dishes, categoryId)
      })
    }
  },

  addToCart(e) {
    const item = e.currentTarget.dataset.dish || e.currentTarget.dataset.combo
    api.get('/api/dish-spec/dish/' + item.id).then(groups => {
      if (groups && groups.length > 0) {
        const selectedSpecs = {}
        groups.forEach(g => {
          const def = g.specs.find(s => s.isDefault === 1)
          if (def) selectedSpecs[g.id] = def
        })
        this.setData({
          showSpecPopup: true,
          specDish: item,
          specGroups: groups,
          selectedSpecs,
          specPrice: this.calcSpecPrice(item, selectedSpecs)
        })
      } else {
        app.addToCart(this.merchantId, item)
        this.updateCart()
        wx.showToast({ title: '已加入购物车', icon: 'success', duration: 800 })
      }
    }).catch(() => {
      app.addToCart(this.merchantId, item)
      this.updateCart()
      wx.showToast({ title: '已加入购物车', icon: 'success', duration: 800 })
    })
  },

  addComboToCart(e) {
    const combo = e.currentTarget.dataset.combo
    app.addToCart(this.merchantId, combo)
    this.updateCart()
    wx.showToast({ title: '已加入购物车', icon: 'success', duration: 800 })
  },

  calcSpecPrice(dish, selectedSpecs) {
    let extra = 0
    Object.values(selectedSpecs).forEach(spec => {
      extra += parseFloat(spec.priceAdjustment || 0)
    })
    return Math.round(((dish.currentPrice || dish.originalPrice) + extra) * 100) / 100
  },

  selectSpec(e) {
    const groupId = e.currentTarget.dataset.groupId
    const specId = e.currentTarget.dataset.specId
    const group = this.data.specGroups.find(g => g.id === groupId)
    if (!group) return
    const spec = group.specs.find(s => s.id === specId)
    if (!spec) return
    const selectedSpecs = { ...this.data.selectedSpecs }
    selectedSpecs[groupId] = spec
    this.setData({
      selectedSpecs,
      specPrice: this.calcSpecPrice(this.data.specDish, selectedSpecs)
    })
  },

  closeSpecPopup() {
    this.setData({ showSpecPopup: false, specDish: null, specGroups: [], selectedSpecs: {} })
  },

  confirmSpec() {
    const dish = this.data.specDish
    const selectedSpecs = this.data.selectedSpecs
    const specNames = Object.values(selectedSpecs).map(s => s.name).join('/')
    const cartItem = {
      ...dish,
      specKey: specNames,
      currentPrice: this.data.specPrice,
      name: dish.name + '(' + specNames + ')'
    }
    app.addToCart(this.merchantId, cartItem)
    this.updateCart()
    this.setData({ showSpecPopup: false, specDish: null, specGroups: [], selectedSpecs: {} })
    wx.showToast({ title: '已加入购物车', icon: 'success', duration: 800 })
  },

  updateCart() {
    if (!this.merchantId) return
    const cartItems = app.getCart(this.merchantId)
    const { total, count } = app.getCartTotal(this.merchantId)
    this.setData({
      cartItems,
      cartCount: count,
      cartTotal: total.toFixed(1)
    })
  },

  increaseQty(e) {
    const itemId = e.currentTarget.dataset.id
    const itemType = e.currentTarget.dataset.type || 'dish'
    const specKey = e.currentTarget.dataset.speckey || ''
    const item = this.data.cartItems.find(c => {
      const cKey = (c.type || 'dish') + '_' + c.id + (c.specKey ? '_' + c.specKey : '')
      const tKey = itemType + '_' + itemId + (specKey ? '_' + specKey : '')
      return cKey === tKey
    })
    if (item) {
      app.addToCart(this.merchantId, item)
      this.updateCart()
    }
  },

  decreaseQty(e) {
    const itemId = e.currentTarget.dataset.id
    const itemType = e.currentTarget.dataset.type || 'dish'
    const specKey = e.currentTarget.dataset.speckey || ''
    app.removeFromCart(this.merchantId, itemId, itemType, specKey)
    this.updateCart()
    if (this.data.cartItems.length === 0) {
      this.setData({ showCartPopup: false })
    }
  },

  clearCart() {
    wx.showModal({
      title: '提示',
      content: '确定清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearCart(this.merchantId)
          this.updateCart()
          this.setData({ showCartPopup: false })
        }
      }
    })
  },

  toggleCartPopup() {
    if (this.data.cartItems.length === 0) {
      wx.showToast({ title: '购物车是空的', icon: 'none' })
      return
    }
    this.setData({ showCartPopup: !this.data.showCartPopup })
  },

  toggleFavorite() {
    if (!app.checkLogin()) return
    if (this.data.isFavorite) {
      api.del('/api/favorite/' + this.merchantId).then(() => {
        this.setData({ isFavorite: false })
        wx.showToast({ title: '已取消收藏', icon: 'none' })
      })
    } else {
      api.post('/api/favorite/' + this.merchantId).then(() => {
        this.setData({ isFavorite: true })
        wx.showToast({ title: '已收藏', icon: 'success' })
      })
    }
  },

  goConfirm() {
    if (this.data.cartCount === 0) {
      wx.showToast({ title: '请先选择菜品', icon: 'none' })
      return
    }
    if (!app.checkLogin()) return
    wx.navigateTo({
      url: '/pages/confirm/confirm?merchantId=' + this.merchantId
    })
  }
})
