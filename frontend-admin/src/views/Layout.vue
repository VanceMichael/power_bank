<template>
  <el-container style="min-height: 100vh">
    <el-aside width="240px" class="sidebar">
      <div class="sidebar-logo">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#818CF8" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
        <span>PowerBank</span>
      </div>
      <el-menu :default-active="route.path" router background-color="transparent" text-color="#94A3B8" active-text-color="#818CF8">
        <el-menu-item index="/dashboard">
          <template #title>
            <span class="menu-item-inner">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
              <span>首页概览</span>
            </span>
          </template>
        </el-menu-item>
        <el-menu-item index="/users">
          <template #title>
            <span class="menu-item-inner">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
              <span>用户管理</span>
            </span>
          </template>
        </el-menu-item>
        <el-menu-item index="/powerbanks">
          <template #title>
            <span class="menu-item-inner">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
              <span>充电宝管理</span>
            </span>
          </template>
        </el-menu-item>
        <el-menu-item index="/orders">
          <template #title>
            <span class="menu-item-inner">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              <span>订单管理</span>
            </span>
          </template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <template #title>
            <span class="menu-item-inner">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>
              <span>个人中心</span>
            </span>
          </template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="top-header">
        <span class="greeting">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          您好，{{ userStore.userInfo?.realName || userStore.userInfo?.username }} 管理员
        </span>
        <el-button text @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
          退出登录
        </el-button>
      </el-header>
      <el-main class="main-content fade-up">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
function handleLogout() { userStore.logout(); ElMessage.success('已退出'); router.push('/login') }
</script>

<style scoped>
.sidebar {
  background: linear-gradient(180deg, #0F0F1A 0%, #1A1A2E 100%);
  border-right: 1px solid rgba(255,255,255,0.06);
}
.sidebar-logo {
  height: 64px; display: flex; align-items: center; justify-content: center; gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.sidebar-logo span { font-size: 18px; font-weight: 700; color: #E2E8F0; letter-spacing: -0.02em; font-family: 'DM Sans', sans-serif; }
.menu-item-inner { display: flex; align-items: center; gap: 10px; }
.top-header {
  background: #fff; display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #F0F0F6; height: 60px !important; padding: 0 28px;
}
.greeting {
  font-size: 15px; color: #1E1E2E; font-weight: 500;
  display: flex; align-items: center; gap: 8px;
}
.main-content { background: #F8F9FC; padding: 28px !important; }
</style>
