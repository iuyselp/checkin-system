import { get, post, put } from './request'

export interface User {
  id: number
  username: string
  phone?: string
  avatar?: string
  status: number
}

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  phone?: string
}

export interface LoginResult {
  token: string
  user: User
}

/**
 * 用户登录
 */
export function login(data: LoginParams) {
  return post<LoginResult>('/user/login', data)
}

/**
 * 用户注册
 */
export function register(data: RegisterParams) {
  return post<User>('/user/register', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return get<User>('/user/info')
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data: Partial<User>) {
  return put<User>('/user/info', data)
}

/**
 * 修改密码
 */
export function updatePassword(oldPassword: string, newPassword: string) {
  return put('/user/password', null, {
    params: { oldPassword, newPassword }
  })
}
