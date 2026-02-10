import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

import { getMe, login as loginApi, logout as logoutApi, signup as signupApi, type SignupRequest, type User } from '@/api/auth'
import { toApiClientError } from '@/api/http'

export const useAuthStore = defineStore('auth', () => {
  const me = ref<User | null>(null)
  const hydrating = ref(false)

  const email = computed(() => me.value?.email ?? '')
  const userId = computed(() => me.value?.id ?? null)
  const isLoggedIn = computed(() => me.value !== null)

  function clearAuthState() {
    me.value = null
  }

  async function hydrate() {
    hydrating.value = true
    try {
      me.value = await getMe()
      return true
    } catch (e) {
      const err = toApiClientError(e)
      if (err.code === 'COMMON_AUTH_REQUIRED') {
        clearAuthState()
        return false
      }
      throw err
    } finally {
      hydrating.value = false
    }
  }

  async function login(emailValue: string, password: string) {
    me.value = await loginApi({ email: emailValue.trim(), password })
  }

  async function signup(req: SignupRequest) {
    me.value = await signupApi(req)
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      clearAuthState()
    }
  }

  return { me, email, userId, isLoggedIn, hydrating, hydrate, login, signup, logout, clearAuthState }
})
