<template>
  <div class="app-chat-page">
    <div class="chat-header">
      <div class="header-left">
        <a-button type="text" @click="goBack">
          <template #icon><ArrowLeftOutlined /></template>
        </a-button>
        <h2 class="app-title">{{ appInfo?.appName || '加载中...' }}</h2>
        <a-tag
          v-if="appInfo?.codeGenType"
          :color="getCodeGenTypeColor"
          style="margin-left: 12px; font-size: 12px;"
        >
          {{ getCodeGenTypeLabel }}
        </a-tag>
      </div>
      <div class="header-right">
        <a-button @click="showDetailModal = true">
          <template #icon><InfoCircleOutlined /></template>
          应用详情
        </a-button>
        <a-button
          @click="handleDownloadCode"
          :loading="downloading"
          :disabled="!appInfo?.id"
          style="margin-left: 8px"
        >
          <template #icon><DownloadOutlined /></template>
          下载代码
        </a-button>
        <a-button
          type="primary"
          @click="handleDeploy"
          :loading="deploying"
          :disabled="!appInfo?.id"
          style="margin-left: 8px"
        >
          <template #icon><CloudUploadOutlined /></template>
          部署
        </a-button>
      </div>
    </div>

    <div class="chat-content">
      <div class="chat-area">
        <div class="messages-container" ref="messagesContainer">
          <!-- 加载更多按钮 -->
          <div v-if="hasMoreHistory" class="load-more-container">
            <a-button
              type="link"
              :loading="loadingHistory"
              @click="loadMoreHistory"
            >
              <template #icon><UpOutlined /></template>
              加载更多历史消息
            </a-button>
          </div>

          <div
            v-for="(msg, index) in messages"
            :key="msg.id || index"
            :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']"
          >
            <div class="message-avatar">
              <a-avatar v-if="msg.role === 'user'" :src="loginUserStore.loginUser.userAvatar">
                {{ loginUserStore.loginUser.userName?.charAt(0) }}
              </a-avatar>
              <a-avatar v-else :src="aiAvatarSrc" :alt="'AI助手'">
                <template #icon v-if="!aiAvatarSrc"><RobotOutlined /></template>
              </a-avatar>
            </div>
            <div class="message-content">
              <div class="message-text" v-if="msg.role === 'user'">{{ msg.content }}</div>
              <div class="message-text" v-else>
                <!-- AI 消息：显示生成内容或加载状态 -->
                <template v-if="msg.isGenerating && !msg.content">
                  <a-spin size="small" /> AI 正在生成中...
                </template>
                <template v-else>
                  <MarkdownRender :content="msg.content" />
                  <!-- 如果还在生成中但有内容，显示加载指示器 -->
                  <div v-if="msg.isGenerating" style="margin-top: 8px;">
                    <a-spin size="small" /> 正在生成中...
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <a-tooltip 
            v-if="!isOwner" 
            title="无法在别人的作品下对话哦~"
            placement="top"
          >
            <a-textarea
              v-model:value="userMessage"
              placeholder="无法在别人的作品下对话哦~"
              :auto-size="{ minRows: 2, maxRows: 4 }"
              :disabled="true"
            />
          </a-tooltip>
          <a-textarea
            v-else
            v-model:value="userMessage"
            placeholder="请描述你想生成的网站，越详细效果越好哦"
            :auto-size="{ minRows: 2, maxRows: 4 }"
            @pressEnter="handleSend"
          />
          <div class="input-actions">
            <a-button 
              type="primary" 
              @click="handleSend" 
              :loading="generating"
              :disabled="!isOwner"
            >
              <template #icon><SendOutlined /></template>
              发送
            </a-button>
          </div>
        </div>
      </div>

      <div class="preview-area">
        <div class="preview-header">
          <h3>生成后的网页展示</h3>
          <a-button v-if="previewUrl" type="link" @click="openInNewTab">
            <template #icon><ExportOutlined /></template>
            新窗口打开
          </a-button>
        </div>
        <div class="preview-content">
          <iframe
            v-if="previewUrl"
            :src="previewUrl"
            frameborder="0"
            class="preview-iframe"
            :key="iframeKey"
            @load="handleIframeLoad"
          ></iframe>
          <a-empty v-else description="等待生成完成后展示" />
        </div>
      </div>
    </div>

    <!-- 应用详情弹窗 -->
    <a-modal
      v-model:open="showDetailModal"
      title="应用详情"
      :footer="null"
      width="500px"
    >
      <div v-if="appInfo" class="app-detail-content">
        <div class="detail-section">
          <h4>应用基础信息</h4>
          <div class="detail-item">
            <span class="detail-label">应用名称：</span>
            <span class="detail-value">{{ appInfo.appName }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">生成类型：</span>
            <span class="detail-value">
              <a-tag
                v-if="appInfo.codeGenType"
                :color="getCodeGenTypeColor"
              >
                {{ getCodeGenTypeLabel }}
              </a-tag>
              <span v-else>-</span>
            </span>
          </div>
          <div class="detail-item">
            <span class="detail-label">创建者：</span>
            <div class="creator-info">
              <a-avatar :src="creatorInfo?.userAvatar" size="small">
                {{ creatorInfo?.userName?.charAt(0) }}
              </a-avatar>
              <span style="margin-left: 8px">{{ creatorInfo?.userName || '加载中...' }}</span>
            </div>
          </div>
          <div class="detail-item">
            <span class="detail-label">创建时间：</span>
            <span class="detail-value">{{ formatDetailTime(appInfo.createTime) }}</span>
          </div>
          <div v-if="appInfo.deployKey" class="detail-item">
            <span class="detail-label">部署状态：</span>
            <a-tag color="success">已部署</a-tag>
          </div>
          <div v-else class="detail-item">
            <span class="detail-label">部署状态：</span>
            <a-tag>未部署</a-tag>
          </div>
        </div>

        <div v-if="canManage" class="detail-section">
          <h4>操作</h4>
          <a-space>
            <a-button type="primary" @click="handleEdit">
              <template #icon><EditOutlined /></template>
              修改
            </a-button>
            <a-popconfirm
              title="确定要删除这个应用吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete"
            >
              <a-button danger>
                <template #icon><DeleteOutlined /></template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- 部署成功弹窗 -->
    <a-modal
      v-model:open="showDeployModal"
      title="部署成功"
      :footer="null"
      width="500px"
    >
      <div class="deploy-success-content">
        <div class="success-icon">
          <CheckCircleOutlined :style="{ fontSize: '64px', color: '#52c41a' }" />
        </div>
        <h3 class="success-title">应用已成功部署！</h3>
        <div class="deploy-url-section">
          <div class="url-label">访问地址：</div>
          <div class="url-value">
            <a-input 
              :value="deployedUrl" 
              readonly 
              :style="{ marginBottom: '12px' }"
            />
            <a-button 
              type="link" 
              size="small" 
              @click="copyUrl"
              :style="{ padding: 0 }"
            >
              <template #icon><CopyOutlined /></template>
              复制链接
            </a-button>
          </div>
        </div>
        <div class="deploy-actions">
          <a-button type="primary" size="large" @click="visitDeployedSite">
            <template #icon><GlobalOutlined /></template>
            访问网站
          </a-button>
          <a-button size="large" @click="closeDeployModal">
            关闭
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  CloudUploadOutlined,
  SendOutlined,
  RobotOutlined,
  ExportOutlined,
  InfoCircleOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  GlobalOutlined,
  CopyOutlined,
  UpOutlined,
  DownloadOutlined
} from '@ant-design/icons-vue'
import { getAppVoById, deployApp, deleteApp, downloadAppCode } from '@/api/appController'
import { getUserVoById } from '@/api/userController'
import { listChatHistoryByPage } from '@/api/chatHistoryController'
import { useLoginUserStore } from '@/stores/loginUser'
import myAxios from '@/request'
import MarkdownRender from '@/components/MarkdownRender.vue'
import { CODE_GEN_TYPE_CONFIG, CodeGenTypeEnum } from '@/constants/CodeGenType'
import { buildPreviewUrl, buildChatApiUrl } from '@/constants/env'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// AI头像地址
const aiAvatarSrc = new URL('@/assets/logo.png', import.meta.url).href

interface Message {
  id?: number
  role: 'user' | 'ai'
  content: string
  createTime?: string
  isGenerating?: boolean // 标识AI是否正在生成中
}

const appInfo = ref<API.AppVO>()
const messages = ref<Message[]>([])
const userMessage = ref('')
const generating = ref(false)
const deploying = ref(false)
const downloading = ref(false)
const previewUrl = ref('')
const iframeKey = ref(0) // 用于强制重新加载iframe
const messagesContainer = ref<HTMLElement>()
const isOwner = ref(true)
const showDetailModal = ref(false)
const showDeployModal = ref(false)
const deployedUrl = ref('')
const creatorInfo = ref<API.UserVO>()

// 历史消息分页相关
const loadingHistory = ref(false)
const currentPage = ref(1)
const pageSize = 10
const totalHistory = ref(0)
const hasMoreHistory = computed(() => currentPage.value * pageSize < totalHistory.value)

const appId = route.params.id as string

// 是否可以管理（本人或管理员）
const canManage = computed(() => {
  return isOwner.value || loginUserStore.loginUser.userRole === 'admin'
})

// 获取生成类型标签文本
const getCodeGenTypeLabel = computed(() => {
  if (!appInfo.value?.codeGenType) return ''
  return CODE_GEN_TYPE_CONFIG[appInfo.value.codeGenType as CodeGenTypeEnum]?.label || appInfo.value.codeGenType
})

// 获取生成类型标签颜色
const getCodeGenTypeColor = computed(() => {
  if (!appInfo.value?.codeGenType) return 'default'
  switch (appInfo.value.codeGenType) {
    case CodeGenTypeEnum.HTML:
      return 'orange'
    case CodeGenTypeEnum.MULTI_FILE:
      return 'blue'
    case CodeGenTypeEnum.VUE_PROJECT:
      return 'green'
    default:
      return 'default'
  }
})

// 加载历史消息
const loadChatHistory = async (isLoadMore = false) => {
  loadingHistory.value = true
  try {
    const res = await listChatHistoryByPage({
      pageNum: currentPage.value,
      pageSize,
      appId: appId, // 直接使用 string 类型，不转换为 Number 以避免精度丢失
      sortField: 'createTime',
      sortOrder: 'descend', // 降序获取，最新的在前
    })

    if (res.data.code === 0 && res.data.data) {
      const records = res.data.data.records || []
      totalHistory.value = res.data.data.totalRow || 0

      // 转换历史消息格式
      const historyMessages: Message[] = records.map((record) => ({
        id: record.id,
        role: record.messageType === 'user' ? 'user' : 'ai',
        content: record.message || '',
        createTime: record.createTime,
      })).reverse() // 反转顺序，让最早的消息在前

      if (isLoadMore) {
        // 加载更多时，在现有消息前面插入
        messages.value = [...historyMessages, ...messages.value]
      } else {
        // 首次加载，直接赋值
        messages.value = historyMessages
      }

      // 首次加载完成后，检查是否需要展示网站
      if (!isLoadMore && totalHistory.value >= 2 && appInfo.value?.codeGenType && appInfo.value?.id) {
        iframeKey.value += 1
        previewUrl.value = buildPreviewUrl(appInfo.value.codeGenType, appInfo.value.id, Date.now())
      }
    }
  } catch (error) {
    console.error('加载历史消息失败', error)
  } finally {
    loadingHistory.value = false
  }
}

// 加载更多历史消息
const loadMoreHistory = async () => {
  currentPage.value++
  await loadChatHistory(true)
}

// 加载应用信息
const loadAppInfo = async () => {
  try {
    const res = await getAppVoById({ id: appId })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data

      // 检查是否是应用所有者
      isOwner.value = appInfo.value.userId === loginUserStore.loginUser.id

      // 加载创建者信息
      if (appInfo.value.userId) {
        loadCreatorInfo(appInfo.value.userId)
      }

      // 加载历史消息
      await loadChatHistory()

      // 只有在是自己的app且没有对话历史时才自动发送初始消息
      if (isOwner.value && messages.value.length === 0 && appInfo.value.initPrompt) {
        messages.value.push({
          role: 'user',
          content: appInfo.value.initPrompt
        })
        await generateCode(appInfo.value.initPrompt)
      }
    } else {
      message.error('加载应用信息失败')
      router.push('/')
    }
  } catch (error) {
    message.error('加载应用信息失败')
    router.push('/')
  }
}

// 加载创建者信息
const loadCreatorInfo = async (userId: string) => {
  try {
    const res = await getUserVoById({ id: userId })
    if (res.data.code === 0 && res.data.data) {
      creatorInfo.value = res.data.data
    }
  } catch (error) {
    console.error('加载创建者信息失败', error)
  }
}

// 生成代码
const generateCode = async (prompt: string) => {
  generating.value = true
  let aiResponse = ''

  try {
    const response = await fetch(
      buildChatApiUrl(prompt, appId),
      {
        method: 'GET',
        credentials: 'include',
      }
    )

    if (!response.ok || !response.body) {
      throw new Error('请求失败')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    // 添加AI消息占位，标记为正在生成中
    const aiMessageIndex = messages.value.length
    messages.value.push({
      role: 'ai',
      content: '',
      isGenerating: true
    })

    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      const chunk = decoder.decode(value, { stream: true })
      buffer += chunk

      // 处理 SSE 格式的数据流
      const lines = buffer.split('\n')
      buffer = lines.pop() || '' // 保留最后一个不完整的行

      for (const line of lines) {
        if (line.startsWith('data:')) {
          try {
            const jsonStr = line.substring(5).trim()
            if (jsonStr) {
              const data = JSON.parse(jsonStr)
              // 提取实际内容，优先使用 d 字段
              const content = data.d || data.content || data.message || data.text || data.data || jsonStr
              aiResponse += content
              messages.value[aiMessageIndex].content = aiResponse
            }
          } catch (e) {
            // 如果不是 JSON，直接使用原始内容
            const content = line.substring(5).trim()
            if (content) {
              aiResponse += content
              messages.value[aiMessageIndex].content = aiResponse
            }
          }
        } else if (line.trim() && !line.startsWith(':')) {
          // 处理非 SSE 格式的普通文本
          try {
            const data = JSON.parse(line)
            // 提取实际内容，优先使用 d 字段
            const content = data.d || data.content || data.message || data.text || data.data || line
            aiResponse += content
            messages.value[aiMessageIndex].content = aiResponse
          } catch (e) {
            // 如果不是 JSON，直接使用原始内容
            if (line.trim()) {
              aiResponse += line
              messages.value[aiMessageIndex].content = aiResponse
            }
          }
        }
      }

      // 滚动到底部
      await nextTick()
      scrollToBottom()
    }

    // 生成完成后，标记为不再生成
    if (messages.value[aiMessageIndex]) {
      messages.value[aiMessageIndex].isGenerating = false
    }

    // 生成完成后显示预览
    if (appInfo.value?.codeGenType && appInfo.value?.id) {
      // 强制重新加载iframe
      iframeKey.value += 1
      previewUrl.value = buildPreviewUrl(appInfo.value.codeGenType, appInfo.value.id, Date.now())
    }
  } catch (error) {
    message.error('生成失败，请重试')
    console.error(error)

    // 如果出错，移除生成中的消息或标记为错误状态
    const lastMessage = messages.value[messages.value.length - 1]
    if (lastMessage && lastMessage.isGenerating) {
      if (!lastMessage.content) {
        // 如果没有内容，移除这条消息
        messages.value.pop()
      } else {
        // 如果有内容，标记为不再生成
        lastMessage.isGenerating = false
      }
    }
  } finally {
    generating.value = false
  }
}

// 发送消息
const handleSend = async (e?: KeyboardEvent) => {
  // 如果是键盘事件，需要按 Ctrl+Enter 才能发送
  if (e && e instanceof KeyboardEvent && !e.ctrlKey) {
    return
  }

  // 权限检查
  if (!isOwner.value) {
    message.warning('无法在别人的作品下对话哦~')
    return
  }

  if (!userMessage.value.trim()) {
    message.warning('请输入消息')
    return
  }

  const msg = userMessage.value
  userMessage.value = ''

  messages.value.push({
    role: 'user',
    content: msg
  })

  await nextTick()
  scrollToBottom()

  await generateCode(msg)
}

// 下载代码
const handleDownloadCode = async () => {
  if (!appInfo.value?.id) return

  downloading.value = true
  try {
    const response = await myAxios.get(`/app/download/${appInfo.value.id}`, {
      responseType: 'blob',
      timeout: 300000, // 5分钟超时，用于处理大文件下载
    })

    // 从响应头获取文件名
    const contentDisposition = response.headers['content-disposition'] || response.headers['Content-Disposition']
    let fileName = `${appInfo.value.appName || 'app'}.zip`

    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = fileNameMatch[1].replace(/['"]/g, '')
      }
    }

    // 创建下载链接
    const blob = new Blob([response.data], { type: 'application/zip' })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)

    message.success('代码下载成功！')
  } catch (error) {
    console.error('下载代码失败:', error)
    message.error('下载代码失败，请重试')
  } finally {
    downloading.value = false
  }
}

// 部署应用
const handleDeploy = async () => {
  if (!appInfo.value?.id) return
  
  deploying.value = true
  try {
    const res = await deployApp({ appId: appInfo.value.id })
    if (res.data.code === 0 && res.data.data) {
      deployedUrl.value = res.data.data
      showDeployModal.value = true
      message.success('部署成功！')
    } else {
      message.error('部署失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('部署失败')
  } finally {
    deploying.value = false
  }
}

// 访问部署的网站
const visitDeployedSite = () => {
  if (deployedUrl.value) {
    window.open(deployedUrl.value, '_blank')
  }
}

// 关闭部署弹窗
const closeDeployModal = () => {
  showDeployModal.value = false
}

// 复制链接
const copyUrl = async () => {
  try {
    await navigator.clipboard.writeText(deployedUrl.value)
    message.success('链接已复制到剪贴板')
  } catch (error) {
    message.error('复制失败，请手动复制')
  }
}

// 返回
const goBack = () => {
  router.push('/')
}

// 新窗口打开
const openInNewTab = () => {
  if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化详情时间
const formatDetailTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 编辑应用
const handleEdit = () => {
  showDetailModal.value = false
  router.push(`/app/edit/${appId}`)
}

// 删除应用
const handleDelete = async () => {
  try {
    const res = await deleteApp({ id: appId })
    if (res.data.code === 0) {
      message.success('删除成功')
      showDetailModal.value = false
      router.push('/')
    } else {
      message.error('删除失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('删除失败')
  }
}

// iframe加载完成处理
const handleIframeLoad = () => {
  // 可以在这里添加加载完成的处理逻辑
}

onMounted(() => {
  loadAppInfo()
})
</script>

<style scoped>
.app-chat-page {
  height: calc(100vh - 64px - 70px);
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #e8e8e8;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.chat-content {
  flex: 1;
  display: flex;
  gap: 8px;
  padding: 8px;
  overflow: hidden;
}

.chat-area {
  flex: 2;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.messages-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  display: flex;
  justify-content: flex-end;
}

.message-text {
  padding: 12px 16px;
  border-radius: 8px;
  background: #f5f5f5;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 为 AI 消息中的 Markdown 内容优化样式 */
.ai-message .message-text {
  background: #ffffff;
  border: 1px solid #e8e8e8;
}

.ai-message .message-text .markdown-render {
  color: #333;
}

.ai-message .message-text .markdown-render :deep(p:last-child) {
  margin-bottom: 0;
}

.user-message .message-text {
  background: #1890ff;
  color: white;
}

.input-area {
  padding: 12px 16px;
  border-top: 1px solid #e8e8e8;
}

.input-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.preview-area {
  flex: 3;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
}

.preview-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.preview-content {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

@media (max-width: 1200px) {
  .chat-content {
    flex-direction: column;
  }
  
  .chat-area,
  .preview-area {
    height: 50%;
  }
}

.app-detail-content {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a1a;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-label {
  color: #8c8c8c;
  min-width: 80px;
  font-weight: 500;
}

.detail-value {
  color: #1a1a1a;
  flex: 1;
}

.creator-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.deploy-success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 24px;
}

.deploy-url-section {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  text-align: left;
}

.url-label {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 8px;
  font-weight: 500;
}

.url-value {
  word-break: break-all;
}

.deploy-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.deploy-actions .ant-btn {
  min-width: 120px;
}

.load-more-container {
  text-align: center;
  padding: 16px 0;
  margin-bottom: 16px;
}
</style>
