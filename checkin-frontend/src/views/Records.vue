<template>
  <div class="records-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>打卡记录</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" size="default">
        <el-form-item>
          <el-button type="primary" @click="loadRecords">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="checkTime" label="打卡时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.checkTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.checkType === 1 ? 'success' : 'warning'">
              {{ row.checkType === 1 ? '上班' : '下班' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="distance" label="距离 (米)" width="100" />
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadRecords"
          @current-change="loadRecords"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { getRecords } from '@/api/checkin'
import type { CheckRecord } from '@/api/checkin'

const loading = ref(false)
const records = ref<CheckRecord[]>([])
const dateRange = ref<[string, string]>()
const page = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(() => {
  loadRecords()
})

const handleDateChange = () => {
  page.value = 1
  loadRecords()
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params: any = {
      page: page.value,
      size: size.value
    }
    
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    const res: any = await getRecords(params)
    records.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusType = (status: number) => {
  const types: Record<number, any> = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    1: '正常',
    2: '迟到',
    3: '早退',
    4: '缺卡'
  }
  return texts[status] || '未知'
}
</script>

<style scoped>
.records-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
