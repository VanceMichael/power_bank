<template>
  <div>
    <div class="page-header" style="display:flex;justify-content:space-between;align-items:flex-start">
      <div>
        <h2>充电宝管理</h2>
        <p class="page-desc">管理所有充电宝设备信息和状态</p>
      </div>
      <div style="display:flex;gap:10px">
        <el-button @click="openPbSearchDialog">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          按编号查询
        </el-button>
        <el-button type="primary" @click="showAddDialog">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          新增充电宝
        </el-button>
      </div>
    </div>
    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="hourlyRate" label="费率(元/时)" width="110">
          <template #default="{ row }">¥{{ row.hourlyRate }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:3px"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              编辑
            </el-button>
            <el-button size="small" :type="row.status === 2 ? 'success' : 'warning'" @click="toggleStatus(row)">
              {{ row.status === 2 ? '恢复空闲' : row.status === 0 ? '标记故障' : '标记空闲' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑充电宝' : '新增充电宝'" width="440px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="90px">
        <el-form-item label="编号" prop="code"><el-input v-model="form.code" placeholder="如 PB-009" /></el-form-item>
        <el-form-item label="费率(元/时)" prop="hourlyRate"><el-input-number v-model="form.hourlyRate" :min="0.01" :step="0.5" :precision="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 按编号查询弹窗 -->
    <el-dialog v-model="pbSearchVisible" title="按编号查询充电宝" width="420px">
      <div style="display:flex;gap:10px;margin-bottom:20px">
        <el-input v-model="pbSearchCode" placeholder="输入充电宝编号，如 PB-001" clearable @keyup.enter="handlePbSearch" />
        <el-button type="primary" :loading="pbSearchLoading" @click="handlePbSearch">查询</el-button>
      </div>
      <div v-if="pbSearchExecuted">
        <div v-if="!pbSearchResult" style="text-align:center;padding:30px 0;font-size:16px;color:#909399;font-weight:500">无</div>
        <el-descriptions v-else :column="1" border>
          <el-descriptions-item label="编号">{{ pbSearchResult.code }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(pbSearchResult.status)" size="small">{{ statusText(pbSearchResult.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="费率">¥{{ pbSearchResult.hourlyRate }}/小时</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPowerBankList, addPowerBank, updatePowerBank, updatePowerBankStatus, searchPowerBankByCode } from '../api'
const list = ref([]); const loading = ref(false); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const dialogVisible = ref(false); const editingId = ref(null); const submitting = ref(false); const formRef = ref(null)
const form = ref({ code: '', hourlyRate: 2.00 })
const formRules = { code: [{ required: true, message: '请输入编号', trigger: 'blur' }], hourlyRate: [{ required: true, message: '请输入费率', trigger: 'blur' }] }

// 按编号查询
const pbSearchVisible = ref(false)
const pbSearchCode = ref('')
const pbSearchResult = ref(null)
const pbSearchLoading = ref(false)
const pbSearchExecuted = ref(false)

function openPbSearchDialog() { pbSearchCode.value = ''; pbSearchResult.value = null; pbSearchExecuted.value = false; pbSearchVisible.value = true }
async function handlePbSearch() {
  if (!pbSearchCode.value) { ElMessage.warning('请输入充电宝编号'); return }
  pbSearchLoading.value = true; pbSearchResult.value = null
  try { const res = await searchPowerBankByCode(pbSearchCode.value); pbSearchResult.value = res.data; pbSearchExecuted.value = true }
  catch (e) { pbSearchResult.value = null; pbSearchExecuted.value = true; console.error('查询充电宝失败:', e) }
  pbSearchLoading.value = false
}
function statusText(s) { return ['空闲', '占用', '故障'][s] || '未知' }
function statusType(s) { return ['success', 'primary', 'danger'][s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }
async function loadData() { loading.value = true; try { const res = await getPowerBankList(page.value, pageSize.value); list.value = res.data.records; total.value = res.data.total } catch (e) { console.error('加载充电宝列表失败:', e) } loading.value = false }
function showAddDialog() { editingId.value = null; form.value = { code: '', hourlyRate: 2.00 }; dialogVisible.value = true }
function showEditDialog(row) { if (row.status === 1) { ElMessage.warning('占用中的充电宝不可编辑'); return } editingId.value = row.id; form.value = { code: row.code, hourlyRate: Number(row.hourlyRate) }; dialogVisible.value = true }
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try { if (editingId.value) { await updatePowerBank(editingId.value, form.value); ElMessage.success('更新成功') } else { await addPowerBank(form.value); ElMessage.success('新增成功') } dialogVisible.value = false; loadData() } catch (e) { console.error('保存充电宝失败:', e) }
  submitting.value = false
}
async function toggleStatus(row) { if (row.status === 1) { ElMessage.warning('占用中的充电宝不可变更状态'); return } try { await updatePowerBankStatus(row.id, row.status === 0 ? 2 : 0); ElMessage.success('状态更新成功'); loadData() } catch (e) { console.error('更新状态失败:', e) } }
onMounted(loadData)
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1E1E2E; }
.page-desc { font-size: 14px; color: #8E8EA0; margin-top: 4px; }
</style>
