const api = require('../../utils/request')

Page({
  data: {
    orderId: '',
    merchantId: '',
    rating: 5,
    content: '',
    tags: [
      { text: '味道好', selected: false },
      { text: '分量足', selected: false },
      { text: '配送快', selected: false },
      { text: '包装好', selected: false },
      { text: '性价比高', selected: false },
      { text: '服务态度好', selected: false }
    ]
  },

  onLoad(options) {
    this.setData({
      orderId: options.orderId || '',
      merchantId: options.merchantId || ''
    })
  },

  setRating(e) {
    this.setData({ rating: e.currentTarget.dataset.rating })
  },

  toggleTag(e) {
    const index = e.currentTarget.dataset.index
    const key = `tags[${index}].selected`
    this.setData({ [key]: !this.data.tags[index].selected })
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  submitComment() {
    if (this.data.rating === 0) {
      wx.showToast({ title: '请选择评分', icon: 'none' })
      return
    }

    const selectedTags = this.data.tags.filter(t => t.selected).map(t => t.text)
    let commentText = this.data.content.trim()
    if (selectedTags.length > 0 && !commentText) {
      commentText = selectedTags.join('，')
    }

    if (!commentText) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' })
      return
    }

    api.post('/api/comment', {
      orderId: this.data.orderId,
      merchantId: this.data.merchantId,
      rating: this.data.rating,
      content: commentText,
      tags: selectedTags.join(',')
    }).then(() => {
      wx.showToast({
        title: '评价成功',
        icon: 'success',
        duration: 1500,
        success: () => {
          setTimeout(() => wx.navigateBack(), 1500)
        }
      })
    })
  }
})
