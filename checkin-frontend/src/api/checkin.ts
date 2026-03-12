import { get, post } from './request'

export interface CheckinParams {
  pointId: number
  latitude: number
  longitude: number
}

export interface CheckRecord {
  id: number
  userId: number
  pointId: number
  pointName?: string
  checkType: number // 1-上班 2-下班
  checkTime: string
  latitude: number
  longitude: number
  distance: number
  status: number // 1-正常 2-迟到 3-早退 4-缺卡
  remark?: string
}

export interface TodayStatus {
  workRecord: CheckRecord | null
  offRecord: CheckRecord | null
  hasWorkCheckin: boolean
  hasOffCheckin: boolean
}

/**
 * 上班打卡
 */
export function workCheckin(data: CheckinParams) {
  return post<CheckRecord>('/checkin/work', data)
}

/**
 * 下班打卡
 */
export function offCheckin(data: CheckinParams) {
  return post<CheckRecord>('/checkin/off', data)
}

/**
 * 获取今日打卡状态
 */
export function getTodayStatus() {
  return get<TodayStatus>('/checkin/today/status')
}

/**
 * 获取打卡记录列表
 */
export function getRecords(params: {
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}) {
  return get('/checkin/records', params)
}
