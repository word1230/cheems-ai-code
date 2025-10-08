<template>
  <div class="markdown-render" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'

interface Props {
  content: string
}

const props = defineProps<Props>()

// 渲染 Markdown 内容
const renderedContent = computed(() => {
  if (!props.content) return ''

  try {
    // 手动处理代码高亮
    let content = props.content

    // 使用 marked 解析 Markdown，暂时不启用代码高亮
    const rawHtml = marked.parse(content, {
      breaks: true,
      gfm: true
    }) as string

    // 手动处理代码高亮
    const processedHtml = rawHtml.replace(/<pre><code class="language-(\w+)">([\s\S]*?)<\/code><\/pre>/g,
      (match, lang, code) => {
        try {
          const decodedCode = code.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"')
          const highlighted = hljs.getLanguage(lang)
            ? hljs.highlight(decodedCode, { language: lang }).value
            : hljs.highlightAuto(decodedCode).value
          return `<pre><code class="hljs language-${lang}">${highlighted}</code></pre>`
        } catch (err) {
          console.error('代码高亮失败:', err)
          return match
        }
      }
    )

    // 使用 DOMPurify 清理 HTML，防止 XSS 攻击
    return DOMPurify.sanitize(processedHtml, {
      ALLOWED_TAGS: [
        'p', 'br', 'strong', 'em', 'u', 'strike', 'code', 'pre',
        'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
        'ul', 'ol', 'li',
        'blockquote',
        'table', 'thead', 'tbody', 'tr', 'th', 'td',
        'a', 'img',
        'div', 'span'
      ],
      ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class'],
      ALLOW_DATA_ATTR: false
    })
  } catch (error) {
    console.error('Markdown 渲染失败:', error)
    return props.content
  }
})
</script>

<style scoped>
.markdown-render {
  word-break: break-word;
  line-height: 1.6;
}

/* 标题样式 */
.markdown-render :deep(h1),
.markdown-render :deep(h2),
.markdown-render :deep(h3),
.markdown-render :deep(h4),
.markdown-render :deep(h5),
.markdown-render :deep(h6) {
  margin: 24px 0 16px 0;
  font-weight: 600;
  color: #1a1a1a;
}

.markdown-render :deep(h1) { font-size: 28px; }
.markdown-render :deep(h2) { font-size: 24px; }
.markdown-render :deep(h3) { font-size: 20px; }
.markdown-render :deep(h4) { font-size: 18px; }
.markdown-render :deep(h5) { font-size: 16px; }
.markdown-render :deep(h6) { font-size: 14px; }

/* 段落样式 */
.markdown-render :deep(p) {
  margin: 16px 0;
  color: #333;
}

/* 链接样式 */
.markdown-render :deep(a) {
  color: #1890ff;
  text-decoration: none;
  transition: color 0.2s;
}

.markdown-render :deep(a:hover) {
  color: #40a9ff;
  text-decoration: underline;
}

/* 列表样式 */
.markdown-render :deep(ul),
.markdown-render :deep(ol) {
  margin: 16px 0;
  padding-left: 24px;
}

.markdown-render :deep(li) {
  margin: 8px 0;
  color: #333;
}

/* 引用样式 */
.markdown-render :deep(blockquote) {
  margin: 16px 0;
  padding: 12px 16px;
  background-color: #f6f8fa;
  border-left: 4px solid #dfe2e5;
  color: #6a737d;
}

/* 代码样式 */
.markdown-render :deep(code) {
  background-color: #f6f8fa;
  color: #d73a49;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 85%;
  font-family: 'SFMono-Regular', 'Consolas', 'Liberation Mono', 'Menlo', monospace;
}

/* 代码块样式 */
.markdown-render :deep(pre) {
  margin: 16px 0;
  border-radius: 6px;
  overflow: auto;
  background-color: #f6f8fa;
  border: 1px solid #d1d5da;
}

.markdown-render :deep(pre code) {
  background-color: transparent;
  color: inherit;
  padding: 16px;
  border-radius: 0;
  font-size: 14px;
  line-height: 1.45;
  overflow: visible;
  word-wrap: normal;
}

/* 代码高亮主题样式 */
.markdown-render :deep(.hljs) {
  display: block;
  padding: 16px;
  overflow-x: auto;
  background: #f6f8fa !important;
  color: #24292e;
}

/* 通用高亮样式 */
.markdown-render :deep(.hljs-comment),
.markdown-render :deep(.hljs-quote) {
  color: #6a737d;
  font-style: italic;
}

.markdown-render :deep(.hljs-keyword),
.markdown-render :deep(.hljs-selector-tag),
.markdown-render :deep(.hljs-subst) {
  color: #d73a49;
}

.markdown-render :deep(.hljs-number),
.markdown-render :deep(.hljs-literal),
.markdown-render :deep(.hljs-variable),
.markdown-render :deep(.hljs-template-variable),
.markdown-render :deep(.hljs-tag .hljs-attr) {
  color: #005cc5;
}

.markdown-render :deep(.hljs-string),
.markdown-render :deep(.hljs-doctag) {
  color: #032f62;
}

.markdown-render :deep(.hljs-title),
.markdown-render :deep(.hljs-section),
.markdown-render :deep(.hljs-selector-id) {
  color: #6f42c1;
  font-weight: bold;
}

.markdown-render :deep(.hljs-subst) {
  font-weight: normal;
}

.markdown-render :deep(.hljs-type),
.markdown-render :deep(.hljs-class .hljs-title),
.markdown-render :deep(.hljs-tag),
.markdown-render :deep(.hljs-regexp),
.markdown-render :deep(.hljs-link) {
  color: #22863a;
}

.markdown-render :deep(.hljs-symbol),
.markdown-render :deep(.hljs-bullet),
.markdown-render :deep(.hljs-built_in),
.markdown-render :deep(.hljs-builtin-name) {
  color: #e36209;
}

.markdown-render :deep(.hljs-meta),
.markdown-render :deep(.hljs-deletion) {
  color: #b31d28;
}

.markdown-render :deep(.hljs-addition) {
  color: #22863a;
}

.markdown-render :deep(.hljs-emphasis) {
  font-style: italic;
}

.markdown-render :deep(.hljs-strong) {
  font-weight: bold;
}

/* 表格样式 */
.markdown-render :deep(table) {
  margin: 16px 0;
  border-collapse: collapse;
  width: 100%;
  background-color: white;
  border: 1px solid #d1d5da;
  border-radius: 6px;
  overflow: hidden;
}

.markdown-render :deep(th),
.markdown-render :deep(td) {
  padding: 12px 16px;
  border: 1px solid #d1d5da;
  text-align: left;
}

.markdown-render :deep(th) {
  background-color: #f6f8fa;
  font-weight: 600;
  color: #24292e;
}

.markdown-render :deep(tr:nth-child(even)) {
  background-color: #f6f8fa;
}

/* 图片样式 */
.markdown-render :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 8px 0;
}

/* 分隔线样式 */
.markdown-render :deep(hr) {
  height: 1px;
  border: none;
  background-color: #d1d5da;
  margin: 24px 0;
}

/* 任务列表样式 */
.markdown-render :deep(input[type="checkbox"]) {
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .markdown-render :deep(pre) {
    padding: 8px;
  }

  .markdown-render :deep(pre code) {
    padding: 8px;
    font-size: 12px;
  }

  .markdown-render :deep(table) {
    font-size: 14px;
  }

  .markdown-render :deep(th),
  .markdown-render :deep(td) {
    padding: 8px 12px;
  }
}
</style>