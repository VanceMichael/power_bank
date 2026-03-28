import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('adminUser', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_info') || 'null'))
  const token = ref(localStorage.getItem('admin_token') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setLogin(data) {
    token.value = data.token
    userInfo.value = { userId: data.userId, username: data.username, role: data.role, realName: data.realName }
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_info', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  function updateInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('admin_info', JSON.stringify(userInfo.value))
  }

  return { userInfo, token, isLoggedIn, setLogin, logout, updateInfo }
})
