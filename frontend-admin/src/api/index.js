import http from './request'

// 认证
export const login = (data) => http.post('/auth/login', data)
export const register = (data) => http.post('/auth/register', data)

// 用户信息
export const getUserInfo = () => http.get('/user/info')
export const updateProfile = (data) => http.put('/user/profile', data)
export const updatePassword = (data) => http.put('/user/password', data)

// 用户管理
export const getUserList = (page, size) => http.get('/user/list', { params: { page, size } })
export const searchUsers = (field, keyword, page, size) =>
  http.get('/user/search', { params: { field, keyword, page, size } })
export const createUser = (data) => http.post('/user', data)
export const updateUser = (id, data) => http.put(`/user/${id}`, data)
export const deleteUser = (id) => http.delete(`/user/${id}`)

// 充电宝管理
export const getPowerBankList = (page, size) => http.get('/powerbank/list', { params: { page, size } })
export const getAvailableCount = () => http.get('/powerbank/available/count')
export const addPowerBank = (data) => http.post('/powerbank', data)
export const updatePowerBank = (id, data) => http.put(`/powerbank/${id}`, data)
export const updatePowerBankStatus = (id, status) => http.put(`/powerbank/${id}/status`, null, { params: { status } })
export const searchPowerBankByCode = (code) => http.get('/powerbank/search', { params: { code } })

// 订单管理
export const getOrderList = (page, size) => http.get('/rental/list', { params: { page, size } })
export const searchOrders = (userId) => http.get('/rental/search', { params: { userId } })
