<template>
  <div>
    <h2 class="section-title">个人中心</h2>
    <div class="profile-grid">
      <div class="card profile-card">
        <div class="profile-avatar">{{ (userInfo?.realName || '?')[0] }}</div>
        <div class="profile-name">{{ userInfo?.realName || '-' }}</div>
        <div class="profile-username">@{{ userInfo?.username }}</div>
        <div class="credit-badge" :class="creditClass">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
          信誉分 {{ userInfo?.creditScore || 0 }}
        </div>
      </div>
      <div class="form-area">
        <div class="card">
          <h3 class="card-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            基本信息
          </h3>
          <form @submit.prevent="handleUpdateProfile">
            <div class="input-group"><label>用户名</label><input v-model="profileForm.username" required /></div>
            <div class="input-group"><label>真实姓名</label><input v-model="profileForm.realName" required /></div>
            <div class="input-group"><label>身份证号</label><input v-model="profileForm.idCard" maxlength="18" required /></div>
            <div class="input-group"><label>手机号</label><input v-model="profileForm.phone" maxlength="11" placeholder="请输入手机号" /></div>
            <button type="submit" class="btn btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存修改' }}</button>
          </form>
        </div>
        <div class="card">
          <h3 class="card-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--warning)" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
            修改密码
          </h3>
          <form @submit.prevent="handleUpdatePassword">
            <div class="input-group"><label>原密码</label><input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" required /></div>
            <div class="input-group"><label>新密码</label><input v-model="pwdForm.newPassword" type="password" placeholder="6-30位字符" required /></div>
            <button type="submit" class="btn btn-warning" :disabled="changingPwd">{{ changingPwd ? '修改中...' : '修改密码' }}</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo, updateProfile, updatePassword } from '../api'
import { useUserStore } from '../stores/user'
import { useToast } from '../stores/toast'
const userStore = useUserStore()
const toast = useToast()
const userInfo = ref(null)
const profileForm = ref({ username: '', realName: '', idCard: '', phone: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const saving = ref(false)
const changingPwd = ref(false)
const creditClass = computed(() => {
  if (!userInfo.value) return ''
  if (userInfo.value.creditScore >= 80) return 'credit-good'
  if (userInfo.value.creditScore >= 60) return 'credit-warn'
  return 'credit-bad'
})
async function loadInfo() {
  try {
    const res = await getUserInfo(); userInfo.value = res.data
    profileForm.value = { username: res.data.username, realName: res.data.realName, idCard: res.data.idCard, phone: res.data.phone || '' }
  } catch (e) { console.error('加载用户信息失败:', e) }
}
async function handleUpdateProfile() {
  if (profileForm.value.phone && !/^1[3-9]\d{9}$/.test(profileForm.value.phone)) { toast.showToast('手机号格式不正确', 'error'); return }
  saving.value = true
  try { await updateProfile(profileForm.value); toast.showToast('信息更新成功', 'success'); userStore.updateInfo({ username: profileForm.value.username, realName: profileForm.value.realName }); loadInfo() } catch (e) { console.error('更新信息失败:', e) }
  saving.value = false
}
async function handleUpdatePassword() {
  if (pwdForm.value.newPassword.length < 6) { toast.showToast('新密码至少6位', 'error'); return }
  changingPwd.value = true
  try { await updatePassword(pwdForm.value); toast.showToast('密码修改成功', 'success'); pwdForm.value = { oldPassword: '', newPassword: '' } } catch (e) { console.error('修改密码失败:', e) }
  changingPwd.value = false
}
onMounted(loadInfo)
</script>

<style lang="scss" scoped>
.profile-grid { display: grid; grid-template-columns: 240px 1fr; gap: 20px; align-items: start; }
.profile-card { text-align: center; padding: 32px 20px; }
.profile-avatar {
  width: 72px; height: 72px; border-radius: 50%; margin: 0 auto 14px;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: #fff; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: 700;
}
.profile-name { font-size: 18px; font-weight: 700; color: var(--text-primary); }
.profile-username { font-size: 13px; color: var(--text-secondary); margin-top: 2px; }
.credit-badge {
  display: inline-flex; align-items: center; gap: 6px; margin-top: 16px;
  padding: 6px 14px; border-radius: 20px; font-size: 14px; font-weight: 600;
  &.credit-good { background: var(--success-bg); color: var(--success); }
  &.credit-warn { background: var(--warning-bg); color: var(--warning); }
  &.credit-bad { background: var(--danger-bg); color: var(--danger); }
}
.form-area { display: flex; flex-direction: column; gap: 16px; }
.card-title {
  font-size: 16px; font-weight: 600; margin-bottom: 18px; padding-bottom: 14px;
  border-bottom: 1px solid var(--border-light); display: flex; align-items: center; gap: 8px;
}
</style>
