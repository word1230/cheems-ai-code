<template>
  <div class="app-card">
    <div class="app-cover" @click="handleViewChat">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
      <div v-else class="default-cover">
        <AppstoreOutlined :style="{ fontSize: '48px', color: '#1890ff' }" />
      </div>
    </div>
    <div class="app-info">
      <h3 class="app-name" @click="handleViewChat">{{ app.appName }}</h3>
      <p class="app-time">创建于 {{ formatTime(app.createTime) }}</p>
      <div class="app-actions">
        <a-button type="primary" size="small" @click="handleViewChat">
          <template #icon><MessageOutlined /></template>
          查看对话
        </a-button>
        <a-button v-if="app.deployKey" size="small" @click="handleViewWork">
          <template #icon><EyeOutlined /></template>
          查看作品
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AppstoreOutlined, MessageOutlined, EyeOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  app: API.AppVO
}>()

const router = useRouter()

// 查看对话
const handleViewChat = () => {
  router.push(`/app/chat/${props.app.id}?view=1`)
}

// 查看作品
const handleViewWork = () => {
  if (props.app.deployKey) {
    window.open(`http://localhost:8123/${props.app.deployKey}`, '_blank')
  }
}

const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < 30 * day) {
    return Math.floor(diff / day) + '天前'
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped>
.app-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.app-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.app-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.app-info {
  padding: 16px;
}

.app-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

.app-name:hover {
  color: #1890ff;
}

.app-time {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0 0 12px 0;
}

.app-actions {
  display: flex;
  gap: 8px;
}

.app-cover {
  cursor: pointer;
}
</style>
