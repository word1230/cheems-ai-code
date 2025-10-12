<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <a
        href="https://github.com/word1230/cheems-ai-code"
        target="_blank"
        rel="noopener noreferrer"
      >
        My Github
      </a>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts"></script>

<style scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  background: rgba(240, 242, 245, 0.9);
  backdrop-filter: blur(10px);
  padding: 16px 50px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  z-index: 1000;
}

.footer-content a {
  color: #1890ff;
  text-decoration: none;
}

.footer-content a:hover {
  text-decoration: underline;
}
</style>
