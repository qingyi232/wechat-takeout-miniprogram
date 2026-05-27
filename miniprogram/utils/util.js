const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

const generateOrderId = () => {
  const now = new Date()
  const y = now.getFullYear()
  const m = formatNumber(now.getMonth() + 1)
  const d = formatNumber(now.getDate())
  const h = formatNumber(now.getHours())
  const min = formatNumber(now.getMinutes())
  const s = formatNumber(now.getSeconds())
  const rand = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `${y}${m}${d}${h}${min}${s}${rand}`
}

const formatPrice = price => {
  return parseFloat(price).toFixed(1)
}

module.exports = {
  formatTime,
  formatNumber,
  generateOrderId,
  formatPrice
}
