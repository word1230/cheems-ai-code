<template>
  <div class="home-page">
    <div class="hero-section">
      <h1 class="main-title">AI 应用生成平台</h1>
      <p class="subtitle">一句话轻松创建网站应用</p>

      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="帮我创建个人博客网站"
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
        <a-tag @click="userPrompt = '创建一个个人博客网站，包含文章发布、评论、标签分类功能，使用简洁现代的设计风格，支持暗色模式切换'">个人博客网站</a-tag>
        <a-tag @click="userPrompt = '设计一个企业官网，包含公司介绍、产品展示、联系我们等页面，要求专业大气，响应式设计适配移动端'">企业官网</a-tag>
        <a-tag @click="userPrompt = '开发一个在线商城，包含商品列表、购物车、订单管理功能，需要用户登录注册，支持商品搜索和筛选'">在线商城</a-tag>
        <a-tag @click="userPrompt = '制作一个作品集展示网站，展示设计师的作品案例，包含项目详情页、联系方式，要求视觉效果突出'">作品集网站</a-tag>
      </div>
    </div>

    <div class="apps-section">
      <h2 class="section-title">我的作品</h2>
      <a-spin :spinning="myAppsLoading">
        <div v-if="myAppsList.length > 0" class="apps-grid">
          <AppCard v-for="app in myAppsList" :key="app.id" :app="app" :user="userMap.get(app.userId!)" />
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
          <AppCard v-for="app in featuredAppsList" :key="app.id" :app="app" :user="userMap.get(app.userId!)" />
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
import { addApp, listAppVoByPage, listMyAppVoByPage } from '@/api/appController'
import { getUserVoById } from '@/api/userController'
import AppCard from '@/components/AppCard.vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { SendOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

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

// 用户信息映射
const userMap = ref<Map<number, API.UserVO>>(new Map())

// 加载用户信息
const loadUserInfo = async (userId: number) => {
  if (userMap.value.has(userId)) {
    return userMap.value.get(userId)
  }

  try {
    const res = await getUserVoById({ id: userId.toString() })
    if (res.data.code === 0 && res.data.data) {
      userMap.value.set(userId, res.data.data)
      return res.data.data
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  }

  return null
}

// 批量加载用户信息
const loadUsersForApps = async (apps: API.AppVO[]) => {
  const userIds = [...new Set(apps.map(app => app.userId).filter(Boolean))]
  await Promise.all(userIds.map(userId => loadUserInfo(userId)))
}

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
      initPrompt: userPrompt.value,
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
      const apps = res.data.data.records || []

      if (page === 1) {
        myAppsList.value = apps
      } else {
        myAppsList.value.push(...apps)
      }

      myAppsTotal.value = res.data.data.totalRow || 0
      myAppsPage.value = page

      // 加载用户信息
      await loadUsersForApps(apps)
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
      const apps = res.data.data.records || []

      if (page === 1) {
        featuredAppsList.value = apps
      } else {
        featuredAppsList.value.push(...apps)
      }

      featuredAppsTotal.value = res.data.data.totalRow || 0
      featuredAppsPage.value = page

      // 加载用户信息
      await loadUsersForApps(apps)
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


onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
background: #4e54c8;  /* fallback for old browsers */
background: -webkit-linear-gradient(to top, #8f94fb, #4e54c8);  /* Chrome 10-25, Safari 5.1-6 */
background: linear-gradient(to bottom, #8f94fb, #4e54c8); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */

  margin: 0;
  padding: 0;
  position: relative;
}

.hero-section {
  text-align: center;
  padding: 60px 20px;
  margin: 20px 20px;
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
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.prompt-input:focus {
  border-color: rgba(255, 255, 255, 0.6);
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 1);
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px;
  margin-bottom: 30px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 24px;
  color: white;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
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
