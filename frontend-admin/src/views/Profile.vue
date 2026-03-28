<template>
  <div>
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="page-desc">管理您的账户信息和安全设置</p>
    </div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display:flex;align-items:center;gap:8px;font-weight:600">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              基本信息
            </div>
          </template>
          <el-form :model="profileForm" :rules="profileRules" ref="profileRef" label-width="80px">
            <el-form-item label="用户名" prop="username"><el-input v-model="profileForm.username" /></el-form-item>
            <el-form-item label="真实姓名" prop="realName"><el-input v-model="profileForm.realName" /></el-form-item>
            <el-form-item label="身份证号" prop="idCard"><el-input v-model="profileForm.idCard" maxlength="18" /></el-form-item>
            <el-form-item label="手机号" prop="phone"><el-input v-model="profileForm.phone" maxlength="11" placeholder="请输入手机号" /></el-form-item>
            <el-form-item><el-button type="primary" :loading="saving" @click="handleSaveProfile">保存修改</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display:flex;align-items:center;gap:8px;font-weight:600">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
              修改密码
            </div>
          </template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-width="80px">
            <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item><el-button type="warning" :loading="changingPwd" @click="handleChangePwd">修改密码</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateProfile, updatePassword } from '../api'
import { useUserStore } from '../stores/user'
const userStore = useUserStore()
const profileRef = ref(null); const pwdRef = ref(null); const saving = ref(false); const changingPwd = ref(false)
const profileForm = ref({ username: '', realName: '', idCard: '', phone: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const profileRules = { username: [{ required: true, message: '请输入用户名', trigger: 'blur' }], realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }], idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }], phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }] }
const pwdRules = { oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }], newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }] }
async function loadInfo() { try { const res = await getUserInfo(); profileForm.value = { username: res.data.username, realName: res.data.realName, idCard: res.data.idCard, phone: res.data.phone || '' } } catch (e) { console.error('加载用户信息失败:', e) } }
async function handleSaveProfile() { await profileRef.value?.validate(); saving.value = true; try { await updateProfile(profileForm.value); ElMessage.success('信息更新成功'); userStore.updateInfo({ username: profileForm.value.username, realName: profileForm.value.realName }) } catch (e) { console.error('保存信息失败:', e) } saving.value = false }
async function handleChangePwd() { await pwdRef.value?.validate(); changingPwd.value = true; try { await updatePassword(pwdForm.value); ElMessage.success('密码修改成功'); pwdForm.value = { oldPassword: '', newPassword: '' } } catch (e) { console.error('修改密码失败:', e) } changingPwd.value = false }
onMounted(loadInfo)
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1E1E2E; }
.page-desc { font-size: 14px; color: #8E8EA0; margin-top: 4px; }
</style>
