<template>
  <label class="field">
    <span v-if="label" class="field__label">{{ label }}</span>
    <select class="field__control" :value="modelValue" @change="onChange">
      <option v-if="placeholder" value="">{{ placeholder }}</option>
      <slot />
    </select>
  </label>
</template>

<script setup lang="ts">
const props = defineProps<{ modelValue: string; label?: string; placeholder?: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

function onChange(e: Event) {
  emit('update:modelValue', (e.target as HTMLSelectElement).value)
}
</script>

<style scoped>
.field {
  display: grid;
  gap: 8px;
}

.field__label {
  font-size: 13px;
  color: var(--c-ink-2);
}

.field__control {
  height: 44px;
  border-radius: 14px;
  border: 1px solid rgba(27, 31, 35, 0.14);
  background: rgba(255, 255, 255, 0.76);
  padding: 0 12px;
  outline: none;
}

.field__control:focus {
  border-color: rgba(88, 169, 224, 0.6);
  box-shadow: 0 0 0 4px rgba(88, 169, 224, 0.18);
}
</style>
