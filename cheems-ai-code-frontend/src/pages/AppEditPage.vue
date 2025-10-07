<template>
  <div class="app-edit-page">
    <a-card title="编辑应用信息">
      <a-form
        :model="formState"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        @finish="handleSubmit"
      >
        <a-form-item label="应用名称" name="appName" :rules="[{ required: true, message: '请输入应用名称' }]">
          <a-input v-model:value="formState.appName" placeholder="请输入应用名称" />
        </a-form-item>

        <a-form-item 
          v-if="isAdmin" 
          label="应用封面" 
          name="cover"
        >
          <a-input v-model:value="formState.cover" placeholder="请输入封面图片URL" />
          <div v-if="formState.cover" style="margin-top: 8px">
            <img :src="formState.cover" style="max-width: 200px; max-height: 200px; border-radius: 4px" />
          </div>
        </a-form-item>

        <a-form-item 
          v-if="isAdmin" 
          label="优先级" 
          name="priority"
        >
          <a-input-number v-model:value="formState.priority" :min="0" :max="100" style="width: 200px" />
          <div style="color: #8c8c8c; margin-top: 4px">优先级 >= 99 将显示为精选应用</div>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-space>
            <a-button type="primary" html-type="submit" :loading="submitting">
              保存
            </a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVoById, editApp, updateApp } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appId = route.params.id as string
const submitting = ref(false)

const formState = reactive({
  appName: '',
  cover: '',
  priority: 0,
})

const isAdmin = computed(() => loginUserStore.loginUser.userRole === 'admin')

// 加载应用信息
const loadAppInfo = async () => {
  try {
    const res = await getAppVoById({ id: appId })
    if (res.data.code === 0 && res.data.data) {
      const app = res.data.data
      formState.appName = app.appName || ''
      formState.cover = app.cover || ''
      formState.priority = app.priority || 0
      
      // 检查权限
      if (!isAdmin.value && app.userId !== loginUserStore.loginUser.id) {
        message.error('无权编辑此应用')
        router.push('/')
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

// 提交表单
const handleSubmit = async () => {
  submitting.value = true
  try {
    let res
    if (isAdmin.value) {
      // 管理员使用 updateApp
      res = await updateApp({
        id: appId,
        appName: formState.appName,
        cover: formState.cover,
        priority: formState.priority,
      })
    } else {
      // 普通用户使用 editApp
      res = await editApp({
        id: appId,
        appName: formState.appName,
      })
    }
    
    if (res.data.code === 0) {
      message.success('保存成功')
      goBack()
    } else {
      message.error('保存失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('保存失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadAppInfo()
})
</script>

<style scoped>
.app-edit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
</style>
