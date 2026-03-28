<template>
  <div>
    <h2 class="section-title">我的订单</h2>
    <div v-if="loading" class="order-list"><div v-for="i in 3" :key="i" class="skeleton" style="height:120px;margin-bottom:12px"></div></div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
      <p>无</p>
    </div>
    <div v-else class="order-list">
      <div v-for="(order, idx) in orders" :key="order.id" class="order-card card"
           :class="{ 'is-paid': order.paymentStatus === 1, 'is-unpaid': order.paymentStatus === 0 && order.status !== 0, 'is-active': order.status === 0 }"
           :style="{ animationDelay: idx * 0.05 + 's' }">
        <div class="order-left-bar"></div>
        <div class="order-content">
          <div class="order-header">
            <div class="order-id-row">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
              <span class="order-id">#{{ order.id }}</span>
              <span class="order-pb">{{ order.powerBankCode }}</span>
            </div>
            <span class="status-tag" :class="statusClass(order)">{{ statusText(order) }}</span>
          </div>
          <div class="order-body">
            <div class="order-row"><span class="label">租赁时间</span><span>{{ formatTime(order.rentalTime) }}</span></div>
            <div v-if="order.returnTime" class="order-row"><span class="label">归还时间</span><span>{{ formatTime(order.returnTime) }}</span></div>
            <div v-if="order.returnTime" class="order-row"><span class="label">租赁时长</span><span class="duration">{{ calcDuration(order.rentalTime, order.returnTime) }}</span></div>
            <div class="order-row" v-if="order.totalFee != null"><span class="label">应付金额</span><span class="fee">¥{{ order.totalFee }}</span></div>
            <div class="order-row">
              <span class="label">支付状态</span>
              <span :class="order.paymentStatus === 1 ? 'text-success' : 'text-danger'">{{ order.paymentStatus === 1 ? '已支付' : '未支付' }}</span>
            </div>
            <div v-if="order.overtime === 1" class="order-row"><span class="label">超时</span><span class="text-danger">是</span></div>
          </div>
          <div v-if="order.paymentStatus === 0 && order.status !== 0" class="order-footer">
            <button class="btn btn-danger btn-sm" :disabled="paying" @click="handlePay(order.id)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
              {{ paying === order.id ? '支付中...' : '去支付' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOrders, payOrder } from '../api'
import { useToast } from '../stores/toast'
const toast = useToast()
const orders = ref([])
const loading = ref(true)
const paying = ref(null)

async function loadOrders() {
  loading.value = true
  try { const res = await getMyOrders(); orders.value = res.data } catch (e) { console.error('加载订单失败:', e) }
  loading.value = false
}
async function handlePay(id) {
  paying.value = id
  try { await payOrder(id); toast.showToast('支付成功', 'success'); loadOrders() } catch (e) { console.error('支付失败:', e) }
  paying.value = null
}
function statusText(o) { if (o.status === 0) return '进行中'; if (o.status === 2) return '超时'; return o.paymentStatus === 1 ? '已完成' : '待支付' }
function statusClass(o) { if (o.status === 0) return 'tag-active'; if (o.paymentStatus === 1) return 'tag-done'; return 'tag-pending' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }
function calcDuration(start, end) {
  if (!start || !end) return '-'
  const s = new Date(start.replace('T', ' '))
  const e = new Date(end.replace('T', ' '))
  const diff = Math.max(0, e - s)
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  if (hours > 0) return `${hours}小时${minutes > 0 ? minutes + '分钟' : ''}`
  return `${minutes}分钟`
}
onMounted(loadOrders)
</script>

<style lang="scss" scoped>
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-card {
  display: flex; padding: 0; overflow: hidden; animation: fadeUp 0.4s ease-out both;
  &:hover { transform: translateY(-2px); }
}
.order-left-bar { width: 4px; flex-shrink: 0; background: var(--border-color); border-radius: 4px 0 0 4px; }
.is-paid { background: rgba(16,185,129,0.06); border: 1px solid rgba(16,185,129,0.18); }
.is-paid .order-left-bar { background: var(--success); }
.is-unpaid { background: rgba(239,68,68,0.06); border: 1px solid rgba(239,68,68,0.18); }
.is-unpaid .order-left-bar { background: var(--danger); }
.is-active { background: rgba(99,102,241,0.05); border: 1px solid rgba(99,102,241,0.15); }
.is-active .order-left-bar { background: var(--primary); }
.order-content { flex: 1; padding: 18px 20px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.order-id-row { display: flex; align-items: center; gap: 8px; }
.order-id { font-weight: 700; font-size: 15px; color: var(--text-primary); }
.order-pb { font-size: 13px; color: var(--text-secondary); background: var(--border-light); padding: 2px 8px; border-radius: 4px; }
.status-tag {
  font-size: 12px; padding: 3px 10px; border-radius: 20px; font-weight: 600;
  &.tag-active { background: var(--primary-bg); color: var(--primary); }
  &.tag-done { background: var(--success-bg); color: var(--success); }
  &.tag-pending { background: var(--danger-bg); color: var(--danger); }
}
.order-body { display: flex; flex-direction: column; gap: 6px; }
.order-row { display: flex; justify-content: space-between; font-size: 14px; .label { color: var(--text-secondary); } .fee { font-weight: 700; color: var(--danger); } }
.duration { font-weight: 600; color: var(--primary); }
.text-success { color: var(--success); font-weight: 600; }
.text-danger { color: var(--danger); font-weight: 600; }
.order-footer { margin-top: 14px; text-align: right; }
.empty-state { text-align: center; padding: 80px 0; color: var(--text-muted); p { margin-top: 12px; font-size: 15px; } }
</style>
