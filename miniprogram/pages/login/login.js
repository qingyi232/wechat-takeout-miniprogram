const app = getApp()
const api = require('../../utils/request')

Page({
  data: {
    nickName: '',
    avatarUrl: ''
  },

  onNickNameInput(e) {
    this.setData({ nickName: e.detail.value })
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({ avatarUrl: res.tempFiles[0].tempFilePath })
      },
      fail: () => {
        this.setData({
          avatarUrl: 'https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.jpg'
        })
      }
    })
  },

  login() {
    const nickName = this.data.nickName.trim()
    if (!nickName) {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }
    const avatarUrl = this.data.avatarUrl || 'https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.jpg'
    wx.showLoading({ title: '登录中...' })
    const openid = 'wx_user_' + nickName
    api.post('/api/user/login', { openid, nickName, avatarUrl }).then(res => {
      wx.hideLoading()
      const token = res.token
      const userInfo = res.user
      wx.setStorageSync('token', token)
      wx.setStorageSync('userInfo', userInfo)
      app.globalData.token = token
      app.globalData.userInfo = userInfo
      app.globalData.isLogin = true
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500,
        success: () => {
          setTimeout(() => { wx.navigateBack() }, 1500)
        }
      })
    }).catch(() => {
      wx.hideLoading()
    })
  },

  goAgreement() {
    wx.navigateTo({ url: '/pages/agreement/agreement' })
  }
})
