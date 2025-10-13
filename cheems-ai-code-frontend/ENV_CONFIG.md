# 环境变量配置说明

## 概述

项目已将所有硬编码的域名提取为环境变量，支持不同环境的灵活配置。

## 环境变量文件

### 1. `.env` (通用配置)
```bash
# API 相关配置
VITE_API_BASE_URL=http://localhost:8123/api

# 应用预览相关配置
VITE_PREVIEW_BASE_URL=http://localhost:8123/api/static

# 应用部署相关配置
VITE_DEPLOY_BASE_URL=http://localhost

# OpenAPI 文档相关配置
VITE_OPENAPI_SCHEMA_URL=http://localhost:8123/api/v3/api-docs
```

### 2. `.env.development` (开发环境)
```bash
VITE_API_BASE_URL=http://localhost:8123/api
VITE_PREVIEW_BASE_URL=http://localhost:8123/api/static
VITE_DEPLOY_BASE_URL=http://localhost
VITE_OPENAPI_SCHEMA_URL=http://localhost:8123/api/v3/api-docs
```

### 3. `.env.production` (生产环境)
```bash
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_PREVIEW_BASE_URL=https://preview.yourdomain.com
VITE_DEPLOY_BASE_URL=https://deploy.yourdomain.com
VITE_OPENAPI_SCHEMA_URL=https://api.yourdomain.com/api/v3/api-docs
```

## 环境变量说明

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `VITE_API_BASE_URL` | API 基础地址 | `http://localhost:8123/api` |
| `VITE_PREVIEW_BASE_URL` | 应用预览基础地址 | `http://localhost:8123/api/static` |
| `VITE_DEPLOY_BASE_URL` | 应用部署基础地址 | `http://localhost` |
| `VITE_OPENAPI_SCHEMA_URL` | OpenAPI 文档地址 | `http://localhost:8123/api/v3/api-docs` |

## 使用方式

### 在代码中使用

```typescript
import { API_BASE_URL, PREVIEW_BASE_URL, DEPLOY_BASE_URL } from '@/constants/env'

// 使用环境变量
console.log(API_BASE_URL)
console.log(PREVIEW_BASE_URL)
console.log(DEPLOY_BASE_URL)
```

### 使用工具函数

```typescript
import { buildPreviewUrl, buildDeployUrl, buildChatApiUrl } from '@/constants/env'

// 构建预览 URL
const previewUrl = buildPreviewUrl('html', 123, Date.now())
// 结果: http://localhost:8123/api/static/html_123?t=1234567890

// 构建部署 URL
const deployUrl = buildDeployUrl('abc123')
// 结果: http://localhost/abc123

// 构建聊天 API URL
const chatUrl = buildChatApiUrl('创建一个按钮', 123)
// 结果: http://localhost:8123/api/app/chat/gen/code?userMessage=创建一个按钮&appId=123
```

## 已更新的文件

1. `src/request.ts` - API 基础配置
2. `src/constants/env.ts` - 环境变量配置和工具函数
3. `src/pages/AppChatPage.vue` - 应用聊天页面
4. `src/components/AppCard.vue` - 应用卡片组件
5. `openapi2ts.config.ts` - OpenAPI 配置

## 配置优先级

1. `.env.production` / `.env.development` (最高)
2. `.env`
3. 默认值 (最低)

## 注意事项

1. 所有环境变量必须以 `VITE_` 开头才能在 Vite 项目中使用
2. 修改环境变量后需要重启开发服务器才能生效
3. 生产环境部署前请确保 `.env.production` 文件中的配置正确
4. 敏感信息不要提交到版本控制系统，请使用 `.gitignore` 忽略