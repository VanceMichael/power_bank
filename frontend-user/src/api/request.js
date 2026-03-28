import axios from 'axios'
import { useToast } from '../stores/toast'
import router from '../router'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  res => {
    // 检查业务层面的 token 无效
    if (res.data.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      const toast = useToast()
      toast.showToast('登录已过期，请重新登录', 'warning')
      return Promise.reject(new Error(res.data.message))
    }
    if (res.data.code !== 200) {
      const toast = useToast()
      toast.showToast(res.data.message || '请求失败', 'error')
      return Promise.reject(new Error(res.data.message))
    }
    return res.data
  },
  err => {
    const toast = useToast()
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      toast.showToast('登录已过期，请重新登录', 'warning')
    } else {
      toast.showToast(err.response?.data?.message || '网络错误', 'error')
    }
    return Promise.reject(err)
  }
)

export default http
