Component({
  data: {
    selected: 0,
    color: '#A0A8B0',
    selectedColor: '#2AAE67',
    list: [
      {
        pagePath: '/pages/index/index',
        text: '首页',
        iconType: 'home'
      },
      {
        pagePath: '/pages/order/order',
        text: '订单',
        iconType: 'order'
      },
      {
        pagePath: '/pages/mine/mine',
        text: '我的',
        iconType: 'mine'
      }
    ]
  },
  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset
      const url = data.path
      wx.switchTab({ url })
    }
  }
})
