<template>
  <div class="statistics-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤统计</span>
          <el-date-picker
            v-model="selectedMonth"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            @change="loadStatistics"
          />
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #409EFF">
                <el-icon :size="30" color="#fff"><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.workDays }}</div>
                <div class="stat-label">应出勤天数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #67C23A">
                <el-icon :size="30" color="#fff"><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.actualDays }}</div>
                <div class="stat-label">实际出勤</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #E6A23C">
                <el-icon :size="30" color="#fff"><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.lateCount }}</div>
                <div class="stat-label">迟到次数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #F56C6C">
                <el-icon :size="30" color="#fff"><Close /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.absentCount }}</div>
                <div class="stat-label">缺勤次数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 出勤率 -->
      <el-divider>出勤概况</el-divider>
      <div class="attendance-rate">
        <el-progress
          :percentage="parseFloat(stats.attendanceRate)"
          :color="getRateColor"
          :format="() => stats.attendanceRate"
        />
      </div>

      <!-- 详细统计 -->
      <el-divider>详细数据</el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="统计月份">{{ selectedMonth }}</el-descriptions-item>
        <el-descriptions-item label="正常打卡">{{ stats.normalCount }} 次</el-descriptions-item>
        <el-descriptions-item label="迟到次数">{{ stats.lateCount }} 次</el-descriptions-item>
        <el-descriptions-item label="早退次数">{{ stats.earlyCount }} 次</el-descriptions-item>
        <el-descriptions-item label="缺勤次数">{{ stats.absentCount }} 次</el-descriptions-item>
        <el-descriptions-item label="出勤率">
          <el-tag :type="getRateType">{{ stats.attendanceRate }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { get } from '@/api/request'

const selectedMonth = ref(dayjs().format('YYYY-MM'))
const loading = ref(false)

const stats = reactive({
  month: '',
  workDays: 0,
  actualDays: 0,
  normalCount: 0,
  lateCount: 0,
  earlyCount: 0,
  absentCount: 0,
  attendanceRate: '0%'
})

onMounted(() => {
  loadStatistics()
})

const loadStatistics = async () => {
  loading.value = true
  try {
    const res: any = await get('/stat/personal', { month: selectedMonth.value })
    Object.assign(stats, res.data)
  } catch (error) {
    ElMessage.error('加载统计数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getRateColor = computed(() => {
  const rate = parseFloat(stats.attendanceRate)
  if (rate >= 90) return '#67C23A'
  if (rate >= 70) return '#E6A23C'
  return '#F56C6C'
})

const getRateType = computed(() => {
  const rate = parseFloat(stats.attendanceRate)
  if (rate >= 90) return 'success'
  if (rate >= 70) return 'warning'
  return 'danger'
})
</script>

<style scoped>
.statistics-container {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.attendance-rate {
  max-width: 600px;
  margin: 0 auto 20px;
}
</style>
