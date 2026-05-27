const app = getApp()
const api = require('../../utils/request')

Page({
  data: {
    hasApply: false,
    apply: null,
    form: {
      name: '',
      description: '',
      address: '',
      phone: '',
      categoryType: '',
      businessHours: '08:00-22:00',
      latitude: 0,
      longitude: 0
    },
    categoryOptions: ['中式快餐', '西式快餐', '奶茶饮品', '火锅烧烤', '轻食沙拉', '面食小吃', '甜品烘焙', '其他'],
    showCategoryPicker: false,
    submitting: false
  },

  onLoad() {
    this.checkApply()
  },

  checkApply() {
    api.get('/api/merchant-apply/my').then(res => {
      if (res) {
        this.setData({ hasApply: true, apply: res })
      }
    }).catch(() => {})
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  showCategorySelect() {
    this.setData({ showCategoryPicker: true })
  },

  hideCategoryPicker() {
    this.setData({ showCategoryPicker: false })
  },

  selectCategory(e) {
    const cat = e.currentTarget.dataset.cat
    this.setData({ 'form.categoryType': cat, showCategoryPicker: false })
  },

  chooseLocation() {
    const openMap = (lat, lng) => {
      const opts = {}
      if (lat && lng) { opts.latitude = lat; opts.longitude = lng }
      wx.chooseLocation({
        ...opts,
        success: (res) => {
          if (res.name || res.address) {
            const addr = (res.name ? res.name + ' ' : '') + (res.address || '')
            this.setData({
              'form.address': addr.trim(),
              'form.latitude': res.latitude,
              'form.longitude': res.longitude
            })
          }
        },
        fail: () => {
          wx.showToast({ title: '请授权位置权限后重试', icon: 'none' })
        }
      })
    }
    if (this.data.form.latitude && this.data.form.longitude) {
      openMap(this.data.form.latitude, this.data.form.longitude)
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => openMap(res.latitude, res.longitude),
        fail: () => openMap()
      })
    }
  },

  submitApply() {
    const { name, description, address, phone, categoryType } = this.data.form
    if (!name.trim()) { wx.showToast({ title: '请输入商家名称', icon: 'none' }); return }
    if (!phone.trim()) { wx.showToast({ title: '请输入联系电话', icon: 'none' }); return }
    if (!address.trim()) { wx.showToast({ title: '请输入商家地址', icon: 'none' }); return }
    if (!categoryType) { wx.showToast({ title: '请选择经营类型', icon: 'none' }); return }

    if (!app.checkLogin()) return

    this.setData({ submitting: true })
    api.post('/api/merchant-apply/submit', this.data.form).then(() => {
      wx.showToast({ title: '申请已提交', icon: 'success' })
      this.checkApply()
    }).catch(() => {}).finally(() => {
      this.setData({ submitting: false })
    })
  },

  reApply() {
    this.setData({ hasApply: false, apply: null, form: { name: '', description: '', address: '', phone: '', categoryType: '', businessHours: '08:00-22:00', latitude: 0, longitude: 0 } })
  },

  copyAccount() {
    if (this.data.apply && this.data.apply.merchantAccount) {
      wx.setClipboardData({
        data: '账号：' + this.data.apply.merchantAccount + '\n密码：' + this.data.apply.merchantPassword,
        success: () => wx.showToast({ title: '已复制账号密码', icon: 'success' })
      })
    }
  }
})
