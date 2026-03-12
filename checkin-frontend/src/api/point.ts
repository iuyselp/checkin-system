import { get, post, put, del } from './request'

export interface CheckPoint {
  id: number
  name: string
  address: string
  latitude: number
  longitude: number
  radius: number
  workStartTime?: string
  workEndTime?: string
  status: number
}

export interface LocationCheckResult {
  inRange: boolean
  distance: number
  radius: number
  pointName: string
}

/**
 * 获取打卡点列表
 */
export function getPointList(params?: { name?: string; page?: number; size?: number }) {
  return get('/point/list', params)
}

/**
 * 获取所有打卡点
 */
export function getAllPoints() {
  return get<CheckPoint[]>('/point/all')
}

/**
 * 获取打卡点详情
 */
export function getPointDetail(id: number) {
  return get<CheckPoint>(`/point/${id}`)
}

/**
 * 创建打卡点
 */
export function createPoint(data: CheckPoint) {
  return post<CheckPoint>('/point', data)
}

/**
 * 更新打卡点
 */
export function updatePoint(id: number, data: CheckPoint) {
  return put<CheckPoint>(`/point/${id}`, data)
}

/**
 * 删除打卡点
 */
export function deletePoint(id: number) {
  return del(`/point/${id}`)
}

/**
 * 校验位置是否在范围内
 */
export function checkLocation(pointId: number, latitude: number, longitude: number) {
  return post<LocationCheckResult>('/point/check', null, {
    params: { pointId, latitude, longitude }
  })
}
