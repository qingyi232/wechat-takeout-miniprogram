const api = require('../../utils/request')

Page({
  data: {
    addressList: [],
    selectMode: false
  },

  onLoad(options) {
    if (options.select === '1') {
      this.setData({ selectMode: true })
    }
  },

  onShow() {
    this.loadAddresses()
  },

  loadAddresses() {
    api.get('/api/address/list').then(res => {
      this.setData({ addressList: res || [] })
    })
  },

  selectAddress(e) {
    if (!this.data.selectMode) return
    const index = e.currentTarget.dataset.index
    const address = this.data.addressList[index]
    wx.setStorageSync('selectedAddress', address)
    wx.navigateBack()
  },

  addAddress() {
    wx.navigateTo({ url: '/pages/address-edit/address-edit' })
  },

  editAddress(e) {
    const id = this.data.addressList[e.currentTarget.dataset.index].id
    wx.navigateTo({ url: '/pages/address-edit/address-edit?id=' + id })
  },

  deleteAddress(e) {
    const id = this.data.addressList[e.currentTarget.dataset.index].id
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          api.del('/api/address/' + id).then(() => {
            wx.showToast({ title: '已删除', icon: 'none' })
            this.loadAddresses()
          })
        }
      }
    })
  }
})
