// 真机调试用局域网IP，模拟器用localhost
// const BASE_URL = 'http://localhost:8080'
const BASE_URL = 'http://192.168.43.67:8080'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const header = { 'Content-Type': 'application/json' }
    if (token) {
      header['Authorization'] = 'Bearer ' + token
    }
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      success: (res) => {
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          const app = getApp()
          app.globalData.isLogin = false
          app.globalData.userInfo = null
          wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/login' })
          }, 1500)
          reject(res)
          return
        }
        if (res.data && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

const get = (url, data) => request({ url, method: 'GET', data })
const post = (url, data) => request({ url, method: 'POST', data })
const put = (url, data) => request({ url, method: 'PUT', data })
const del = (url, data) => request({ url, method: 'DELETE', data })

// 处理图片URL：/uploads/开头的补全为完整后端地址
const imgUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('/uploads/')) return BASE_URL + url
  if (url.startsWith('uploads/')) return BASE_URL + '/' + url
  return url
}

module.exports = { request, get, post, put, del, BASE_URL, imgUrl }
