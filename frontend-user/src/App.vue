<template>
  <div id="app-root">
    <router-view />
    <Transition name="toast">
      <div v-if="toast.show" :class="['toast-bar', toast.type]">
        <span class="toast-icon" v-html="toastIcon"></span>
        <span>{{ toast.message }}</span>
      </div>
    </Transition>
    <!-- 自定义确认弹窗 -->
    <Transition name="modal">
      <div v-if="confirmStore.visible" class="confirm-overlay" @click.self="confirmStore.cancel()">
        <div class="confirm-dialog">
          <div class="confirm-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          </div>
          <div class="confirm-title">{{ confirmStore.title }}</div>
          <div class="confirm-msg">{{ confirmStore.message }}</div>
          <div class="confirm-actions">
            <button class="btn btn-outline" @click="confirmStore.cancel()">取消</button>
            <button class="btn btn-primary" @click="confirmStore.ok()">确认</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useToast } from './stores/toast'
import { useConfirm } from './stores/confirm'
const toast = useToast()
const confirmStore = useConfirm()
const toastIcon = computed(() => {
  const icons = {
    success: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6L9 17l-5-5"/></svg>',
    error: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
    warning: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>'
  }
  return icons[toast.type] || icons.success
})
</script>

<style lang="scss">
.toast-bar {
  position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
  padding: 12px 24px; border-radius: 10px; font-size: 14px; font-weight: 500;
  z-index: 9999; display: flex; align-items: center; gap: 8px;
  backdrop-filter: blur(12px); font-family: var(--font);
  &.success { background: rgba(16,185,129,0.92); color: #fff; box-shadow: 0 4px 20px rgba(16,185,129,0.3); }
  &.error { background: rgba(239,68,68,0.92); color: #fff; box-shadow: 0 4px 20px rgba(239,68,68,0.3); }
  &.warning { background: rgba(245,158,11,0.92); color: #fff; box-shadow: 0 4px 20px rgba(245,158,11,0.3); }
}
.toast-icon { display: flex; align-items: center; }
.toast-enter-active { animation: toastIn 0.35s ease-out; }
.toast-leave-active { animation: toastOut 0.25s ease-in; }
@keyframes toastIn { from { opacity: 0; transform: translateX(-50%) translateY(-16px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }
@keyframes toastOut { from { opacity: 1; transform: translateX(-50%) translateY(0); } to { opacity: 0; transform: translateX(-50%) translateY(-16px); } }

/* 确认弹窗 */
.confirm-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px);
  z-index: 9998; display: flex; align-items: center; justify-content: center;
}
.confirm-dialog {
  background: var(--bg-card); border-radius: var(--radius-lg); padding: 32px; width: 380px;
  box-shadow: 0 24px 80px rgba(0,0,0,0.15); text-align: center;
}
.confirm-icon { margin-bottom: 14px; }
.confirm-title { font-size: 17px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; }
.confirm-msg { font-size: 14px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 24px; }
.confirm-actions { display: flex; gap: 10px; justify-content: center; }
.confirm-actions .btn { min-width: 90px; }
.modal-enter-active { animation: modalIn 0.25s ease-out; }
.modal-leave-active { animation: modalOut 0.2s ease-in; }
@keyframes modalIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes modalOut { from { opacity: 1; } to { opacity: 0; } }
.modal-enter-active .confirm-dialog { animation: dialogIn 0.25s ease-out; }
.modal-leave-active .confirm-dialog { animation: dialogOut 0.2s ease-in; }
@keyframes dialogIn { from { opacity: 0; transform: scale(0.92); } to { opacity: 1; transform: scale(1); } }
@keyframes dialogOut { from { opacity: 1; transform: scale(1); } to { opacity: 0; transform: scale(0.92); } }
</style>
