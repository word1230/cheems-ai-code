# ID 类型精度修复说明

## 问题描述
后端返回的 ID 是 `long` 类型（64位整数），在 JSON 序列化时会转为 `string` 类型以避免 JavaScript 中 `Number` 类型的精度丢失问题（JavaScript 的 Number 只能安全表示 53 位整数）。

如果前端将 ID 转为 `Number` 类型，会导致精度丢失，从而无法正确获取应用信息。

## 修复内容

### 1. 类型定义修复 (src/api/typings.d.ts)

将所有 ID 相关的类型从 `number` 改为 `string`：

- `LoginUserVO.id`: `number` → `string`
- `User.id`: `number` → `string`
- `User.userId`: `number` → `string`
- `UserVO.id`: `number` → `string`
- `App.id`: `number` → `string`
- `App.userId`: `number` → `string`
- `AppVO.id`: `number` → `string`
- `AppVO.userId`: `number` → `string`
- `DeleteRequest.id`: `number` → `string`
- `AppEditRequest.id`: `number` → `string`
- `AppUpdateRequest.id`: `number` → `string`
- `AppDeployRequest.appId`: `number` → `string`
- `UserUpdateRequest.id`: `number` → `string`
- `BaseResponseLong.data`: `number` → `string`
- 所有查询参数中的 `id` 和 `userId` 字段

### 2. 页面代码修复

#### HomePage.vue
```typescript
// 修复前
const goToChat = (appId: number) => {
  router.push(`/app/chat/${appId}`)
}

// 修复后
const goToChat = (appId: string) => {
  router.push(`/app/chat/${appId}`)
}
```

#### AppChatPage.vue
```typescript
// 修复前
const appId = Number(route.params.id)

// 修复后
const appId = route.params.id as string
```

```typescript
// 修复前
const response = await fetch(
  `http://localhost:8123/api/app/chat/gen/code?userMessage=${encodeURIComponent(prompt)}&appId=${appId}`,
  ...
)

// 修复后
const response = await fetch(
  `http://localhost:8123/api/app/chat/gen/code?userMessage=${encodeURIComponent(prompt)}&appId=${encodeURIComponent(appId)}`,
  ...
)
```

#### AppEditPage.vue
```typescript
// 修复前
const appId = Number(route.params.id)

// 修复后
const appId = route.params.id as string
```

#### AppManagePage.vue
```typescript
// 修复前
const handleDelete = async (id?: number) => {
  ...
}

// 修复后
const handleDelete = async (id?: string) => {
  ...
}
```

## 重要原则

**在整个项目中，永远不要将 ID 转为 Number 类型！**

### 正确做法 ✅
```typescript
// 从路由参数获取 ID
const appId = route.params.id as string

// 从 API 响应获取 ID
const id = res.data.data  // 已经是 string 类型

// 传递 ID 给 API
await getAppVoById({ id: appId })

// 在 URL 中使用 ID
router.push(`/app/chat/${appId}`)

// 在 fetch URL 中使用 ID
const url = `http://localhost:8123/api/app/chat/gen/code?appId=${encodeURIComponent(appId)}`
```

### 错误做法 ❌
```typescript
// 不要这样做！会丢失精度！
const appId = Number(route.params.id)
const id = parseInt(res.data.data)
const userId = +user.id
```

## 测试验证

修复后，应该能够：
1. ✅ 成功创建应用并获取正确的应用 ID
2. ✅ 正确跳转到应用对话页
3. ✅ 正确加载应用信息
4. ✅ 正确调用 AI 生成代码接口
5. ✅ 正确部署应用
6. ✅ 正确编辑和删除应用

## 注意事项

1. TypeScript 类型检查会确保不会意外将 ID 作为 number 使用
2. 所有 ID 相关的比较应该使用字符串比较
3. 在 URL 参数中使用 ID 时，建议使用 `encodeURIComponent()` 进行编码
4. 数据库中的 ID 是 `bigint` 类型，前端必须使用 `string` 类型来保持精度
