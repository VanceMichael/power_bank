<template>
  <div>
    <h2 class="section-title">归还充电宝</h2>
    <div v-if="loading" class="order-list"><div v-for="i in 2" :key="i" class="skeleton" style="height:100px;margin-bottom:12px"></div></div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 102.13-9.36L1 10"/></svg>
      <p>无</p>
    </div>
    <div v-else class="order-list">
      <div v-for="(order, idx) in orders" :key="order.id" class="return-card card" :style="{ animationDelay: idx * 0.06 + 's' }">
        <div class="return-left">
          <div class="return-icon-wrap">
            <span class="pulse-ring"></span>
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
          </div>
          <div class="return-info">
            <div class="pb-code">{{ order.powerBankCode }}</div>
            <div class="rent-meta">
              <span>
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                {{ formatTime(order.rentalTime) }}
              </span>
              <span class="duration">已使用 {{ calcDuration(order.rentalTime) }}</span>
            </div>
          </div>
        </div>
        <button class="btn btn-warning btn-sm" :disabled="returning" @click="handleReturn(order.id)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 102.13-9.36L1 10"/></svg>
          {{ returning === order.id ? '归还中...' : '归还' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCurrentOrders, returnPowerBank } from '../api'
import { useToast } from '../stores/toast'
import { useConfirm } from '../stores/confirm'
const toast = useToast()
const confirmDialog = useConfirm()
const orders = ref([])
const loading = ref(true)
const returning = ref(null)

async function loadData() {
  loading.value = true
  try { const res = await getCurrentOrders(); orders.value = res.data } catch (e) { console.error('加载当前订单失败:', e) }
  loading.value = false
}
async function handleReturn(id) {
  const yes = await confirmDialog.show('确认归还', '确认归还该充电宝？')
  if (!yes) return
  returning.value = id
  try { const res = await returnPowerBank(id); toast.showToast(`归还成功，费用 ¥${res.data.totalFee}`, 'success'); loadData() } catch (e) { console.error('归还失败:', e) }
  returning.value = null
}
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }
function calcDuration(rentalTime) {
  if (!rentalTime) return '-'
  const diff = Math.floor((new Date() - new Date(rentalTime)) / 60000)
  if (diff < 60) return `${diff}分钟`
  return `${Math.floor(diff / 60)}小时${diff % 60}分钟`
}
onMounted(loadData)
</script>

<style lang="scss" scoped>
.order-list { display: flex; flex-direction: column; gap: 12px; }
.return-card {
  display: flex; align-items: center; justify-content: space-between; animation: fadeUp 0.4s ease-out both;
  &:hover { transform: translateY(-2px); }
}
.return-left { display: flex; align-items: center; gap: 16px; }
.return-icon-wrap {
  position: relative; width: 52px; height: 52px; border-radius: 14px; background: var(--primary-bg);
  display: flex; align-items: center; justify-content: center;
}
.pulse-ring {
  position: absolute; inset: -4px; border-radius: 18px; border: 2px solid var(--primary);
  opacity: 0; animation: pulseRing 2s ease-out infinite;
}
@keyframes pulseRing { 0% { opacity: 0.6; transform: scale(0.9); } 100% { opacity: 0; transform: scale(1.15); } }
.pb-code { font-size: 16px; font-weight: 700; color: var(--text-primary); }
.rent-meta {
  display: flex; align-items: center; gap: 14px; margin-top: 4px; font-size: 13px; color: var(--text-secondary);
  span { display: flex; align-items: center; gap: 4px; }
  .duration { color: var(--primary); font-weight: 600; }
}
.empty-state { text-align: center; padding: 80px 0; color: var(--text-muted); p { margin-top: 12px; font-size: 15px; } }
</style>
