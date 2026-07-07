import request from './request'

export function getCustomerList(params) {
  return request({ url: '/customers', method: 'get', params })
}
export function getCustomerDetail(id) {
  return request({ url: `/customers/${id}`, method: 'get' })
}
export function addCustomer(data) {
  return request({ url: '/customers', method: 'post', data })
}
export function updateCustomer(id, data) {
  return request({ url: `/customers/${id}`, method: 'put', data })
}
export function deleteCustomer(id) {
  return request({ url: `/customers/${id}`, method: 'delete' })
}
export function checkPhone(phone, excludeId) {
  return request({ url: '/customers/check-phone', method: 'get', params: { phone, excludeId } })
}
