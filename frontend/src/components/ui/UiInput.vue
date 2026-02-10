<template>
  <label class="field">
    <span v-if="label" class="field__label">{{ label }}</span>
    <input class="field__control" v-bind="$attrs" :value="modelValue" @input="onInput" />
  </label>
</template>

<script setup lang="ts">
defineOptions({ inheritAttrs: false })

const props = defineProps<{ modelValue: string; label?: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

function onInput(e: Event) {
  emit('update:modelValue', (e.target as HTMLInputElement).value)
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
