<template>
  <Teleport to="body">
    <div v-if="open" class="overlay" @mousedown.self="emit('close')">
      <div class="modal" role="dialog" aria-modal="true">
        <header class="modal__head">
          <h2 class="modal__title">{{ title }}</h2>
          <UiButton size="sm" variant="ghost" @click="emit('close')">Close</UiButton>
        </header>
        <div class="modal__body">
          <slot />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import UiButton from '@/components/ui/UiButton.vue'

defineProps<{ open: boolean; title: string }>()
const emit = defineEmits<{ (e: 'close'): void }>()
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 32, 0.36);
  backdrop-filter: blur(8px);
  display: grid;
  place-items: center;
  padding: var(--s-4);
  z-index: 100;
}

.modal {
  width: min(720px, 100%);
  border-radius: var(--r-lg);
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(27, 31, 35, 0.14);
  box-shadow: var(--sh-2);
}

.modal__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--s-4);
  border-bottom: 1px solid rgba(27, 31, 35, 0.12);
}

.modal__title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: -0.02em;
}

.modal__body {
  padding: var(--s-4);
}
</style>
