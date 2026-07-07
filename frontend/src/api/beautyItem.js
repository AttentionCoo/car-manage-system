import request from './request'

export function getBeautyItemList(params) {
  return request({ url: '/beauty-items', method: 'get', params })
}
export function getBeautyItemDetail(id) {
  return request({ url: `/beauty-items/${id}`, method: 'get' })
}
export function addBeautyItem(data) {
  return request({ url: '/beauty-items', method: 'post', data })
}
export function updateBeautyItem(id, data) {
  return request({ url: `/beauty-items/${id}`, method: 'put', data })
}
export function deleteBeautyItem(id) {
  return request({ url: `/beauty-items/${id}`, method: 'delete' })
}
export function batchUpdateStatus(data) {
  return request({ url: '/beauty-items/status/batch', method: 'put', data })
}
export function getAllItems() {
  return request({ url: '/beauty-items/all', method: 'get' })
}
