import { defineStore } from 'pinia'
import { ref } from 'vue'

export type Toast = {
  id: string
  title: string
  message?: string
  tone: 'info' | 'success' | 'error'
}

function makeId() {
  return `${Date.now()}_${Math.random().toString(16).slice(2)}`
}

export const useToastStore = defineStore('toasts', () => {
  const items = ref<Toast[]>([])

  function push(toast: Omit<Toast, 'id'>, opts?: { ttlMs?: number }) {
    const id = makeId()
    items.value = [{ id, ...toast }, ...items.value].slice(0, 5)

    const ttl = opts?.ttlMs ?? 3500
    window.setTimeout(() => dismiss(id), ttl)
  }

  function dismiss(id: string) {
    items.value = items.value.filter((t) => t.id !== id)
  }

  return { items, push, dismiss }
})
