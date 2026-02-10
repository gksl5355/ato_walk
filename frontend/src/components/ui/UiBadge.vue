<template>
  <span :class="['badge', `badge--${tone}`]">
    <slot />
  </span>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ tone?: 'sky' | 'sun' | 'ink' | 'danger' }>(), { tone: 'sky' })
</script>

<style scoped>
.badge {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: -0.01em;
  border: 1px solid rgba(27, 31, 35, 0.12);
  background: rgba(255, 255, 255, 0.75);
}

.badge--sky {
  background: rgba(88, 169, 224, 0.16);
}

.badge--sun {
  background: rgba(246, 195, 56, 0.2);
}

.badge--ink {
  background: rgba(27, 31, 35, 0.06);
}

.badge--danger {
  background: rgba(196, 61, 61, 0.12);
}
</style>
