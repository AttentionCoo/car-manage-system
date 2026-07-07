import request from './request'

export function getVehicleList(params) {
  return request({ url: '/vehicles', method: 'get', params })
}
export function getVehicleDetail(id) {
  return request({ url: `/vehicles/${id}`, method: 'get' })
}
export function addVehicle(data) {
  return request({ url: '/vehicles', method: 'post', data })
}
export function updateVehicle(id, data) {
  return request({ url: `/vehicles/${id}`, method: 'put', data })
}
export function deleteVehicle(id) {
  return request({ url: `/vehicles/${id}`, method: 'delete' })
}
export function getVehiclesByCustomer(customerId) {
  return request({ url: `/customers/${customerId}/vehicles`, method: 'get' })
}
