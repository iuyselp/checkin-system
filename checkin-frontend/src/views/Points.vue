<template>
  <div class="points-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>打卡点管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增打卡点
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" size="default">
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="输入名称搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPoints">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="points" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="address" label="地址" />
        <el-table-column label="位置" width="200">
          <template #default="{ row }">
            {{ row.latitude.toFixed(4) }}, {{ row.longitude.toFixed(4) }}
          </template>
        </el-table-column>
        <el-table-column prop="radius" label="半径 (米)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadPoints"
          @current-change="loadPoints"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑打卡点' : '新增打卡点'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number
            v-model="form.latitude"
            :precision="8"
            :min="-90"
            :max="90"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number
            v-model="form.longitude"
            :precision="8"
            :min="-180"
            :max="180"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="有效半径" prop="radius">
          <el-input-number v-model="form.radius" :min="10" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上班时间">
          <el-time-picker
            v-model="form.workStartTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择时间"
          />
        </el-form-item>
        <el-form-item label="下班时间">
          <el-time-picker
            v-model="form.workEndTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择时间"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getPointList, createPoint, updatePoint, deletePoint } from '@/api/point'
import type { CheckPoint } from '@/api/point'

const loading = ref(false)
const submitting = ref(false)
const points = ref<CheckPoint[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const searchForm = reactive({
  name: ''
})

const form = reactive<CheckPoint>({
  id: 0,
  name: '',
  address: '',
  latitude: 39.9042,
  longitude: 116.4074,
  radius: 100,
  workStartTime: '09:00:00',
  workEndTime: '18:00:00',
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }],
  longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  radius: [{ required: true, message: '请输入有效半径', trigger: 'blur' }]
}

onMounted(() => {
  loadPoints()
})

const loadPoints = async () => {
  loading.value = true
  try {
    const res: any = await getPointList({
      name: searchForm.name,
      page: page.value,
      size: size.value
    })
    points.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: 0,
    name: '',
    address: '',
    latitude: 39.9042,
    longitude: 116.4074,
    radius: 100,
    workStartTime: '09:00:00',
    workEndTime: '18:00:00',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: CheckPoint) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: CheckPoint) => {
  try {
    await ElMessageBox.confirm('确定要删除该打卡点吗？', '提示', {
      type: 'warning'
    })
    await deletePoint(row.id)
    ElMessage.success('删除成功')
    loadPoints()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updatePoint(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createPoint(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadPoints()
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.points-container {
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
