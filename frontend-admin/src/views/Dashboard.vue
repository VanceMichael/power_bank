<template>
  <div>
    <div class="page-header">
      <h2>系统概览</h2>
      <p class="page-desc">充电宝租赁平台运营数据一览</p>
    </div>
    <div class="stats-grid">
      <div v-for="(item, idx) in stats" :key="item.label" class="stat-card" :class="'stat-' + idx" :style="{ animationDelay: idx * 0.08 + 's' }">
        <div class="stat-icon" v-html="item.icon"></div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
    <el-card shadow="hover" style="margin-top: 24px">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px;font-weight:600">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          最近订单
        </div>
      </template>
      <el-table :data="recentOrders" stripe style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="username" label="用户" width="100" />
        <el-table-column prop="powerBankCode" label="充电宝" width="100" />
        <el-table-column prop="rentalTime" label="租赁时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.rentalTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'primary' : row.paymentStatus === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '进行中' : row.paymentStatus === 1 ? '已完成' : '待支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalFee" label="费用" width="100">
          <template #default="{ row }">{{ row.totalFee != null ? `¥${row.totalFee}` : '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, getPowerBankList, getOrderList, getAvailableCount } from '../api'

const stats = ref([
  { icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>', label: '注册用户', value: 0 },
  { icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>', label: '充电宝总数', value: 0 },
  { icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>', label: '空闲充电宝', value: 0 },
  { icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>', label: '总订单数', value: 0 }
])
const recentOrders = ref([])
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }
onMounted(async () => {
  try {
    const [users, pbs, orders, availableCount] = await Promise.all([getUserList(1, 1), getPowerBankList(1, 1), getOrderList(1, 5), getAvailableCount()])
    stats.value[0].value = users.data.total
    stats.value[1].value = pbs.data.total
    stats.value[2].value = availableCount.data
    stats.value[3].value = orders.data.total
    recentOrders.value = orders.data.records
  } catch (e) { console.error('加载仪表盘数据失败:', e) }
})
</script>

<style scoped>
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1E1E2E; }
.page-desc { font-size: 14px; color: #8E8EA0; margin-top: 4px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card {
  padding: 22px; border-radius: 12px; display: flex; align-items: center; gap: 16px;
  animation: fadeUp 0.4s ease-out both; transition: all 0.2s ease;
  border: 1px solid rgba(255,255,255,0.1);
}
.stat-card:hover { transform: translateY(-2px); }
.stat-0 { background: linear-gradient(135deg, #6366F1, #818CF8); color: #fff; }
.stat-1 { background: linear-gradient(135deg, #10B981, #34D399); color: #fff; }
.stat-2 { background: linear-gradient(135deg, #F59E0B, #FBBF24); color: #fff; }
.stat-3 { background: linear-gradient(135deg, #EF4444, #F87171); color: #fff; }
.stat-icon { width: 48px; height: 48px; border-radius: 12px; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.85; margin-top: 2px; }
</style>
