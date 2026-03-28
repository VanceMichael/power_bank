import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useToast = defineStore('toast', () => {
  const show = ref(false)
  const message = ref('')
  const type = ref('success')
  let timer = null

  function showToast(msg, t = 'success', duration = 2500) {
    if (timer) clearTimeout(timer)
    message.value = msg
    type.value = t
    show.value = true
    timer = setTimeout(() => { show.value = false }, duration)
  }

  return { show, message, type, showToast }
})
