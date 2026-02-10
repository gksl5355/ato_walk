<template>
  <div class="card">
    <slot />
  </div>
</template>

<style scoped>
.card {
  border-radius: var(--r-lg);
  background: var(--c-surface);
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: var(--sh-1);
}
</style>
