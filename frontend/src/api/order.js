import request from './request'

export function getOrderList(params) {
  return request({ url: '/beauty-orders', method: 'get', params })
}
export function getOrderDetail(orderId) {
  return request({ url: `/beauty-orders/${orderId}`, method: 'get' })
}
export function createOrder(data) {
  return request({ url: '/beauty-orders', method: 'post', data })
}
export function updateOrderStatus(orderId, data) {
  return request({ url: `/beauty-orders/${orderId}/status`, method: 'put', data })
}
export function payOrder(orderId, data) {
  return request({ url: `/beauty-orders/${orderId}/payment`, method: 'post', data })
}
export function cancelOrder(orderId, data) {
  return request({ url: `/beauty-orders/${orderId}/cancel`, method: 'put', data })
}
export function getTodayPending() {
  return request({ url: '/beauty-orders/today/pending', method: 'get' })
}
export function getOrderOverview(date) {
  return request({ url: '/beauty-orders/statistics/overview', method: 'get', params: { date } })
}
