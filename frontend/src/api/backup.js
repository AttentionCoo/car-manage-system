import request from './request'

export function createBackup(data) {
  return request({ url: '/backup/create', method: 'post', data })
}
export function getBackupList(params) {
  return request({ url: '/backup/list', method: 'get', params })
}
export function getBackupDetail(backupId) {
  return request({ url: `/backup/${backupId}`, method: 'get' })
}
export function restoreBackup(backupId, data) {
  return request({ url: `/backup/${backupId}/restore`, method: 'post', data })
}
export function deleteBackup(backupId, data) {
  return request({ url: `/backup/${backupId}`, method: 'delete', data })
}
