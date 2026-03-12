<template>
  <div class="checkin-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>打卡</span>
          <el-tag :type="nowType">{{ nowTime }}</el-tag>
        </div>
      </template>

      <!-- 今日状态 -->
      <div class="today-status">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover" :class="['status-card', status.workRecord ? 'checked' : '']">
              <div class="status-icon">
                <el-icon :size="40" :color="status.workRecord ? '#67C23A' : '#909399'">
                  <Sunrise />
                </el-icon>
              </div>
              <div class="status-info">
                <div class="status-title">上班打卡</div>
                <div class="status-time">
                  {{ status.workRecord ? formatTime(status.workRecord.checkTime) : '未打卡' }}
                </div>
                <div class="status-remark" v-if="status.workRecord">
                  <el-tag :type="status.workRecord.status === 1 ? 'success' : 'warning'" size="small">
                    {{ status.workRecord.remark }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" :class="['status-card', status.offRecord ? 'checked' : '']">
              <div class="status-icon">
                <el-icon :size="40" :color="status.offRecord ? '#67C23A' : '#909399'">
                  <Sunny />
                </el-icon>
              </div>
              <div class="status-info">
                <div class="status-title">下班打卡</div>
                <div class="status-time">
                  {{ status.offRecord ? formatTime(status.offRecord.checkTime) : '未打卡' }}
                </div>
                <div class="status-remark" v-if="status.offRecord">
                  <el-tag :type="status.offRecord.status === 1 ? 'success' : 'warning'" size="small">
                    {{ status.offRecord.remark }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 打卡点选择 -->
      <el-divider>选择打卡点</el-divider>
      <div class="point-select">
        <el-select
          v-model="selectedPointId"
          placeholder="请选择打卡点"
          style="width: 100%"
          @change="handlePointChange"
        >
          <el-option
            v-for="point in points"
            :key="point.id"
            :label="point.name"
            :value="point.id"
          >
            <span>{{ point.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              {{ point.address }}
            </span>
          </el-option>
        </el-select>
      </div>

      <!-- 位置信息 -->
      <div class="location-info" v-if="location">
        <el-alert
          :title="`当前位置：纬度 ${location.latitude.toFixed(6)}, 经度 ${location.longitude.toFixed(6)}`"
          :type="locationChecked ? (locationInRange ? 'success' : 'error') : 'info'"
          show-icon
          :closable="false"
        >
          <template v-if="locationChecked">
            <div style="margin-top: 8px">
              距离打卡点：{{ location.distance?.toFixed(0) }}米 | 
              有效范围：{{ location.radius }}米 |
              {{ locationInRange ? '✅ 在范围内' : '❌ 超出范围' }}
            </div>
          </template>
        </el-alert>
      </div>

      <!-- 打卡按钮 -->
      <div class="checkin-actions">
        <el-button
          type="success"
          size="large"
          :disabled="!canWorkCheckin || !selectedPointId || !locationInRange"
          :loading="checking"
          @click="handleWorkCheckin"
        >
          <el-icon><Check /></el-icon>
          上班打卡
        </el-button>
        <el-button
          type="warning"
          size="large"
          :disabled="!canOffCheckin || !selectedPointId || !locationInRange"
          :loading="checking"
          @click="handleOffCheckin"
        >
          <el-icon><Check /></el-icon>
          下班打卡
        </el-button>
      </div>

      <!-- 获取位置按钮 -->
      <div class="location-action">
        <el-button @click="getLocation" :loading="locating">
          <el-icon><Position /></el-icon>
          获取当前位置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getTodayStatus, workCheckin, offCheckin } from '@/api/checkin'
import { getAllPoints, checkLocation } from '@/api/point'
import type { CheckPoint } from '@/api/point'

const nowTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
const points = ref<CheckPoint[]>([])
const selectedPointId = ref<number>()
const checking = ref(false)
const locating = ref(false)
const location = ref<{ latitude: number; longitude: number } | null>(null)
const locationChecked = ref(false)
const locationInRange = ref(false)
const locationDistance = ref(0)
const locationRadius = ref(100)

const status = reactive({
  workRecord: null as any,
  offRecord: null as any
})

const canWorkCheckin = computed(() => !status.workRecord)
const canOffCheckin = computed(() => status.workRecord && !status.offRecord)

const nowType = computed(() => {
  const hour = dayjs().hour()
  if (hour < 12) return 'success'
  if (hour < 18) return 'warning'
  return 'info'
})

// 定时器更新时间
let timer: any
onMounted(() => {
  timer = setInterval(() => {
    nowTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  }, 1000)
  
  loadTodayStatus()
  loadPoints()
})

// 清理定时器
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (timer) clearInterval(timer)
})

// 加载今日状态
const loadTodayStatus = async () => {
  try {
    const res = await getTodayStatus()
    status.workRecord = res.data.workRecord
    status.offRecord = res.data.offRecord
  } catch (error) {
    console.error(error)
  }
}

// 加载打卡点
const loadPoints = async () => {
  try {
    const res = await getAllPoints()
    points.value = res.data
    if (points.value.length > 0) {
      selectedPointId.value = points.value[0].id
    }
  } catch (error) {
    console.error(error)
  }
}

// 获取位置
const getLocation = () => {
  if (!navigator.geolocation) {
    ElMessage.error('浏览器不支持地理位置')
    return
  }

  locating.value = true
  navigator.geolocation.getCurrentPosition(
    async (position) => {
      location.value = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude
      }
      locating.value = false
      ElMessage.success('位置获取成功')
      
      // 自动校验位置
      if (selectedPointId.value) {
        await checkLocationRange()
      }
    },
    (error) => {
      locating.value = false
      ElMessage.error('获取位置失败：' + error.message)
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

// 打卡点变化时校验位置
const handlePointChange = async () => {
  if (location.value) {
    await checkLocationRange()
  }
}

// 校验位置范围
const checkLocationRange = async () => {
  if (!location.value || !selectedPointId.value) return
  
  try {
    const res = await checkLocation(selectedPointId.value, location.value.latitude, location.value.longitude)
    locationChecked.value = true
    locationInRange.value = res.data.inRange
    locationDistance.value = res.data.distance
    locationRadius.value = res.data.radius
  } catch (error) {
    console.error(error)
  }
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('HH:mm:ss')
}

// 上班打卡
const handleWorkCheckin = async () => {
  if (!location.value || !selectedPointId.value) {
    ElMessage.warning('请先获取位置并选择打卡点')
    return
  }

  checking.value = true
  try {
    await workCheckin({
      pointId: selectedPointId.value,
      latitude: location.value.latitude,
      longitude: location.value.longitude
    })
    ElMessage.success('上班打卡成功')
    await loadTodayStatus()
  } catch (error) {
    console.error(error)
  } finally {
    checking.value = false
  }
}

// 下班打卡
const handleOffCheckin = async () => {
  if (!location.value || !selectedPointId.value) {
    ElMessage.warning('请先获取位置并选择打卡点')
    return
  }

  checking.value = true
  try {
    await offCheckin({
      pointId: selectedPointId.value,
      latitude: location.value.latitude,
      longitude: location.value.longitude
    })
    ElMessage.success('下班打卡成功')
    await loadTodayStatus()
  } catch (error) {
    console.error(error)
  } finally {
    checking.value = false
  }
}
</script>

<style scoped>
.checkin-container {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.today-status {
  margin-bottom: 20px;
}

.status-card {
  text-align: center;
  padding: 20px;
  transition: all 0.3s;
}

.status-card.checked {
  border-color: #67C23A;
  background-color: #f0f9ff;
}

.status-icon {
  margin-bottom: 12px;
}

.status-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status-time {
  color: #666;
  margin-bottom: 8px;
}

.point-select {
  margin: 20px 0;
}

.location-info {
  margin: 20px 0;
}

.checkin-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin: 30px 0;
}

.checkin-actions .el-button {
  width: 150px;
  height: 50px;
  font-size: 16px;
}

.location-action {
  text-align: center;
}
</style>
