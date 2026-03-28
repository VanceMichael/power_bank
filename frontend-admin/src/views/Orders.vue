<template>
  <div>
    <div class="page-header">
      <h2>订单管理</h2>
      <p class="page-desc">查看和搜索所有租赁订单记录</p>
    </div>
    <el-card shadow="hover" style="margin-bottom: 16px">
      <el-form :inline="true" @submit.prevent="handleSearch">
        <el-form-item label="用户ID">
          <el-input v-model="searchUserId" placeholder="输入用户ID" clearable style="width: 180px">
            <template #prefix><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="hover">
      <el-table :data="orders" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="username" label="用户" width="100" />
        <el-table-column prop="realName" label="姓名" width="80" />
        <el-table-column prop="powerBankCode" label="充电宝" width="100" />
        <el-table-column prop="rentalTime" label="租赁时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.rentalTime) }}</template>
        </el-table-column>
        <el-table-column prop="returnTime" label="归还时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.returnTime) }}</template>
        </el-table-column>
        <el-table-column prop="totalFee" label="费用" width="80">
          <template #default="{ row }">{{ row.totalFee != null ? `¥${row.totalFee}` : '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'primary' : row.paymentStatus === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '进行中' : row.paymentStatus === 1 ? '已完成' : '待支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="超时" width="70">
          <template #default="{ row }">
            <el-tag v-if="row.overtime === 1" type="danger" size="small">是</el-tag>
            <span v-else style="color:#B4B4C4">-</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!isSearching" style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, searchOrders } from '../api'
const orders = ref([]); const loading = ref(false); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const searchUserId = ref(''); const isSearching = ref(false)
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }
async function loadData() { loading.value = true; try { const res = await getOrderList(page.value, pageSize.value); orders.value = res.data.records; total.value = res.data.total } catch (e) { console.error('加载订单列表失败:', e) } loading.value = false }
async function handleSearch() { if (!searchUserId.value) { ElMessage.warning('请输入用户ID'); return } loading.value = true; isSearching.value = true; try { const res = await searchOrders(searchUserId.value); orders.value = res.data } catch (e) { console.error('搜索订单失败:', e) } loading.value = false }
function resetSearch() { searchUserId.value = ''; isSearching.value = false; page.value = 1; loadData() }
onMounted(loadData)
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1E1E2E; }
.page-desc { font-size: 14px; color: #8E8EA0; margin-top: 4px; }
</style>
