import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const STORAGE_KEY = 'ato_walk_auth'

type StoredAuth = {
  email: string
}

function readStoredAuth(): StoredAuth | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) {
      return null
    }
    const parsed = JSON.parse(raw) as unknown
    if (typeof parsed !== 'object' || parsed === null) {
      return null
    }
    const p = parsed as { email?: unknown }
    if (typeof p.email !== 'string' || p.email.trim() === '') {
      return null
    }
    return { email: p.email }
  } catch {
    return null
  }
}

function writeStoredAuth(value: StoredAuth | null) {
  if (!value) {
    localStorage.removeItem(STORAGE_KEY)
    return
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
}

export const useAuthStore = defineStore('auth', () => {
  const stored = readStoredAuth()
  const email = ref<string>(stored?.email ?? '')
  const isLoggedIn = computed(() => email.value.trim().length > 0)

  function login(nextEmail: string) {
    email.value = nextEmail.trim()
    writeStoredAuth({ email: email.value })
  }

  function logout() {
    email.value = ''
    writeStoredAuth(null)
  }

  return { email, isLoggedIn, login, logout }
})
