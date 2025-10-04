<script setup lang="ts">

import { health } from '@/api/healthController'

health().then((res) => {
  console.log(res)
})


</script>

<template>
  <main>
    这是主页
    
  </main>
</template>
