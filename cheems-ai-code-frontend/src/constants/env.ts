/**
 * 环境变量配置
 */

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

// 应用预览基础地址
export const PREVIEW_BASE_URL = import.meta.env.VITE_PREVIEW_BASE_URL || 'http://localhost:8123/api/static'

// 应用部署基础地址
export const DEPLOY_BASE_URL = import.meta.env.VITE_DEPLOY_BASE_URL || 'http://localhost'

// OpenAPI 文档地址
export const OPENAPI_SCHEMA_URL = import.meta.env.VITE_OPENAPI_SCHEMA_URL || 'http://localhost:8123/api/v3/api-docs'

// 构建应用预览 URL
export const buildPreviewUrl = (codeGenType: string, appId: number | string, timestamp?: number) => {
  const baseUrl = PREVIEW_BASE_URL.endsWith('/') ? PREVIEW_BASE_URL.slice(0, -1) : PREVIEW_BASE_URL
  const url = `${baseUrl}/${codeGenType}_${appId}`
  return timestamp ? `${url}?t=${timestamp}` : url
}

// 构建应用部署 URL
export const buildDeployUrl = (deployKey: string) => {
  const baseUrl = DEPLOY_BASE_URL.endsWith('/') ? DEPLOY_BASE_URL.slice(0, -1) : DEPLOY_BASE_URL
  return `${baseUrl}/${deployKey}`
}

// 构建聊天 API URL
export const buildChatApiUrl = (userMessage: string, appId: number | string) => {
  const baseUrl = API_BASE_URL.endsWith('/') ? API_BASE_URL.slice(0, -1) : API_BASE_URL
  return `${baseUrl}/app/chat/gen/code?userMessage=${encodeURIComponent(userMessage)}&appId=${encodeURIComponent(appId)}`
}