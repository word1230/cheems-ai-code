<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <a
        href="https://github.com/word1230/cheems-ai-code"
        target="_blank"
        rel="noopener noreferrer"
      >
        Github
      </a>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts"></script>

<style scoped>
.footer {
  position: sticky;
  bottom: 0;
  text-align: center;
  background: #f0f2f5;
  padding: 16px 50px;
}

.footer-content a {
  color: #1890ff;
  text-decoration: none;
}

.footer-content a:hover {
  text-decoration: underline;
}
</style>
