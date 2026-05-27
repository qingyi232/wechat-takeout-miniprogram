const api = require('../../utils/request')

Page({
  data: {
    editId: null,
    name: '',
    phone: '',
    address: '',
    latitude: 0,
    longitude: 0,
    isDefault: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ editId: options.id })
      wx.setNavigationBarTitle({ title: '编辑地址' })
      api.get('/api/address/list').then(list => {
        const addr = (list || []).find(a => String(a.id) === String(options.id))
        if (addr) {
          this.setData({
            name: addr.name || addr.contactName || '',
            phone: addr.phone || addr.contactPhone || '',
            address: addr.address || addr.detail || '',
            isDefault: !!addr.isDefault
          })
        }
      })
    } else {
      wx.setNavigationBarTitle({ title: '新增地址' })
    }
  },

  onNameInput(e) { this.setData({ name: e.detail.value }) },
  onPhoneInput(e) { this.setData({ phone: e.detail.value }) },
  onAddressInput(e) { this.setData({ address: e.detail.value }) },
  onDefaultChange(e) { this.setData({ isDefault: e.detail.value }) },

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
              address: addr.trim(),
              latitude: res.latitude,
              longitude: res.longitude
            })
          }
        },
        fail: () => {
          wx.showToast({ title: '请授权位置权限后重试', icon: 'none' })
        }
      })
    }
    if (this.data.latitude && this.data.longitude) {
      openMap(this.data.latitude, this.data.longitude)
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => openMap(res.latitude, res.longitude),
        fail: () => openMap()
      })
    }
  },

  saveAddress() {
    const { name, phone, address, isDefault, editId, latitude, longitude } = this.data
    if (!name.trim()) {
      wx.showToast({ title: '请输入联系人', icon: 'none' }); return
    }
    if (!phone.trim() || phone.length !== 11) {
      wx.showToast({ title: '请输入正确手机号', icon: 'none' }); return
    }
    if (!address.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' }); return
    }

    const addrObj = {
      name: name.trim(),
      phone: phone.trim(),
      address: address.trim(),
      latitude: latitude || 0,
      longitude: longitude || 0,
      isDefault: isDefault ? 1 : 0
    }

    const req = editId
      ? api.put('/api/address', { ...addrObj, id: editId })
      : api.post('/api/address', addrObj)

    req.then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => { wx.navigateBack() }, 1000)
    })
  }
})
