<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="logo">
        <el-icon :size="24"><Clock /></el-icon>
        <span>打卡系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/checkin">
          <el-icon><Position /></el-icon>
          <span>打卡</span>
        </el-menu-item>
        <el-menu-item index="/records">
          <el-icon><Document /></el-icon>
          <span>记录</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>统计</span>
        </el-menu-item>
        <el-menu-item index="/points">
          <el-icon><Location /></el-icon>
          <span>打卡点</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体内容 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header>
        <div class="header-content">
          <h3>打卡签到系统</h3>
          <div class="user-info">
            <el-dropdown>
              <span class="user-name">
                <el-avatar :size="32" :icon="UserFilled" />
                {{ user?.username || '用户' }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'
import type { User } from '@/api/user'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const user = computed(() => {
  const userStr = localStorage.getItem('user')
  return userStr ? (JSON.parse(userStr) as User) : null
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 60px;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h3 {
  margin: 0;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
