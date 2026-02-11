<template>
  <section class="hero">
    <div class="hero__copy">
      <h1 class="hero__title">
        Make meetups feel
        <span class="hero__titleAccent">safe</span>
        and
        <span class="hero__titleAccent2">easy</span>
      </h1>
      <p class="hero__sub">
        A gentle, structured board for dog-walk meetups. No chaos, just clear steps.
      </p>

      <div class="hero__cta">
        <UiButton @click="goMeetups">Explore meetups</UiButton>
        <UiButton variant="soft" @click="scrollToAuth">Login / Sign up</UiButton>
      </div>
    </div>

    <div class="hero__art" aria-hidden="true">
      <div class="hero__cloud hero__cloud--a" />
      <div class="hero__cloud hero__cloud--b" />
      <img class="hero__paw" :src="pawUrl" alt="" />
      <div class="hero__frame">
        <img class="hero__puppy" :src="puppyUrl" alt="" />
      </div>
    </div>
  </section>

  <section ref="authSection" class="auth">
    <UiCard class="auth__card">
      <div class="auth__head">
        <h2 class="auth__title">Join Ato Walk</h2>
        <p class="auth__note">Sign up with dog survey, or log in with your account.</p>
      </div>

      <div class="auth__tabs">
        <UiButton size="sm" :variant="mode === 'login' ? 'primary' : 'ghost'" @click="mode = 'login'">Login</UiButton>
        <UiButton size="sm" :variant="mode === 'signup' ? 'primary' : 'ghost'" @click="mode = 'signup'">Sign up</UiButton>
      </div>

      <form v-if="mode === 'login'" class="auth__form" @submit.prevent="onLogin">
        <UiInput v-model="loginForm.email" label="Email" type="email" placeholder="ato@ato.com" />
        <UiInput v-model="loginForm.password" label="Password" type="password" placeholder="1234" />
        <div class="auth__actions">
          <UiButton type="submit" :disabled="submitting">{{ submitting ? 'Logging in...' : 'Login' }}</UiButton>
          <UiButton variant="ghost" :disabled="!auth.isLoggedIn || submitting" @click="onLogout">Logout</UiButton>
        </div>
      </form>

      <form v-else class="auth__form" @submit.prevent="onSignup">
        <div class="survey">
          <h3 class="survey__title">Dog survey</h3>
          <UiInput v-model="signupForm.dogName" label="Dog name" placeholder="Ato" />
          <UiInput v-model="signupForm.dogBreed" label="Breed" placeholder="Pomeranian" />

          <div class="survey__grid">
            <div class="radioGroup">
              <div class="radioGroup__label">Size</div>
              <div class="radioGroup__options">
                <label class="radioOption">
                  <input v-model="signupForm.dogSize" type="radio" name="dogSize" value="SMALL" />
                  <span>소형</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogSize" type="radio" name="dogSize" value="MEDIUM" />
                  <span>중형</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogSize" type="radio" name="dogSize" value="LARGE" />
                  <span>대형</span>
                </label>
              </div>
            </div>
            <div class="radioGroup">
              <div class="radioGroup__label">Neutered</div>
              <div class="radioGroup__options">
                <label class="radioOption">
                  <input v-model="signupForm.dogNeutered" type="radio" name="dogNeutered" value="true" />
                  <span>예</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogNeutered" type="radio" name="dogNeutered" value="false" />
                  <span>아니오</span>
                </label>
              </div>
            </div>
            <div class="radioGroup">
              <div class="radioGroup__label">Sociability</div>
              <div class="radioGroup__options">
                <label class="radioOption">
                  <input v-model="signupForm.dogSociabilityLevel" type="radio" name="dogSociability" value="HIGH" />
                  <span>높음</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogSociabilityLevel" type="radio" name="dogSociability" value="MEDIUM" />
                  <span>보통</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogSociabilityLevel" type="radio" name="dogSociability" value="LOW" />
                  <span>낮음</span>
                </label>
              </div>
            </div>
            <div class="radioGroup">
              <div class="radioGroup__label">Reactivity</div>
              <div class="radioGroup__options">
                <label class="radioOption">
                  <input v-model="signupForm.dogReactivityLevel" type="radio" name="dogReactivity" value="LOW" />
                  <span>차분</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogReactivityLevel" type="radio" name="dogReactivity" value="MEDIUM" />
                  <span>보통</span>
                </label>
                <label class="radioOption">
                  <input v-model="signupForm.dogReactivityLevel" type="radio" name="dogReactivity" value="HIGH" />
                  <span>예민</span>
                </label>
              </div>
            </div>
          </div>

          <UiTextarea v-model="signupForm.dogNotes" label="Notes (optional)" placeholder="Anything to share" />
        </div>

        <div class="account">
          <h3 class="survey__title">Account</h3>
          <UiInput v-model="signupForm.email" label="Email" type="email" placeholder="you@domain.com" />
          <UiInput v-model="signupForm.password" label="Password" type="password" placeholder="At least 4 chars" />
        </div>

        <div class="auth__actions">
          <UiButton type="submit" :disabled="submitting">{{ submitting ? 'Signing up...' : 'Sign up' }}</UiButton>
        </div>
      </form>
    </UiCard>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import puppyUrl from '@/assets/puppy.png'
import pawUrl from '@/assets/paw.png'
import { toApiClientError } from '@/api/http'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import UiInput from '@/components/ui/UiInput.vue'
import UiTextarea from '@/components/ui/UiTextarea.vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toasts'

const router = useRouter()
const auth = useAuthStore()
const toasts = useToastStore()

const mode = ref<'login' | 'signup'>('login')
const submitting = ref(false)
const authSection = ref<HTMLElement | null>(null)

const loginForm = reactive({
  email: 'ato@ato.com',
  password: '1234',
})

const signupForm = reactive({
  dogName: '',
  dogBreed: '',
  dogSize: 'SMALL',
  dogNeutered: 'true',
  dogSociabilityLevel: 'MEDIUM',
  dogReactivityLevel: 'LOW',
  dogNotes: '',
  email: '',
  password: '',
})

function scrollToAuth() {
  authSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function goMeetups() {
  router.push('/meetups')
}

async function onLogin() {
  submitting.value = true
  try {
    await auth.login(loginForm.email, loginForm.password)
    toasts.push({ tone: 'success', title: 'Logged in', message: 'Welcome back.' })
    await router.push('/meetups')
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    submitting.value = false
  }
}

async function onSignup() {
  submitting.value = true
  try {
    await auth.signup({
      email: signupForm.email,
      password: signupForm.password,
      dogName: signupForm.dogName,
      dogBreed: signupForm.dogBreed,
      dogSize: signupForm.dogSize as 'SMALL' | 'MEDIUM' | 'LARGE',
      dogNeutered: signupForm.dogNeutered === 'true',
      dogSociabilityLevel: signupForm.dogSociabilityLevel as 'LOW' | 'MEDIUM' | 'HIGH',
      dogReactivityLevel: signupForm.dogReactivityLevel as 'LOW' | 'MEDIUM' | 'HIGH',
      dogNotes: signupForm.dogNotes.trim() || undefined,
    })
    toasts.push({ tone: 'success', title: 'Signed up', message: 'Account created.' })
    await router.push('/meetups')
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    submitting.value = false
  }
}

async function onLogout() {
  submitting.value = true
  try {
    await auth.logout()
    toasts.push({ tone: 'success', title: 'Logged out', message: 'Session ended.' })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    submitting.value = false
    await router.push('/')
  }
}
</script>

<style scoped>
.hero {
  display: grid;
  gap: var(--s-8);
  align-items: center;
}

@media (min-width: 880px) {
  .hero {
    grid-template-columns: 1.15fr 0.85fr;
    gap: var(--s-12);
  }
}

.hero__title {
  margin: 0;
  font-family: var(--font-display);
  font-size: clamp(42px, 5vw, 60px);
  line-height: 1.02;
  letter-spacing: -0.03em;
}

.hero__titleAccent {
  background: linear-gradient(180deg, rgba(88, 169, 224, 0), rgba(88, 169, 224, 0.26));
  border-radius: 12px;
  padding: 0 8px;
}

.hero__titleAccent2 {
  background: linear-gradient(180deg, rgba(246, 195, 56, 0), rgba(246, 195, 56, 0.28));
  border-radius: 12px;
  padding: 0 8px;
}

.hero__sub {
  margin: var(--s-3) 0 0;
  max-width: 56ch;
  color: var(--c-ink-2);
  font-size: 16px;
  line-height: 1.6;
}

.hero__cta {
  margin-top: var(--s-6);
  display: flex;
  flex-wrap: wrap;
  gap: var(--s-2);
}

.hero__art {
  position: relative;
  min-height: 420px;
}

.hero__frame {
  position: absolute;
  inset: 0;
  margin: auto;
  width: min(360px, 100%);
  aspect-ratio: 1 / 1.1;
  border-radius: 40px;
  background: linear-gradient(180deg, #dbeeff 0%, #cfe6fa 100%);
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: var(--sh-2);
  overflow: hidden;
}

.hero__puppy {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center center;
  transform: scale(1.08);
}

.hero__cloud {
  position: absolute;
  width: 260px;
  height: 130px;
  background: radial-gradient(circle at 30% 30%, rgba(234, 243, 251, 1), rgba(234, 243, 251, 0.35));
  border-radius: 999px;
  opacity: 0.9;
}

.hero__cloud--a {
  left: -30px;
  top: 26px;
  transform: rotate(-6deg);
}

.hero__cloud--b {
  right: -40px;
  bottom: 20px;
  transform: rotate(8deg);
}

.hero__paw {
  position: absolute;
  right: 40px;
  top: 12px;
  width: 84px;
  height: 84px;
  transform: rotate(16deg);
  z-index: 2;
  object-fit: contain;
  filter: drop-shadow(0 8px 16px rgba(27, 31, 35, 0.14));
}

.auth {
  margin-top: var(--s-12);
}

.auth__card {
  padding: var(--s-6);
}

.auth__head {
  margin-bottom: var(--s-4);
}

.auth__title {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.auth__note {
  margin: 8px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
  line-height: 1.5;
}

.auth__tabs {
  display: flex;
  gap: var(--s-2);
  margin-bottom: var(--s-4);
}

.auth__form {
  display: grid;
  gap: var(--s-4);
}

.survey,
.account {
  display: grid;
  gap: var(--s-3);
}

.survey__title {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
  font-size: 18px;
}

.survey__grid {
  display: grid;
  gap: var(--s-3);
}

.radioGroup {
  border: 1px solid var(--c-line);
  border-radius: var(--r-sm);
  padding: var(--s-3);
  background: var(--c-surface-2);
}

.radioGroup__label {
  font-size: 12px;
  color: var(--c-ink-2);
  margin-bottom: var(--s-2);
}

.radioGroup__options {
  display: flex;
  flex-wrap: wrap;
  gap: var(--s-2);
}

.radioOption {
  position: relative;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.radioOption input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.radioOption span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 11px;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.16);
  background: rgba(255, 255, 255, 0.76);
  font-size: 13px;
  color: var(--c-ink-2);
}

.radioOption span::before {
  content: '';
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 1px solid rgba(27, 31, 35, 0.24);
  background: #fff;
  box-sizing: border-box;
}

.radioOption input:checked + span {
  color: var(--c-ink);
  border-color: rgba(88, 169, 224, 0.42);
  background: rgba(88, 169, 224, 0.18);
}

.radioOption input:checked + span::before {
  border-color: rgba(88, 169, 224, 0.7);
  background: linear-gradient(180deg, rgba(150, 214, 255, 1), rgba(88, 169, 224, 1));
  box-shadow: inset 0 0 0 3px #fff;
}

@media (min-width: 780px) {
  .survey__grid {
    grid-template-columns: 1fr 1fr;
  }
}

.auth__actions {
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}
</style>
