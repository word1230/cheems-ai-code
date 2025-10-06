<template>
  <a-layout-header class="header">
    <div class="header-left">
      <img src="@/assets/logo.png" alt="Logo" class="logo" />
      <span class="title">Cheems AI Code</span>
    </div>
    <a-menu
      v-model:selectedKeys="selectedKeys"
      mode="horizontal"
      :style="{ flex: 1, minWidth: 0, borderBottom: 'none' }"
      :items="menuItems"
      @click="handleMenuClick"
    />
    <div class="user-login-status">
  <div v-if="loginUserStore.loginUser.id">
  <a-dropdown>
    <a-space>
      <a-avatar :src="loginUserStore.loginUser.userAvatar" />
      {{ loginUserStore.loginUser.userName ?? '无名' }}
    </a-space>
    <template #overlay>
      <a-menu>
        <a-menu-item @click="doLogout">
          <LogoutOutlined />
          退出登录
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</div>
  <div v-else>
    <a-button type="primary" href="/user/login">登录</a-button>
  </div>
</div>

  </a-layout-header>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/loginUser'
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { HomeOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController'
import { message, type MenuProps } from 'ant-design-vue'


const router = useRouter()
const selectedKeys = ref<string[]>(['route.path'])

const loginUserStore = useLoginUserStore()



// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))


// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}


router.afterEach((to)=>{
  selectedKeys.value = [to.path]
})


// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.msg)
  }
}



</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  margin-right: 32px;
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  white-space: nowrap;
}

.header-right {
  margin-left: auto;
}

@media (max-width: 768px) {
  .title {
    font-size: 16px;
  }

  .logo {
    width: 32px;
    height: 32px;
  }

  .header-left {
    margin-right: 16px;
  }
}
</style>
