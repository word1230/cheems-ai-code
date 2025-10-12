<template>
  <div class="app-card">
    <div class="app-cover" @click="handleViewChat">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
      <div v-else class="default-cover">
        <AppstoreOutlined :style="{ fontSize: '48px', color: '#1890ff' }" />
      </div>
    </div>
    <div class="app-info">
      <div class="app-header">
        <div class="user-avatar">
          <a-avatar :src="user?.userAvatar" size="small">
            {{ user?.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </div>
        <div class="app-details">
          <h3 class="app-name" @click="handleViewChat">{{ app.appName }}</h3>
          <p class="user-name">{{ user?.userName || '匿名用户' }}</p>
        </div>
      </div>
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
import { buildDeployUrl } from '@/constants/env'

const props = defineProps<{
  app: API.AppVO
  user?: API.UserVO // 可选的用户信息
}>()

const router = useRouter()

// 查看对话
const handleViewChat = () => {
  router.push(`/app/chat/${props.app.id}?view=1`)
}

// 查看作品
const handleViewWork = () => {
  if (props.app.deployKey) {
    window.open(buildDeployUrl(props.app.deployKey), '_blank')
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
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s;
}

.app-card:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.app-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
background: #4e54c8;  /* fallback for old browsers */
background: -webkit-linear-gradient(to top, #8f94fb, #4e54c8);  /* Chrome 10-25, Safari 5.1-6 */
background: linear-gradient(to top, #8f94fb, #4e54c8); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */


  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
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

.app-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  flex-shrink: 0;
}

.app-details {
  flex: 1;
  min-width: 0;
}

.app-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: white;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  line-height: 1.4;
}

.app-name:hover {
  color: #1890ff;
}

.user-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.app-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
