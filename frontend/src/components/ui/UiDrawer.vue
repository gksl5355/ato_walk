<template>
  <Teleport to="body">
    <div v-if="open" class="overlay" @mousedown.self="emit('close')">
      <aside class="drawer" role="dialog" aria-modal="true">
        <header class="drawer__head">
          <div class="drawer__title">
            <div class="drawer__kicker">Meetup</div>
            <div class="drawer__main">{{ title }}</div>
          </div>
          <UiButton size="sm" variant="ghost" @click="emit('close')">Close</UiButton>
        </header>
        <div class="drawer__body">
          <slot />
        </div>
      </aside>
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
  background: rgba(15, 23, 32, 0.26);
  backdrop-filter: blur(8px);
  z-index: 120;
}

.drawer {
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  width: min(520px, 100%);
  background: rgba(255, 255, 255, 0.88);
  border-left: 1px solid rgba(27, 31, 35, 0.14);
  box-shadow: var(--sh-2);
  display: grid;
  grid-template-rows: auto 1fr;
  animation: slideIn 160ms ease both;
}

.drawer__head {
  padding: var(--s-4);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(27, 31, 35, 0.12);
}

.drawer__kicker {
  font-size: 12px;
  color: var(--c-ink-2);
}

.drawer__main {
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: -0.02em;
}

.drawer__body {
  padding: var(--s-4);
  overflow: auto;
}

@keyframes slideIn {
  from {
    transform: translateX(12px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 640px) {
  .drawer {
    width: 100%;
    border-left: none;
    border-top: 1px solid rgba(27, 31, 35, 0.14);
    top: auto;
    bottom: 0;
    height: min(86vh, 760px);
    border-radius: 22px 22px 0 0;
    animation: rise 180ms ease both;
  }

  @keyframes rise {
    from {
      transform: translateY(14px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }
}
</style>
