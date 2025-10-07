# 应用卡片和权限控制功能说明

## 功能概述

### 1. 应用卡片增强 (AppCard.vue)

#### 新增按钮
- **查看对话**：始终显示，点击跳转到应用对话页面
- **查看作品**：仅当应用有 `deployKey` 时显示，点击新窗口打开部署地址

#### 部署地址
- 格式：`http://localhost:8123/{deployKey}`
- 注意：这是部署后的正式地址，与生成网站的预览地址不同
  - 预览地址：`http://localhost:8123/api/static/{codeGenType}_{appId}/`
  - 部署地址：`http://localhost:8123/{deployKey}`

#### 交互优化
- 点击封面图 → 查看对话
- 点击应用名称 → 查看对话
- 点击"查看对话"按钮 → 查看对话
- 点击"查看作品"按钮 → 新窗口打开部署地址

### 2. 查看模式 (AppChatPage.vue)

#### URL 参数控制
通过 `?view=1` 参数控制是否自动发送消息：

```typescript
// 从主页点击"查看对话"
router.push(`/app/chat/${appId}?view=1`)

// 在对话页判断
const isViewMode = route.query.view === '1'

// 只有非查看模式才自动发送初始提示词
if (!isViewMode && appInfo.value.initPrompt) {
  // 自动发送消息...
}
```

#### 使用场景
- **创建应用后**：不带 `view` 参数，自动发送初始提示词开始生成
- **查看已有应用**：带 `view=1` 参数，不自动发送消息，只展示历史对话

### 3. 权限控制

#### 所有者判断
```typescript
const isOwner = ref(true)

// 加载应用信息后判断
isOwner.value = appInfo.value.userId === loginUserStore.loginUser.id
```

#### 权限限制
**非所有者的限制：**
1. 输入框禁用
2. 发送按钮禁用
3. 鼠标悬停输入框时显示提示："无法在别人的作品下对话哦~"
4. 尝试发送消息时弹出警告

**所有者的权限：**
1. 可以正常输入和发送消息
2. 可以继续与 AI 对话完善应用
3. 可以部署应用

#### 实现细节

**输入框禁用：**
```vue
<a-tooltip 
  v-if="!isOwner" 
  title="无法在别人的作品下对话哦~"
  placement="top"
>
  <a-textarea
    v-model:value="userMessage"
    placeholder="无法在别人的作品下对话哦~"
    :disabled="true"
  />
</a-tooltip>
<a-textarea
  v-else
  v-model:value="userMessage"
  placeholder="描述你想要的功能，可以一步一步完善生成效果..."
  @pressEnter="handleSend"
/>
```

**发送按钮禁用：**
```vue
<a-button 
  type="primary" 
  @click="handleSend" 
  :loading="generating"
  :disabled="!isOwner"
>
  发送
</a-button>
```

**发送前检查：**
```typescript
const handleSend = async () => {
  if (!isOwner.value) {
    message.warning('无法在别人的作品下对话哦~')
    return
  }
  // 继续发送...
}
```

## 完整流程

### 流程 1：创建新应用
1. 用户在主页输入提示词
2. 调用 `addApp` 创建应用
3. 跳转到 `/app/chat/{appId}`（不带 view 参数）
4. 自动发送初始提示词
5. AI 开始生成代码
6. 用户可以继续对话完善

### 流程 2：查看自己的应用
1. 在主页点击应用卡片的"查看对话"
2. 跳转到 `/app/chat/{appId}?view=1`
3. 不自动发送消息
4. 显示历史对话（如果有）
5. 用户可以继续对话

### 流程 3：查看别人的应用
1. 在精选页面点击应用卡片的"查看对话"
2. 跳转到 `/app/chat/{appId}?view=1`
3. 不自动发送消息
4. 输入框禁用，显示提示
5. 只能查看，不能对话

### 流程 4：查看已部署的作品
1. 点击应用卡片的"查看作品"按钮
2. 新窗口打开 `http://localhost:8123/{deployKey}`
3. 查看部署后的正式网站

## 代码结构

### AppCard.vue
```
应用卡片组件
├── 封面图（可点击）
├── 应用名称（可点击）
├── 创建时间
└── 操作按钮
    ├── 查看对话（始终显示）
    └── 查看作品（有 deployKey 时显示）
```

### HomePage.vue
```
主页
├── 英雄区域（输入框）
├── 我的应用列表
│   └── AppCard 组件
└── 精选应用列表
    └── AppCard 组件
```

### AppChatPage.vue
```
对话页
├── 权限判断（isOwner）
├── 查看模式判断（isViewMode）
├── 对话区域
│   ├── 消息列表
│   └── 输入框（根据权限禁用）
└── 预览区域
```

## 注意事项

1. **部署地址 vs 预览地址**
   - 预览地址：开发时查看生成效果
   - 部署地址：正式部署后的访问地址
   - 两者不同，不要混淆

2. **权限检查时机**
   - 加载应用信息后立即检查
   - 发送消息前再次检查
   - UI 层面禁用 + 逻辑层面校验

3. **查看模式判断**
   - 使用 URL 参数而不是状态管理
   - 刷新页面后仍然有效
   - 简单可靠

4. **用户体验**
   - 禁用状态有明确提示
   - 按钮状态清晰
   - 操作反馈及时

## 测试场景

- ✅ 创建新应用后自动生成
- ✅ 查看自己的应用不自动生成
- ✅ 查看别人的应用输入框禁用
- ✅ 非所有者尝试发送消息被拦截
- ✅ 有 deployKey 的应用显示"查看作品"按钮
- ✅ 点击"查看作品"打开正确的部署地址
- ✅ 点击"查看对话"跳转到对话页
- ✅ 鼠标悬停禁用输入框显示提示
