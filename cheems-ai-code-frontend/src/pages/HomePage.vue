<template>
  <div class="home-page">
    <div class="hero-section">
      <h1 class="main-title">一句话 <span class="highlight">🤖</span> 呈所想</h1>
      <p class="subtitle">与 AI 对话轻松创建应用和网站</p>

      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="使用 NoCode 创建一个高效的小工具，帮我计算......"
          :auto-size="{ minRows: 3, maxRows: 6 }"
          class="prompt-input"
        />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="handleCreateApp" :loading="creating">
            <template #icon><SendOutlined /></template>
            创建应用
          </a-button>
        </div>
      </div>

      <div class="quick-tags">
        <a-tag @click="userPrompt = '波普风电商网页'">波普风电商网页</a-tag>
        <a-tag @click="userPrompt = '企业网站'">企业网站</a-tag>
        <a-tag @click="userPrompt = '电商运营后台'">电商运营后台</a-tag>
        <a-tag @click="userPrompt = '暗黑诺基亚社区'">暗黑诺基亚社区</a-tag>
      </div>
    </div>

    <div class="apps-section">
      <h2 class="section-title">我的作品</h2>
      <a-spin :spinning="myAppsLoading">
        <div v-if="myAppsList.length > 0" class="apps-grid">
          <AppCard v-for="app in myAppsList" :key="app.id" :app="app" />
        </div>
        <a-empty v-else description="暂无应用，快去创建吧" />
      </a-spin>

      <div v-if="myAppsTotal > myAppsList.length" class="load-more">
        <a-button @click="loadMoreMyApps" :loading="myAppsLoading">加载更多</a-button>
      </div>
    </div>

    <div class="apps-section">
      <h2 class="section-title">精选案例</h2>
      <a-spin :spinning="featuredAppsLoading">
        <div v-if="featuredAppsList.length > 0" class="apps-grid">
          <AppCard v-for="app in featuredAppsList" :key="app.id" :app="app" />
        </div>
        <a-empty v-else description="暂无精选应用" />
      </a-spin>

      <div v-if="featuredAppsTotal > featuredAppsList.length" class="load-more">
        <a-button @click="loadMoreFeaturedApps" :loading="featuredAppsLoading">加载更多</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { SendOutlined } from '@ant-design/icons-vue'
import { addApp, listMyAppVoByPage, listAppVoByPage } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userPrompt = ref('')
const creating = ref(false)

// 我的应用
const myAppsList = ref<API.AppVO[]>([])
const myAppsLoading = ref(false)
const myAppsPage = ref(1)
const myAppsTotal = ref(0)

// 精选应用
const featuredAppsList = ref<API.AppVO[]>([])
const featuredAppsLoading = ref(false)
const featuredAppsPage = ref(1)
const featuredAppsTotal = ref(0)

// 创建应用
const handleCreateApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      appName: userPrompt.value.substring(0, 20),
      initPrompt: userPrompt.value,
      codeGenType: 'multi_file',
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      router.push(`/app/chat/${res.data.data}`)
    } else {
      message.error('创建失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('创建失败')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async (page: number = 1) => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  myAppsLoading.value = true
  try {
    const res = await listMyAppVoByPage({
      pageNum: page,
      pageSize: 20,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      if (page === 1) {
        myAppsList.value = res.data.data.records || []
      } else {
        myAppsList.value.push(...(res.data.data.records || []))
      }
      myAppsTotal.value = res.data.data.totalRow || 0
      myAppsPage.value = page
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    myAppsLoading.value = false
  }
}

// 加载更多我的应用
const loadMoreMyApps = () => {
  loadMyApps(myAppsPage.value + 1)
}

// 加载精选应用
const loadFeaturedApps = async (page: number = 1) => {
  featuredAppsLoading.value = true
  try {
    const res = await listAppVoByPage({
      pageNum: page,
      pageSize: 20,
      sortField: 'priority',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      if (page === 1) {
        featuredAppsList.value = res.data.data.records || []
      } else {
        featuredAppsList.value.push(...(res.data.data.records || []))
      }
      featuredAppsTotal.value = res.data.data.totalRow || 0
      featuredAppsPage.value = page
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    featuredAppsLoading.value = false
  }
}

// 加载更多精选应用
const loadMoreFeaturedApps = () => {
  loadFeaturedApps(featuredAppsPage.value + 1)
}

// 格式化时间
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

onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
})
</script>

<style scoped>
.home-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.hero-section {
  text-align: center;
  padding: 60px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 60px;
  color: white;
}

.main-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  color: white;
}

.highlight {
  font-size: 52px;
}

.subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.95;
}

.input-section {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.prompt-input {
  font-size: 16px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.input-actions {
  display: flex;
  justify-content: center;
}

.quick-tags {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.quick-tags :deep(.ant-tag) {
  cursor: pointer;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  transition: all 0.3s;
}

.quick-tags :deep(.ant-tag:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.apps-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1a1a1a;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.load-more {
  text-align: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .main-title {
    font-size: 32px;
  }

  .subtitle {
    font-size: 16px;
  }

  .section-title {
    font-size: 24px;
  }

  .apps-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
}
</style>
