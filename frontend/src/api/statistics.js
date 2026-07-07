import request from './request'

export function getMonthlyItemCount(year, month) {
  return request({ url: '/statistics/monthly-item-count', method: 'get', params: { year, month } })
}
export function getYearlyCustomerCount(year) {
  return request({ url: '/statistics/yearly-customer-count', method: 'get', params: { year } })
}
export function getMonthlyRevenue(year, month) {
  return request({ url: '/statistics/monthly-revenue', method: 'get', params: { year, month } })
}
