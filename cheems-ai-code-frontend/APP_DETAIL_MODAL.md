# 应用详情弹窗功能说明

## 功能概述

在应用对话页面的顶部添加"应用详情"按钮，点击后弹出模态框展示应用的详细信息和操作选项。

## 功能位置

```
对话页顶部栏
├── 左侧：返回按钮 + 应用名称
└── 右侧：应用详情按钮 + 部署按钮
```

## 弹窗内容

### 1. 应用基础信息

#### 显示内容
- **应用名称**：当前应用的名称
- **创建者**：
  - 头像（Avatar）
  - 昵称（用户名）
- **创建时间**：格式化的完整时间（年-月-日 时:分:秒）
- **部署状态**：
  - 已部署（绿色标签）- 有 deployKey
  - 未部署（灰色标签）- 无 deployKey

#### 实现代码
```vue
<div class="detail-section">
  <h4>应用基础信息</h4>
  <div class="detail-item">
    <span class="detail-label">应用名称：</span>
    <span class="detail-value">{{ appInfo.appName }}</span>
  </div>
  <div class="detail-item">
    <span class="detail-label">创建者：</span>
    <div class="creator-info">
      <a-avatar :src="creatorInfo?.userAvatar" size="small">
        {{ creatorInfo?.userName?.charAt(0) }}
      </a-avatar>
      <span>{{ creatorInfo?.userName }}</span>
    </div>
  </div>
  <div class="detail-item">
    <span class="detail-label">创建时间：</span>
    <span class="detail-value">{{ formatDetailTime(appInfo.createTime) }}</span>
  </div>
  <div class="detail-item">
    <span class="detail-label">部署状态：</span>
    <a-tag :color="appInfo.deployKey ? 'success' : ''">
      {{ appInfo.deployKey ? '已部署' : '未部署' }}
    </a-tag>
  </div>
</div>
```

### 2. 操作栏（权限控制）

#### 显示条件
仅当满足以下任一条件时显示：
- 当前用户是应用的创建者（本人）
- 当前用户是管理员

#### 操作按钮
1. **修改按钮**
   - 点击后关闭弹窗
   - 跳转到应用编辑页面 `/app/edit/{appId}`

2. **删除按钮**
   - 带二次确认的 Popconfirm
   - 确认后调用删除接口
   - 删除成功后跳转到主页

#### 权限判断逻辑
```typescript
const canManage = computed(() => {
  return isOwner.value || loginUserStore.loginUser.userRole === 'admin'
})
```

## 数据加载

### 1. 应用信息
应用信息在页面加载时已经获取，直接使用 `appInfo.value`

### 2. 创建者信息
需要额外调用接口获取：

```typescript
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
```

在加载应用信息后自动调用：
```typescript
if (appInfo.value.userId) {
  loadCreatorInfo(appInfo.value.userId)
}
```

## 时间格式化

使用本地化的完整时间格式：

```typescript
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
```

输出示例：`2025-01-07 14:30:45`

## 操作功能

### 1. 修改应用
```typescript
const handleEdit = () => {
  showDetailModal.value = false  // 关闭弹窗
  router.push(`/app/edit/${appId}`)  // 跳转到编辑页
}
```

### 2. 删除应用
```typescript
const handleDelete = async () => {
  try {
    const res = await deleteApp({ id: appId })
    if (res.data.code === 0) {
      message.success('删除成功')
      showDetailModal.value = false
      router.push('/')  // 跳转到主页
    } else {
      message.error('删除失败：' + res.data.msg)
    }
  } catch (error) {
    message.error('删除失败')
  }
}
```

## 样式设计

### 布局结构
```
弹窗内容
├── 应用基础信息区域
│   ├── 标题（带下划线）
│   └── 信息列表（标签 + 值）
└── 操作区域（可选）
    ├── 标题（带下划线）
    └── 按钮组
```

### 关键样式
```css
.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
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
```

## 交互流程

### 查看详情流程
1. 用户点击"应用详情"按钮
2. 弹出模态框
3. 显示应用基础信息
4. 如果有权限，显示操作按钮
5. 用户可以关闭弹窗或执行操作

### 修改流程
1. 点击"修改"按钮
2. 关闭弹窗
3. 跳转到编辑页面
4. 用户修改信息并保存

### 删除流程
1. 点击"删除"按钮
2. 弹出二次确认对话框
3. 用户确认删除
4. 调用删除接口
5. 删除成功后跳转到主页

## 权限矩阵

| 用户类型 | 查看详情 | 修改 | 删除 |
|---------|---------|------|------|
| 应用创建者 | ✅ | ✅ | ✅ |
| 管理员 | ✅ | ✅ | ✅ |
| 其他用户 | ✅ | ❌ | ❌ |

## 使用的 API

1. **getUserVoById** - 获取用户信息
   - 用于加载创建者信息
   - 参数：`{ id: userId }`

2. **deleteApp** - 删除应用
   - 用于删除应用
   - 参数：`{ id: appId }`

## 使用的图标

- `InfoCircleOutlined` - 应用详情按钮
- `EditOutlined` - 修改按钮
- `DeleteOutlined` - 删除按钮

## 注意事项

1. **权限控制**
   - 操作栏仅对有权限的用户显示
   - 使用 `v-if="canManage"` 控制

2. **数据加载**
   - 创建者信息异步加载
   - 加载失败不影响其他信息显示

3. **删除确认**
   - 使用 Popconfirm 组件
   - 防止误操作

4. **跳转处理**
   - 删除成功后跳转到主页
   - 修改时跳转到编辑页

5. **弹窗关闭**
   - 点击遮罩层可关闭
   - 点击右上角 X 可关闭
   - 执行操作后自动关闭

## 测试场景

- ✅ 应用创建者查看详情
- ✅ 管理员查看详情
- ✅ 其他用户查看详情（无操作栏）
- ✅ 创建者修改应用
- ✅ 创建者删除应用
- ✅ 管理员修改应用
- ✅ 管理员删除应用
- ✅ 删除确认对话框
- ✅ 删除成功后跳转
- ✅ 创建者信息正确显示
- ✅ 部署状态正确显示
- ✅ 时间格式正确
