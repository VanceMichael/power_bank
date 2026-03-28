<template>
  <div class="login-page">
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="2"/><line x1="12" y1="10" x2="12" y2="14"/><line x1="10" y1="12" x2="14" y2="12"/><line x1="6" y1="6" x2="18" y2="6"/></svg>
        </div>
        <h1>PowerBank</h1>
        <p>智能充电宝租赁平台</p>
        <div class="brand-features">
          <div class="feature-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg><span>即租即用，随借随还</span></div>
          <div class="feature-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg><span>押金保障，安全可靠</span></div>
          <div class="feature-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><span>按时计费，透明公正</span></div>
        </div>
      </div>
    </div>
    <div class="login-form-area">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>{{ mode === 'login' ? '欢迎回来' : '创建账户' }}</h2>
          <p>{{ mode === 'login' ? '登录您的账户以继续' : '注册新账户开始使用' }}</p>
        </div>

        <div class="tab-switch">
          <button :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</button>
          <button :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</button>
        </div>

        <!-- 登录表单 -->
        <form v-if="mode === 'login'" @submit.prevent="handleLogin">
          <div class="input-group">
            <label>账户名</label>
            <input v-model="loginForm.username" placeholder="请输入账户名" required />
          </div>
          <div class="input-group">
            <label>密码</label>
            <input v-model="loginForm.password" type="password" placeholder="请输入密码" required />
          </div>
          <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </form>

        <!-- 注册表单 -->
        <form v-if="mode === 'register'" @submit.prevent="handleRegister">
          <div class="input-group"><label>账户名</label><input v-model="regForm.username" placeholder="3-20位字符" required /></div>
          <div class="input-group"><label>密码</label><input v-model="regForm.password" type="password" placeholder="6-30位字符" required @input="checkPasswordMatch" /></div>
          <div class="input-group" :class="{ 'has-error': pwdMismatch }">
            <label>确认密码</label>
            <input v-model="regForm.confirmPassword" type="password" placeholder="再次输入密码" required @input="checkPasswordMatch" />
            <span v-if="pwdMismatch" class="error-hint">两次输入的密码不一致</span>
          </div>
          <div class="input-group"><label>真实姓名</label><input v-model="regForm.realName" placeholder="请输入真实姓名" required /></div>
          <div class="input-group"><label>身份证号</label><input v-model="regForm.idCard" placeholder="18位身份证号" maxlength="18" required /></div>
          <div class="input-group"><label>手机号</label><input v-model="regForm.phone" placeholder="请输入手机号（选填）" maxlength="11" pattern="^1[3-9]\d{9}$" /></div>
          <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
            {{ loading ? '注册中...' : '注 册' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../api'
import { useUserStore } from '../stores/user'
import { useToast } from '../stores/toast'

const router = useRouter()
const userStore = useUserStore()
const toast = useToast()
const mode = ref('login')
const loading = ref(false)
const loginForm = ref({ username: '', password: '' })
const regForm = ref({ username: '', password: '', confirmPassword: '', realName: '', idCard: '', phone: '' })
const pwdMismatch = ref(false)

// 实时校验确认密码
function checkPasswordMatch() {
  pwdMismatch.value = regForm.value.confirmPassword !== '' && regForm.value.password !== regForm.value.confirmPassword
}

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(loginForm.value)
    // 只允许普通用户登录
    if (res.data.role === 1) {
      toast.showToast('该账号是管理员，请使用管理后台登录', 'warning')
      loading.value = false
      return
    }
    userStore.setLogin(res.data)
    toast.showToast('登录成功', 'success')
    router.push('/home')
  } catch (e) {
    console.error('登录失败:', e)
  }
  loading.value = false
}

async function handleRegister() {
  if (regForm.value.password !== regForm.value.confirmPassword) { toast.showToast('两次输入的密码不一致', 'error'); return }
  if (regForm.value.password.length < 6) { toast.showToast('密码至少6位字符', 'error'); return }
  if (regForm.value.username.length < 3 || regForm.value.username.length > 20) { toast.showToast('用户名需要3-20位字符', 'error'); return }
  if (!/^\d{17}[\dXx]$/.test(regForm.value.idCard)) { toast.showToast('身份证号格式不正确', 'error'); return }
  if (regForm.value.phone && !/^1[3-9]\d{9}$/.test(regForm.value.phone)) { toast.showToast('手机号格式不正确', 'error'); return }
  loading.value = true
  try {
    await register(regForm.value)
    toast.showToast('注册成功，请登录', 'success')
    mode.value = 'login'
    loginForm.value.username = regForm.value.username
  } catch (e) {
    console.error('注册失败:', e)
  }
  loading.value = false
}
</script>

<style lang="scss" scoped>
.login-page { min-height: 100vh; display: flex; }
.login-brand {
  flex: 0 0 44%; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(145deg, #4F46E5 0%, #6366F1 40%, #818CF8 100%);
  position: relative; overflow: hidden;
  &::before { content: ''; position: absolute; width: 500px; height: 500px; border-radius: 50%; background: rgba(255,255,255,0.05); top: -120px; right: -100px; }
  &::after { content: ''; position: absolute; width: 300px; height: 300px; border-radius: 50%; background: rgba(255,255,255,0.04); bottom: -80px; left: -60px; }
}
.brand-content { position: relative; z-index: 1; color: #fff; padding: 40px; }
.brand-icon { margin-bottom: 20px; opacity: 0.9; }
.brand-content h1 { font-size: 36px; font-weight: 700; letter-spacing: -0.02em; margin-bottom: 8px; }
.brand-content > p { font-size: 16px; opacity: 0.8; margin-bottom: 40px; }
.brand-features { display: flex; flex-direction: column; gap: 16px; }
.feature-item { display: flex; align-items: center; gap: 12px; font-size: 14px; opacity: 0.85; }
.login-form-area { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px; background: var(--bg-page); }
.form-wrapper { width: 100%; max-width: 400px; }
.form-header { margin-bottom: 24px; h2 { font-size: 26px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px; } p { font-size: 14px; color: var(--text-secondary); } }

.tab-switch {
  display: flex; background: #f0f2f5; border-radius: 8px; padding: 4px; margin-bottom: 24px;
  button {
    flex: 1; padding: 10px; border: none; border-radius: 6px; background: transparent;
    font-size: 14px; cursor: pointer; transition: all 0.25s; color: var(--text-secondary); font-family: var(--font); font-weight: 500;
    &.active { background: var(--bg-card); color: var(--primary); box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
  }
}
.btn-block { width: 100%; margin-top: 8px; }
.btn-lg { padding: 13px; font-size: 15px; }
.input-group.has-error input { border-color: var(--danger); }
.error-hint { color: var(--danger); font-size: 12px; margin-top: 4px; display: block; }
</style>
