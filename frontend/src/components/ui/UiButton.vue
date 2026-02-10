<template>
  <button :class="['btn', `btn--${variant}`, `btn--${size}`]" :type="type" :disabled="disabled">
    <slot />
  </button>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    variant?: 'primary' | 'ghost' | 'soft'
    size?: 'sm' | 'md'
    type?: 'button' | 'submit' | 'reset'
    disabled?: boolean
  }>(),
  {
    variant: 'primary',
    size: 'md',
    type: 'button',
    disabled: false,
  },
)
</script>

<style scoped>
.btn {
  border: 1px solid transparent;
  border-radius: 999px;
  cursor: pointer;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease,
    background-color 120ms ease,
    border-color 120ms ease;
  user-select: none;
  letter-spacing: -0.01em;
  font-weight: 600;
}

.btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.btn--md {
  height: 44px;
  padding: 0 16px;
}

.btn--sm {
  height: 34px;
  padding: 0 12px;
  font-size: 13px;
}

.btn--primary {
  background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.4), transparent 50%),
    linear-gradient(180deg, #77c4f3 0%, var(--c-sky) 100%);
  color: #0f1720;
  border-color: rgba(15, 23, 32, 0.1);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.1), 0 12px 32px rgba(88, 169, 224, 0.24);
}

.btn--primary:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.1), 0 18px 46px rgba(88, 169, 224, 0.3);
}

.btn--soft {
  background: rgba(255, 255, 255, 0.72);
  color: var(--c-ink);
  border-color: rgba(27, 31, 35, 0.12);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.06);
}

.btn--soft:not(:disabled):hover {
  transform: translateY(-1px);
}

.btn--ghost {
  background: transparent;
  color: var(--c-ink-2);
  border-color: rgba(27, 31, 35, 0.12);
}

.btn--ghost:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.62);
  color: var(--c-ink);
}
</style>
