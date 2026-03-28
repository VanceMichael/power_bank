import http from './request'

// 认证
export const login = (data) => http.post('/auth/login', data)
export const register = (data) => http.post('/auth/register', data)

// 用户
export const getUserInfo = () => http.get('/user/info')
export const updateProfile = (data) => http.put('/user/profile', data)
export const updatePassword = (data) => http.put('/user/password', data)

// 充电宝
export const getAvailablePowerBanks = () => http.get('/powerbank/available')
export const searchPowerBank = (code) => http.get('/powerbank/search', { params: { code } })

// 租赁
export const rentPowerBank = (powerBankId) => http.post('/rental/rent', null, { params: { powerBankId } })
export const returnPowerBank = (id) => http.put(`/rental/return/${id}`)
export const payOrder = (id) => http.put(`/rental/pay/${id}`)
export const getMyOrders = () => http.get('/rental/my-orders')
export const getCurrentOrders = () => http.get('/rental/current')
