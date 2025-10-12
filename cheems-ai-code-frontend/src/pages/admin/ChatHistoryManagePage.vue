<template>
  <div class="chat-history-manage-page">
    <h2>对话管理</h2>

    <a-card style="margin-bottom: 16px">
      <a-form layout="inline">
        <a-form-item label="应用ID">
          <a-input v-model:value="searchParams.appId" placeholder="请输入应用ID" />
        </a-form-item>
        <a-form-item label="用户ID">
          <a-input v-model:value="searchParams.userId" placeholder="请输入用户ID" />
        </a-form-item>
        <a-form-item label="消息类型">
          <a-select v-model:value="searchParams.messageType" placeholder="请选择消息类型" style="width: 120px" allowClear>
            <a-select-option value="user">用户</a-select-option>
            <a-select-option value="ai">AI</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-table
      :columns="columns"
      :data-source="dataList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'message'">
          <a-tooltip :title="record.message">
            <span class="message-preview">{{ record.message }}</span>
          </a-tooltip>
        </template>

        <template v-if="column.key === 'messageType'">
          <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'">
            {{ record.messageType === 'user' ? '用户' : 'AI' }}
          </a-tag>
        </template>

        <template v-if="column.key === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>

        <template v-if="column.key === 'action'">
          <a-space>
            <a-popconfirm
              title="确定要删除这条对话记录吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import { listChatHistoryByPageAdmin, remove } from '@/api/chatHistoryController'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    key: 'appId',
    width: 100,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    key: 'userId',
    width: 100,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    key: 'message',
    ellipsis: true,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    key: 'messageType',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
  },
]

const dataList = ref<API.ChatHistory[]>([])
const loading = ref(false)
const searchParams = reactive({
  appId: '',
  userId: '',
  messageType: undefined as string | undefined,
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await listChatHistoryByPageAdmin({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      appId: searchParams.appId || undefined, // 直接使用 string 类型，不转换避免精度丢失
      userId: searchParams.userId || undefined, // 直接使用 string 类型，不转换避免精度丢失
      messageType: searchParams.messageType,
      sortField: 'createTime',
      sortOrder: 'descend',
    })

    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records || []
      pagination.total = res.data.data.totalRow || 0
    } else {
      message.error('加载失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchParams.appId = ''
  searchParams.userId = ''
  searchParams.messageType = undefined
  pagination.current = 1
  loadData()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 删除
const handleDelete = async (id?: number) => {
  if (!id) return

  try {
    const res = await remove({ id })
    if (res.data) {
      message.success('删除成功')
      loadData()
    } else {
      message.error('删除失败')
    }
  } catch (error) {
    message.error('删除失败')
  }
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.chat-history-manage-page {
  padding: 24px;
}

h2 {
  margin-bottom: 24px;
   text-align: center;
}

.message-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}
</style>
