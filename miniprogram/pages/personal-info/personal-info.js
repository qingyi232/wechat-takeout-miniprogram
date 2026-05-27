const app = getApp()
const api = require('../../utils/request')

Page({
  data: {
    userInfo: {}
  },

  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} })
  },

  changeAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const avatarUrl = res.tempFiles[0].tempFilePath
        api.put('/api/user/update', { avatarUrl }).then(() => {
          app.globalData.userInfo.avatarUrl = avatarUrl
          wx.setStorageSync('userInfo', app.globalData.userInfo)
          this.setData({ 'userInfo.avatarUrl': avatarUrl })
          wx.showToast({ title: '头像已更新', icon: 'success' })
        })
      }
    })
  },

  changeNickName() {
    wx.showModal({
      title: '修改昵称',
      editable: true,
      placeholderText: '请输入新昵称',
      content: this.data.userInfo.nickName || '',
      success: (res) => {
        if (res.confirm && res.content && res.content.trim()) {
          const nickName = res.content.trim()
          api.put('/api/user/update', { nickName }).then(() => {
            app.globalData.userInfo.nickName = nickName
            wx.setStorageSync('userInfo', app.globalData.userInfo)
            this.setData({ 'userInfo.nickName': nickName })
            wx.showToast({ title: '昵称已更新', icon: 'success' })
          })
        }
      }
    })
  }
})
