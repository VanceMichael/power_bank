<template>
  <div class="login-page">
    <div class="login-bg-decor">
      <div class="decor-circle c1"></div>
      <div class="decor-circle c2"></div>
      <div class="decor-circle c3"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
        </div>
        <h1>PowerBank Admin</h1>
        <p>充电宝租赁管理后台</p>
      </div>

      <!-- 登录表单 -->
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员账号" size="large">
            <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin">
            <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" style="width:100%;height:44px;font-size:15px" @click="handleLogin">登 录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await login(form.value)
    // 只允许管理员登录
    if (res.data.role !== 1) {
      ElMessage.warning('该账号不是管理员，请使用用户端登录')
      loading.value = false
      return
    }
    userStore.setLogin(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    console.error('登录失败:', e)
  }
  loading.value = false
}
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(145deg, #0F0F1A 0%, #1A1A2E 40%, #16213E 100%); position: relative; overflow: hidden;
}
.login-bg-decor { position: absolute; inset: 0; pointer-events: none; }
.decor-circle { position: absolute; border-radius: 50%; }
.c1 { width: 600px; height: 600px; background: radial-gradient(circle, rgba(99,102,241,0.12) 0%, transparent 70%); top: -200px; right: -100px; }
.c2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(129,140,248,0.08) 0%, transparent 70%); bottom: -100px; left: -80px; }
.c3 { width: 200px; height: 200px; background: radial-gradient(circle, rgba(99,102,241,0.1) 0%, transparent 70%); top: 40%; left: 20%; }
.login-card {
  background: rgba(255,255,255,0.97); border-radius: 16px; padding: 44px 40px; width: 420px;
  box-shadow: 0 24px 80px rgba(0,0,0,0.25); position: relative; z-index: 1;
}
.login-header { text-align: center; margin-bottom: 32px; }
.logo-icon {
  width: 56px; height: 56px; border-radius: 14px; margin: 0 auto 16px;
  background: linear-gradient(135deg, #4F46E5, #818CF8);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 16px rgba(99,102,241,0.3);
}
.login-header h1 { font-size: 22px; color: #1E1E2E; font-weight: 700; }
.login-header p { font-size: 13px; color: #8E8EA0; margin-top: 4px; }
</style>
