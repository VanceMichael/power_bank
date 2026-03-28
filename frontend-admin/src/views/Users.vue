<template>
  <div>
    <div class="page-header" style="display:flex;justify-content:space-between;align-items:flex-start">
      <div>
        <h2>用户管理</h2>
        <p class="page-desc">查看和管理所有注册用户信息</p>
      </div>
      <div style="display:flex;gap:10px">
        <el-button type="primary" @click="openAddDialog">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          新增用户
        </el-button>
        <el-button @click="openSearchDialog">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          查找用户
        </el-button>
      </div>
    </div>

    <!-- 用户列表 -->
    <el-card shadow="hover">
      <el-table :data="users" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="realName" label="真实姓名" width="90" />
        <el-table-column prop="idCard" label="身份证号" min-width="170" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="creditScore" label="信誉分" width="80">
          <template #default="{ row }">
            <el-tag :type="row.creditScore >= 80 ? 'success' : row.creditScore >= 60 ? 'warning' : 'danger'" size="small">{{ row.creditScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:3px"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 查找弹窗 -->
    <el-dialog v-model="searchDialogVisible" title="查找用户" width="600px">
      <div style="display:flex;gap:12px;margin-bottom:20px">
        <el-select v-model="searchField" placeholder="选择查找模块" style="width:160px">
          <el-option label="姓名" value="realName" />
          <el-option label="身份证号" value="idCard" />
          <el-option label="手机号" value="phone" />
          <el-option label="用户名" value="username" />
        </el-select>
        <el-input v-model="searchKeyword" placeholder="输入搜索内容" clearable style="flex:1" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch" :loading="searchLoading">搜索</el-button>
      </div>
      <div v-if="searchExecuted">
        <div v-if="searchResults.length === 0" class="no-result">无</div>
        <el-table v-else :data="searchResults" stripe style="width:100%" max-height="360">
          <el-table-column prop="username" label="用户名" width="100" />
          <el-table-column prop="realName" label="姓名" width="80" />
          <el-table-column prop="idCard" label="身份证号" width="180" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="creditScore" label="信誉分">
            <template #default="{ row }">
              <el-tag :type="row.creditScore >= 80 ? 'success' : row.creditScore >= 60 ? 'warning' : 'danger'" size="small">{{ row.creditScore }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog v-model="editDialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="editForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="editForm.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号（选填）" maxlength="11" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="信誉分" prop="creditScore">
          <el-input-number v-model="editForm.creditScore" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, searchUsers, createUser, updateUser, deleteUser } from '../api'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索弹窗
const searchDialogVisible = ref(false)
const searchField = ref('realName')
const searchKeyword = ref('')
const searchResults = ref([])
const searchLoading = ref(false)
const searchExecuted = ref(false)

// 编辑弹窗
const editDialogVisible = ref(false)
const isEdit = ref(false)
const editForm = ref({ username: '', password: '', realName: '', idCard: '', phone: '', creditScore: 100 })
const editFormRef = ref(null)
const editLoading = ref(false)
const editingId = ref(null)

const editRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '3-20位字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }, { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(page.value, pageSize.value)
    users.value = res.data.records
    total.value = res.data.total
  } catch (e) { console.error('加载用户列表失败:', e) }
  loading.value = false
}

function openSearchDialog() {
  searchField.value = 'realName'
  searchKeyword.value = ''
  searchResults.value = []
  searchExecuted.value = false
  searchDialogVisible.value = true
}

async function handleSearch() {
  if (!searchKeyword.value) { ElMessage.warning('请输入搜索内容'); return }
  searchLoading.value = true
  try {
    const res = await searchUsers(searchField.value, searchKeyword.value, 1, 50)
    searchResults.value = res.data.records
    searchExecuted.value = true
  } catch (e) { console.error('搜索用户失败:', e) }
  searchLoading.value = false
}

function openAddDialog() {
  isEdit.value = false
  editingId.value = null
  editForm.value = { username: '', password: '', realName: '', idCard: '', phone: '', creditScore: 100 }
  editDialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editingId.value = row.id
  editForm.value = {
    username: row.username,
    password: '',
    realName: row.realName,
    idCard: row.idCard,
    phone: row.phone || '',
    creditScore: row.creditScore
  }
  editDialogVisible.value = true
}

async function handleSave() {
  await editFormRef.value?.validate()
  editLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser(editingId.value, editForm.value)
      ElMessage.success('修改成功')
    } else {
      await createUser(editForm.value)
      ElMessage.success('新增成功')
    }
    editDialogVisible.value = false
    loadData()
  } catch (e) { console.error('保存用户失败:', e) }
  editLoading.value = false
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '确认删除', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') { console.error('删除用户失败:', e) }
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1E1E2E; }
.page-desc { font-size: 14px; color: #8E8EA0; margin-top: 4px; }
.no-result { text-align: center; padding: 40px 0; font-size: 16px; color: #909399; font-weight: 500; }
</style>
