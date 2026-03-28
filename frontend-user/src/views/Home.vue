<template>
  <div>
    <h2 class="section-title">可用充电宝</h2>
    <div class="identity-bar">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      <span>当前身份：<strong>用户</strong></span>
      <span class="identity-sep">|</span>
      <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
    </div>
    <div v-if="loading" class="grid-skeleton">
      <div v-for="i in 6" :key="i" class="skeleton-card"><div class="skeleton" style="height:100%;border-radius:12px"></div></div>
    </div>
    <div v-else-if="list.length === 0" class="empty-state">
      <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.2"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
      <p>无</p>
    </div>
    <div v-else class="pb-grid">
      <div v-for="(item, idx) in list" :key="item.id" class="pb-card card" :style="{ animationDelay: idx * 0.06 + 's' }">
        <div class="pb-top">
          <div class="pb-icon-wrap">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
          </div>
          <div class="pb-status"><span class="status-dot"></span>空闲</div>
        </div>
        <div class="pb-code">{{ item.code }}</div>
        <div class="pb-rate">
          <span class="rate-value">¥{{ item.hourlyRate }}</span>
          <span class="rate-unit">/小时</span>
        </div>
        <button class="btn btn-primary btn-block btn-sm" :disabled="renting" @click="handleRent(item)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
          {{ renting === item.id ? '租赁中...' : '立即租赁' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAvailablePowerBanks, rentPowerBank } from '../api'
import { useToast } from '../stores/toast'
import { useConfirm } from '../stores/confirm'
import { useUserStore } from '../stores/user'
const toast = useToast()
const confirmDialog = useConfirm()
const userStore = useUserStore()
const list = ref([])
const loading = ref(true)
const renting = ref(null)

async function loadData() {
  loading.value = true
  try { const res = await getAvailablePowerBanks(); list.value = res.data } catch (e) { console.error('加载充电宝列表失败:', e) }
  loading.value = false
}

async function handleRent(item) {
  const yes = await confirmDialog.show('确认租赁', `确认租赁 ${item.code}？费用 ¥${item.hourlyRate}/小时，押金 ¥99.00`)
  if (!yes) return
  renting.value = item.id
  try { await rentPowerBank(item.id); toast.showToast('租赁成功', 'success'); loadData() } catch (e) { console.error('租赁失败:', e) }
  renting.value = null
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.pb-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.pb-card {
  padding: 20px; text-align: center; animation: fadeUp 0.4s ease-out both;
  &:hover { transform: translateY(-3px); box-shadow: var(--shadow-lg); }
}
.pb-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.pb-icon-wrap {
  width: 48px; height: 48px; border-radius: 12px; background: var(--primary-bg);
  display: flex; align-items: center; justify-content: center;
}
.pb-status {
  display: flex; align-items: center; gap: 5px; font-size: 12px; color: var(--success); font-weight: 500;
  .status-dot { width: 7px; height: 7px; border-radius: 50%; background: var(--success); animation: pulse 2s infinite; }
}
.pb-code { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.pb-rate { margin-bottom: 16px; .rate-value { font-size: 22px; font-weight: 700; color: var(--primary); } .rate-unit { font-size: 13px; color: var(--text-secondary); } }
.grid-skeleton { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.skeleton-card { height: 200px; }
.empty-state { text-align: center; padding: 80px 0; color: var(--text-muted); p { margin-top: 12px; font-size: 15px; } }
.identity-bar {
  display: flex; align-items: center; gap: 8px; margin-bottom: 20px;
  padding: 10px 16px; background: var(--primary-bg); border-radius: var(--radius-sm);
  font-size: 13px; color: var(--text-regular);
  strong { color: var(--primary); font-weight: 600; }
  .identity-sep { color: var(--border-color); }
}
</style>
