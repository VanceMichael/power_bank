<template>
  <div class="layout">
    <header class="top-bar">
      <div class="top-bar-inner">
        <div class="brand">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
          <span>PowerBank</span>
        </div>
        <nav class="nav-links">
          <router-link to="/home">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
            可用充电宝
          </router-link>
          <router-link to="/orders">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            我的订单
          </router-link>
          <router-link to="/return">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 102.13-9.36L1 10"/></svg>
            归还充电宝
          </router-link>
          <router-link to="/profile">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            个人中心
          </router-link>
          <a href="javascript:void(0)" class="nav-search-btn" @click="openSearchDialog">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            查询
          </a>
        </nav>
        <div class="user-area">
          <div class="user-avatar">{{ (userStore.userInfo?.realName || userStore.userInfo?.username || '?')[0] }}</div>
          <span class="greeting">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
          <button class="btn btn-ghost btn-sm" @click="handleLogout">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            退出
          </button>
        </div>
      </div>
    </header>
    <main class="page-container fade-up">
      <router-view />
    </main>

    <!-- 统一查询弹窗 -->
    <div v-if="showSearchDialog" class="modal-overlay" @click.self="showSearchDialog = false">
      <div class="modal-card" style="width:500px">
        <div class="modal-header">
          <h3>查询</h3>
          <button class="modal-close" @click="showSearchDialog = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="search-row">
            <select v-model="searchType" class="search-select">
              <option value="powerbank">按编号查询充电宝</option>
            </select>
            <input v-model="searchKeyword" class="search-input" :placeholder="searchPlaceholder" @keyup.enter="handleSearch" />
            <button class="btn btn-primary btn-sm" :disabled="searchLoading" @click="handleSearch">
              {{ searchLoading ? '查询中...' : '查询' }}
            </button>
          </div>
          <div v-if="searchExecuted">
            <div v-if="!hasSearchResult" class="no-result">无</div>
            <!-- 充电宝查询结果 -->
            <div v-else-if="searchType === 'powerbank' && pbSearchResult" class="result-item card">
              <div class="result-row"><span class="label">编号</span><span style="font-weight:600">{{ pbSearchResult.code }}</span></div>
              <div class="result-row">
                <span class="label">状态</span>
                <span :class="pbStatusClass(pbSearchResult.status)">{{ pbStatusText(pbSearchResult.status) }}</span>
              </div>
              <div class="result-row"><span class="label">费率</span><span style="color:var(--primary);font-weight:600">¥{{ pbSearchResult.hourlyRate }}/小时</span></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useToast } from '../stores/toast'
import { searchPowerBank } from '../api'

const router = useRouter()
const userStore = useUserStore()
const toast = useToast()

// 统一查询
const showSearchDialog = ref(false)
const searchType = ref('powerbank')
const searchKeyword = ref('')
const searchLoading = ref(false)
const searchExecuted = ref(false)
const pbSearchResult = ref(null)

const searchPlaceholder = computed(() => {
  return '输入充电宝编号，如 PB-001'
})

const hasSearchResult = computed(() => {
  if (searchType.value === 'powerbank') return !!pbSearchResult.value
  return false
})

function openSearchDialog() {
  showSearchDialog.value = true
  searchExecuted.value = false
  searchKeyword.value = ''
  pbSearchResult.value = null
}

function handleLogout() { userStore.logout(); toast.showToast('已退出登录', 'success'); router.push('/login') }

async function handleSearch() {
  if (!searchKeyword.value.trim()) { toast.showToast('请输入查询内容', 'warning'); return }
  searchLoading.value = true
  pbSearchResult.value = null
  
  try {
    if (searchType.value === 'powerbank') {
      const res = await searchPowerBank(searchKeyword.value.trim())
      pbSearchResult.value = res.data
    }
    searchExecuted.value = true
  } catch (e) {
    searchExecuted.value = true
  }
  searchLoading.value = false
}

function pbStatusText(s) { return ['空闲', '占用', '故障'][s] || '未知' }
function pbStatusClass(s) { return ['credit-good', 'credit-warn', 'credit-bad'][s] || '' }
</script>

<style lang="scss" scoped>
.top-bar {
  background: rgba(255,255,255,0.82); backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid var(--border-light); position: sticky; top: 0; z-index: 100;
}
.top-bar-inner {
  max-width: 1200px; margin: 0 auto; padding: 0 20px; height: 60px;
  display: flex; align-items: center; gap: 16px;
}
.brand {
  display: flex; align-items: center; gap: 8px; flex-shrink: 0;
  span { font-size: 17px; font-weight: 700; color: var(--primary); letter-spacing: -0.02em; }
}
.nav-links {
  display: flex; gap: 2px; flex: 1; min-width: 0;
  a {
    display: flex; align-items: center; gap: 5px; padding: 7px 10px; border-radius: var(--radius-sm);
    font-size: 13px; font-weight: 500; color: var(--text-secondary); transition: var(--transition);
    white-space: nowrap; flex-shrink: 0;
    &:hover { background: var(--primary-bg); color: var(--primary); }
    &.router-link-active { background: var(--primary-bg); color: var(--primary); }
  }
}
.user-area { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.user-avatar {
  width: 32px; height: 32px; border-radius: 50%; background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600;
}
.greeting { font-size: 13px; color: var(--text-secondary); white-space: nowrap; }

/* 查找弹窗 */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 1000;
  display: flex; align-items: center; justify-content: center;
  animation: fadeIn 0.2s ease;
}
.modal-card {
  background: var(--bg-card); border-radius: 12px; width: 560px; max-height: 80vh;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2); overflow: hidden; display: flex; flex-direction: column;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid var(--border-color);
  h3 { font-size: 17px; font-weight: 600; }
}
.modal-close {
  background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-secondary);
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  border-radius: 6px; transition: all 0.2s;
  &:hover { background: #f0f2f5; color: var(--text-primary); }
}
.modal-body { padding: 20px 24px; overflow-y: auto; flex: 1; }
.search-row { display: flex; gap: 10px; margin-bottom: 16px; }
.search-select {
  width: 140px; padding: 9px 12px; border: 1px solid var(--border-color); border-radius: 8px;
  font-size: 14px; background: #fff; cursor: pointer; outline: none;
  appearance: none; -webkit-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23909399' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat; background-position: right 10px center; padding-right: 28px;
  &:focus { border-color: var(--primary); }
}
.search-input {
  flex: 1; padding: 9px 14px; border: 1px solid var(--border-color); border-radius: 8px;
  font-size: 14px; outline: none;
  &:focus { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(64,158,255,0.12); }
}
.no-result { text-align: center; padding: 40px 0; font-size: 16px; color: var(--text-secondary); font-weight: 500; }
.result-list { display: flex; flex-direction: column; gap: 10px; }
.result-item { padding: 16px; }
.result-row {
  display: flex; justify-content: space-between; font-size: 14px; padding: 4px 0;
  .label { color: var(--text-secondary); }
}
.credit-good { color: var(--success); font-weight: 600; }
.credit-warn { color: var(--warning); font-weight: 600; }
.credit-bad { color: var(--danger); font-weight: 600; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>
