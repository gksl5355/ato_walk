<template>
  <div class="host" aria-live="polite" aria-relevant="additions removals">
    <div v-for="t in toasts.items" :key="t.id" :class="['toast', `toast--${t.tone}`]">
      <div class="toast__title">{{ t.title }}</div>
      <div v-if="t.message" class="toast__message">{{ t.message }}</div>
      <button class="toast__close" type="button" @click="toasts.dismiss(t.id)">×</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useToastStore } from '@/stores/toasts'

const toasts = useToastStore()
</script>

<style scoped>
.host {
  position: fixed;
  right: 16px;
  bottom: 16px;
  display: grid;
  gap: 10px;
  z-index: 200;
  width: min(360px, calc(100vw - 32px));
}

.toast {
  position: relative;
  border-radius: 18px;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(27, 31, 35, 0.14);
  box-shadow: var(--sh-1);
  overflow: hidden;
}

.toast::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: rgba(27, 31, 35, 0.2);
}

.toast--success::before {
  background: rgba(35, 160, 112, 0.75);
}

.toast--error::before {
  background: rgba(196, 61, 61, 0.75);
}

.toast--info::before {
  background: rgba(88, 169, 224, 0.75);
}

.toast__title {
  font-weight: 800;
  letter-spacing: -0.02em;
  padding-right: 18px;
}

.toast__message {
  margin-top: 4px;
  font-size: 13px;
  color: var(--c-ink-2);
  padding-right: 18px;
}

.toast__close {
  position: absolute;
  right: 10px;
  top: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 18px;
  color: rgba(27, 31, 35, 0.58);
}
</style>
