import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useConfirm = defineStore('confirm', () => {
  const visible = ref(false)
  const title = ref('')
  const message = ref('')
  let _resolve = null

  function show(t, msg) {
    title.value = t
    message.value = msg
    visible.value = true
    return new Promise(resolve => { _resolve = resolve })
  }

  function ok() { visible.value = false; _resolve?.(true); _resolve = null }
  function cancel() { visible.value = false; _resolve?.(false); _resolve = null }

  return { visible, title, message, show, ok, cancel }
})
