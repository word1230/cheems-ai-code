<template>
  <div class="app-manage-page">
    <h2>应用管理</h2>
    
    <a-card style="margin-bottom: 16px">
      <a-form layout="inline">
        <a-form-item label="应用名称">
          <a-input v-model:value="searchParams.appName" placeholder="请输入应用名称" />
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
        <template v-if="column.key === 'cover'">
          <img v-if="record.cover" :src="record.cover" style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px" />
          <span v-else>-</span>
        </template>
        
        <template v-if="column.key === 'priority'">
          <a-tag v-if="record.priority >= 99" color="gold">精选</a-tag>
          <span v-else>{{ record.priority }}</span>
        </template>
        
        <template v-if="column.key === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" size="small" @click="handleFeature(record)">
              {{ record.priority >= 99 ? '取消精选' : '精选' }}
            </a-button>
            <a-popconfirm
              title="确定要删除这个应用吗？"
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
import { useRouter } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'
import { listAppVoByPageAdmin, deleteAppByAdmin, updateApp } from '@/api/appController'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
  },
  {
    title: '封面',
    dataIndex: 'cover',
    key: 'cover',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
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
    width: 240,
  },
]

const dataList = ref<API.AppVO[]>([])
const loading = ref(false)
const searchParams = reactive({
  appName: '',
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
    const res = await listAppVoByPageAdmin({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      appName: searchParams.appName || undefined,
      sortField: 'createTime',
      sortOrder: 'desc',
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
  searchParams.appName = ''
  pagination.current = 1
  loadData()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 编辑
const handleEdit = (record: API.AppVO) => {
  router.push(`/app/edit/${record.id}`)
}

// 精选/取消精选
const handleFeature = async (record: API.AppVO) => {
  try {
    const newPriority = record.priority && record.priority >= 99 ? 0 : 99
    const res = await updateApp({
      id: record.id,
      appName: record.appName,
      cover: record.cover,
      priority: newPriority,
    })
    
    if (res.data.code === 0) {
      message.success(newPriority >= 99 ? '已设为精选' : '已取消精选')
      loadData()
    } else {
      message.error('操作失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('操作失败')
  }
}

// 删除
const handleDelete = async (id?: string) => {
  if (!id) return
  
  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      loadData()
    } else {
      message.error('删除失败：' + res.data.msg)
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
.app-manage-page {
  padding: 24px;
}

h2 {
  margin-bottom: 24px;
}
</style>
