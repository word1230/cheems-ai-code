# Cheems AI Code 前端项目结构

## 项目概述
这是一个基于 Vue 3 + TypeScript + Ant Design Vue 的 AI 代码生成平台前端项目。

## 技术栈
- Vue 3 (Composition API)
- TypeScript
- Ant Design Vue 4.x
- Vue Router 4
- Pinia (状态管理)
- Axios (HTTP 请求)
- Vite (构建工具)

## 目录结构

```
src/
├── api/                      # API 接口
│   ├── appController.ts      # 应用相关接口
│   ├── userController.ts     # 用户相关接口
│   ├── healthController.ts   # 健康检查接口
│   ├── typings.d.ts          # API 类型定义
│   └── index.ts              # API 导出
├── assets/                   # 静态资源
├── components/               # 公共组件
│   ├── GlobalHeader.vue      # 全局头部
│   ├── GlobalFooter.vue      # 全局底部
│   └── AppCard.vue           # 应用卡片组件
├── layouts/                  # 布局组件
│   └── BasicLayout.vue       # 基础布局
├── pages/                    # 页面组件
│   ├── HomePage.vue          # 主页
│   ├── AppChatPage.vue       # 应用对话页
│   ├── AppEditPage.vue       # 应用编辑页
│   ├── admin/                # 管理员页面
│   │   ├── UserManagePage.vue    # 用户管理
│   │   └── AppManagePage.vue     # 应用管理
│   └── user/                 # 用户页面
│       ├── UserLoginPage.vue     # 用户登录
│       └── UserRegisterPage.vue  # 用户注册
├── router/                   # 路由配置
│   └── index.ts
├── stores/                   # 状态管理
│   └── loginUser.ts          # 登录用户状态
├── access.ts                 # 权限控制
├── request.ts                # Axios 配置
├── App.vue                   # 根组件
└── main.ts                   # 入口文件
```

## 核心功能

### 1. 主页 (HomePage.vue)
- 用户提示词输入框
- 我的应用分页列表
- 精选应用分页列表
- 快速创建应用

### 2. 应用对话页 (AppChatPage.vue)
- 左侧对话区域（用户消息和 AI 消息）
- 右侧网页预览区域
- 支持 SSE 流式输出
- 应用部署功能

### 3. 应用管理页 (AppManagePage.vue)
- 仅管理员可访问
- 应用列表展示
- 编辑、删除、精选操作

### 4. 应用编辑页 (AppEditPage.vue)
- 用户可编辑自己的应用名称
- 管理员可编辑任意应用的名称、封面、优先级

## API 接口说明

### 应用相关接口
- `addApp` - 创建应用
- `deleteApp` - 删除应用（用户）
- `deleteAppByAdmin` - 删除应用（管理员）
- `editApp` - 编辑应用（用户）
- `updateApp` - 更新应用（管理员）
- `getAppById` - 获取应用详情
- `getAppVoById` - 获取应用 VO 详情
- `listAppVoByPage` - 分页查询应用列表
- `listAppVoByPageAdmin` - 管理员分页查询
- `listMyAppVoByPage` - 查询我的应用
- `deployApp` - 部署应用
- `chatToGenCode` - AI 对话生成代码（SSE）

### 用户相关接口
- `userLogin` - 用户登录
- `userRegister` - 用户注册
- `userLogout` - 用户登出
- `getLoginUser` - 获取登录用户信息
- 其他用户管理接口...

## 业务流程

### 创建应用流程
1. 用户在主页输入提示词
2. 调用 `addApp` 接口创建应用，获得应用 ID
3. 跳转到应用对话页 `/app/chat/:id`
4. 自动发送初始提示词给 AI
5. 通过 SSE 接口实时接收 AI 回复
6. 生成完成后在右侧展示网站效果

### 部署应用流程
1. 在对话页点击"部署按钮"
2. 调用 `deployApp` 接口
3. 获得可访问的 URL 地址
4. 显示部署成功提示

### 权限控制
- 普通用户：只能编辑和删除自己的应用
- 管理员：可以管理所有应用，设置精选应用

## 开发说明

### 启动项目
```bash
npm install
npm run dev
```

### 构建项目
```bash
npm run build
```

### 代码规范
```bash
npm run lint
npm run format
```

## 注意事项

1. 后端接口地址配置在 `src/request.ts` 中，默认为 `http://localhost:8123/api`
2. SSE 流式接口需要使用原生 `fetch` API，不能使用 Axios
3. 应用预览地址格式：`http://localhost:8123/api/static/{codeGenType}_{appId}/`
4. 精选应用的判断标准：`priority >= 99`
5. 分页查询默认每页 20 条数据

## 待优化项

1. 添加应用搜索功能
2. 优化移动端适配
3. 添加应用分享功能
4. 支持更多代码生成类型
5. 添加应用预览截图功能
