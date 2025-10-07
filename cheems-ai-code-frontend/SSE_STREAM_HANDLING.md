# SSE 流式输出处理说明

## 问题描述
应用对话页左侧的 AI 回复使用 SSE (Server-Sent Events) 流式输出，需要正确解析后端返回的数据格式，提取实际内容并在一个聊天气泡中展示。

## SSE 数据格式

### 标准 SSE 格式
```
data: {"content": "这是AI的回复"}

data: {"content": "继续输出..."}

```

### 可能的 JSON 字段
后端可能使用不同的字段名返回内容：
- `content` - 内容字段
- `message` - 消息字段
- `text` - 文本字段
- `data` - 数据字段

## 实现逻辑

### 1. 流式读取
```typescript
const reader = response.body.getReader()
const decoder = new TextDecoder()
let buffer = ''

while (true) {
  const { done, value } = await reader.read()
  if (done) break
  
  const chunk = decoder.decode(value, { stream: true })
  buffer += chunk
  // 处理数据...
}
```

### 2. 按行分割
```typescript
const lines = buffer.split('\n')
buffer = lines.pop() || '' // 保留最后一个不完整的行
```

### 3. 解析 SSE 格式
```typescript
for (const line of lines) {
  if (line.startsWith('data:')) {
    const jsonStr = line.substring(5).trim()
    // 解析 JSON...
  }
}
```

### 4. 提取内容
```typescript
try {
  const data = JSON.parse(jsonStr)
  // 尝试多个可能的字段名
  const content = data.content || data.message || data.text || data.data || jsonStr
  aiResponse += content
} catch (e) {
  // 如果不是 JSON，直接使用原始内容
  aiResponse += jsonStr
}
```

### 5. 更新 UI
```typescript
messages.value[aiMessageIndex].content = aiResponse
await nextTick()
scrollToBottom()
```

## 完整流程

```
后端 SSE 流 → 前端 fetch 接收 → 按行分割 → 解析 JSON → 提取内容 → 累加到消息 → 更新 UI → 滚动到底部
```

## 关键特性

### 1. 累加显示
所有流式输出的内容累加到同一个聊天气泡中：
```typescript
let aiResponse = ''  // 累加器
// 每次接收到新内容
aiResponse += content
messages.value[aiMessageIndex].content = aiResponse
```

### 2. 容错处理
- 如果 JSON 解析失败，使用原始文本
- 支持多种 JSON 字段名
- 处理不完整的行（保留在 buffer 中）

### 3. 实时更新
- 使用 `nextTick()` 确保 DOM 更新
- 自动滚动到底部显示最新内容

### 4. 生成完成后的处理
```typescript
// 流式输出完成后，显示预览
if (appInfo.value?.codeGenType && appInfo.value?.id) {
  previewUrl.value = `http://localhost:8123/api/static/${appInfo.value.codeGenType}_${appInfo.value.id}/`
}
```

## 支持的数据格式

### 格式 1: 标准 SSE + JSON
```
data: {"content": "Hello"}

data: {"content": " World"}

```

### 格式 2: 纯 JSON（每行一个）
```json
{"content": "Hello"}
{"content": " World"}
```

### 格式 3: 纯文本
```
Hello
World
```

### 格式 4: 混合格式
```
data: {"message": "开始生成..."}

data: 生成中...

data: {"content": "完成！"}

```

## 调试建议

如果流式输出显示不正确，可以添加调试日志：

```typescript
console.log('Raw chunk:', chunk)
console.log('Parsed line:', line)
console.log('Extracted content:', content)
console.log('Accumulated response:', aiResponse)
```

## 注意事项

1. **不要使用 Axios**：Axios 不支持流式响应，必须使用原生 `fetch` API
2. **编码问题**：使用 `TextDecoder` 正确处理 UTF-8 编码
3. **行分割**：SSE 使用 `\n\n` 分隔事件，但我们按 `\n` 分割以支持更多格式
4. **Buffer 管理**：保留不完整的行在 buffer 中，等待下一次数据到达
5. **错误处理**：JSON 解析失败时降级到纯文本模式

## 测试场景

- ✅ 标准 SSE 格式
- ✅ JSON 格式（多种字段名）
- ✅ 纯文本格式
- ✅ 混合格式
- ✅ 中文内容
- ✅ 长文本
- ✅ 快速流式输出
- ✅ 慢速流式输出
