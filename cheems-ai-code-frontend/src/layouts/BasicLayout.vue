<template>
  <a-layout class="layout">
    <GlobalHeader />

        <RouterView />

    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  padding: 24px;
  background: #f0f2f5;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  min-height: calc(100vh - 64px - 70px - 48px);
}

@media (max-width: 768px) {
  .content {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    min-height: calc(100vh - 64px - 70px - 32px);
  }
}
</style>
