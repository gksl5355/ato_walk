<template>
  <section class="pageHead">
    <div>
      <h1 class="title">Meetups</h1>
      <p class="sub">A tidy board with clear steps.</p>
    </div>
    <div class="pageHead__actions">
      <UiButton @click="openCreate">Create meetup</UiButton>
    </div>
  </section>

  <UiCard class="filters">
    <div class="filters__grid">
      <div class="radioGroup">
        <div class="radioGroup__label">Dog size</div>
        <div class="radioGroup__options">
          <label class="radioOption">
            <input v-model="filters.dogSize" type="radio" name="filterDogSize" value="" />
            <span>Any</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.dogSize" type="radio" name="filterDogSize" value="SMALL" />
            <span>소형</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.dogSize" type="radio" name="filterDogSize" value="MEDIUM" />
            <span>중형</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.dogSize" type="radio" name="filterDogSize" value="LARGE" />
            <span>대형</span>
          </label>
        </div>
      </div>

      <div class="radioGroup">
        <div class="radioGroup__label">Sociability</div>
        <div class="radioGroup__options">
          <label class="radioOption">
            <input v-model="filters.sociabilityLevel" type="radio" name="filterSociability" value="" />
            <span>Any</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.sociabilityLevel" type="radio" name="filterSociability" value="HIGH" />
            <span>높음</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.sociabilityLevel" type="radio" name="filterSociability" value="MEDIUM" />
            <span>보통</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.sociabilityLevel" type="radio" name="filterSociability" value="LOW" />
            <span>낮음</span>
          </label>
        </div>
      </div>

      <div class="radioGroup">
        <div class="radioGroup__label">Reactivity</div>
        <div class="radioGroup__options">
          <label class="radioOption">
            <input v-model="filters.reactivityLevel" type="radio" name="filterReactivity" value="" />
            <span>Any</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.reactivityLevel" type="radio" name="filterReactivity" value="LOW" />
            <span>차분</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.reactivityLevel" type="radio" name="filterReactivity" value="MEDIUM" />
            <span>보통</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.reactivityLevel" type="radio" name="filterReactivity" value="HIGH" />
            <span>예민</span>
          </label>
        </div>
      </div>

      <div class="radioGroup">
        <div class="radioGroup__label">Neutered</div>
        <div class="radioGroup__options">
          <label class="radioOption">
            <input v-model="filters.neutered" type="radio" name="filterNeutered" value="" />
            <span>Any</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.neutered" type="radio" name="filterNeutered" value="true" />
            <span>예</span>
          </label>
          <label class="radioOption">
            <input v-model="filters.neutered" type="radio" name="filterNeutered" value="false" />
            <span>아니오</span>
          </label>
        </div>
      </div>

      <div class="filters__actions">
        <UiButton variant="soft" @click="refresh">Apply</UiButton>
        <UiButton variant="ghost" @click="resetFilters">Reset</UiButton>
      </div>
    </div>
  </UiCard>

  <section v-if="state.loading" class="loading">Loading meetups...</section>

  <section v-else>
    <div v-if="state.page && state.page.content.length === 0" class="empty">
      <img class="empty__img" :src="puppyUrl" alt="" />
      <div class="empty__title">No meetups yet</div>
      <div class="empty__sub">Create the first one and keep it friendly.</div>
      <UiButton @click="openCreate">Create meetup</UiButton>
    </div>

    <div v-else class="cards">
      <button
        v-for="m in state.page?.content ?? []"
        :key="m.id"
        class="meetupCard"
        type="button"
        @click="openDetails(m)"
      >
        <div class="meetupCard__top">
          <UiBadge :tone="badgeTone(m.status)">{{ m.status }}</UiBadge>
          <div class="meetupCard__when">{{ formatWhen(m.scheduledAt) }}</div>
        </div>
        <div class="meetupCard__title">{{ m.title }}</div>
        <div class="meetupCard__meta">{{ m.location }} · max {{ m.maxParticipants }}</div>
      </button>
    </div>

    <div v-if="state.page" class="pager">
      <UiButton variant="ghost" size="sm" :disabled="state.page.first" @click="goPage(state.page.number - 1)">
        Prev
      </UiButton>
      <div class="pager__mid">Page {{ state.page.number + 1 }} / {{ state.page.totalPages }}</div>
      <UiButton variant="ghost" size="sm" :disabled="state.page.last" @click="goPage(state.page.number + 1)">
        Next
      </UiButton>
    </div>
  </section>

  <UiModal :open="modal.open" :title="modal.mode === 'create' ? 'Create meetup' : 'Edit meetup'" @close="closeModal">
    <form class="form" @submit.prevent="submitModal">
      <UiInput v-model="form.title" label="Title" placeholder="Morning walk at the park" required />
      <UiTextarea v-model="form.description" label="Description (optional)" placeholder="Any notes for participants" />
      <UiInput v-model="form.location" label="Location" placeholder="Gangnam, Seoul" required />
      <UiInput v-model="form.maxParticipants" label="Max participants" type="number" min="1" required />
      <UiInput v-model="form.scheduledAt" label="Scheduled at" type="datetime-local" required />

      <div class="form__section">
        <div class="form__sectionTitle">Preferences (optional)</div>

        <div class="radioGroup">
          <div class="radioGroup__label">Dog size</div>
          <div class="radioGroup__options">
            <label class="radioOption">
              <input v-model="form.dogSize" type="radio" name="meetupDogSize" value="" />
              <span>Any</span>
            </label>
            <label class="radioOption">
              <input v-model="form.dogSize" type="radio" name="meetupDogSize" value="SMALL" />
              <span>소형</span>
            </label>
            <label class="radioOption">
              <input v-model="form.dogSize" type="radio" name="meetupDogSize" value="MEDIUM" />
              <span>중형</span>
            </label>
            <label class="radioOption">
              <input v-model="form.dogSize" type="radio" name="meetupDogSize" value="LARGE" />
              <span>대형</span>
            </label>
          </div>
        </div>

        <div class="radioGroup">
          <div class="radioGroup__label">Sociability</div>
          <div class="radioGroup__options">
            <label class="radioOption">
              <input v-model="form.sociabilityLevel" type="radio" name="meetupSociability" value="" />
              <span>Any</span>
            </label>
            <label class="radioOption">
              <input v-model="form.sociabilityLevel" type="radio" name="meetupSociability" value="HIGH" />
              <span>높음</span>
            </label>
            <label class="radioOption">
              <input v-model="form.sociabilityLevel" type="radio" name="meetupSociability" value="MEDIUM" />
              <span>보통</span>
            </label>
            <label class="radioOption">
              <input v-model="form.sociabilityLevel" type="radio" name="meetupSociability" value="LOW" />
              <span>낮음</span>
            </label>
          </div>
        </div>

        <div class="radioGroup">
          <div class="radioGroup__label">Reactivity</div>
          <div class="radioGroup__options">
            <label class="radioOption">
              <input v-model="form.reactivityLevel" type="radio" name="meetupReactivity" value="" />
              <span>Any</span>
            </label>
            <label class="radioOption">
              <input v-model="form.reactivityLevel" type="radio" name="meetupReactivity" value="LOW" />
              <span>차분</span>
            </label>
            <label class="radioOption">
              <input v-model="form.reactivityLevel" type="radio" name="meetupReactivity" value="MEDIUM" />
              <span>보통</span>
            </label>
            <label class="radioOption">
              <input v-model="form.reactivityLevel" type="radio" name="meetupReactivity" value="HIGH" />
              <span>예민</span>
            </label>
          </div>
        </div>

        <div class="radioGroup">
          <div class="radioGroup__label">Neutered</div>
          <div class="radioGroup__options">
            <label class="radioOption">
              <input v-model="form.neutered" type="radio" name="meetupNeutered" value="" />
              <span>Any</span>
            </label>
            <label class="radioOption">
              <input v-model="form.neutered" type="radio" name="meetupNeutered" value="true" />
              <span>예</span>
            </label>
            <label class="radioOption">
              <input v-model="form.neutered" type="radio" name="meetupNeutered" value="false" />
              <span>아니오</span>
            </label>
          </div>
        </div>
      </div>

      <div class="form__actions">
        <UiButton type="submit" :disabled="modal.submitting">
          {{ modal.submitting ? 'Saving...' : 'Save' }}
        </UiButton>
        <UiButton variant="ghost" :disabled="modal.submitting" @click="closeModal">Cancel</UiButton>
      </div>
    </form>
  </UiModal>

  <UiDrawer :open="drawer.open" :title="drawer.meetup?.title ?? ''" @close="closeDrawer">
    <template v-if="drawer.meetup">
      <section class="drawerBlock">
        <div class="drawerBlock__grid">
          <div>
            <div class="drawerLabel">When</div>
            <div class="drawerValue">{{ formatWhen(drawer.meetup.scheduledAt) }}</div>
          </div>
          <div>
            <div class="drawerLabel">Where</div>
            <div class="drawerValue">{{ drawer.meetup.location }}</div>
          </div>
          <div>
            <div class="drawerLabel">Status</div>
            <div class="drawerValue">
              <UiBadge :tone="badgeTone(drawer.meetup.status)">{{ drawer.meetup.status }}</UiBadge>
            </div>
          </div>
        </div>

        <p v-if="drawer.meetup.description" class="drawerDesc">{{ drawer.meetup.description }}</p>

        <div v-if="isDrawerHost" class="drawerActions">
          <UiButton variant="soft" @click="openEdit(drawer.meetup)">Edit</UiButton>
          <UiButton variant="ghost" :disabled="drawer.busy" @click="onCancel(drawer.meetup.id)">Cancel</UiButton>
          <UiButton variant="ghost" :disabled="drawer.busy" @click="onEnd(drawer.meetup.id)">End</UiButton>
        </div>
      </section>

      <section class="drawerBlock">
        <h3 class="drawerH">Participation</h3>
        <div class="drawerActions" v-if="canRequestJoin">
          <UiButton :disabled="participation.busy" @click="onRequestParticipation(drawer.meetup.id)">
            {{ participation.busy ? 'Requesting...' : 'Request to join' }}
          </UiButton>
        </div>

        <p v-if="myParticipation?.status === 'REQUESTED'" class="drawerMuted">Your participation request is pending approval.</p>
        <p v-if="myParticipation?.status === 'APPROVED'" class="drawerMuted">You are approved for this meetup.</p>
        <p v-if="myParticipation?.status === 'REJECTED'" class="drawerMuted">Your previous request was rejected. You can request again.</p>

        <div v-if="isDrawerHost" class="participationList">
          <div v-if="participation.loading" class="drawerMuted">Loading requests...</div>
          <div v-else-if="(participation.page?.content.length ?? 0) === 0" class="drawerMuted">No pending requests.</div>
          <div v-else class="participationList__items">
            <div v-for="item in participation.page?.content ?? []" :key="item.id" class="participationList__item">
              <div>
                <div class="participationList__user">{{ item.userNickname }}</div>
                <div class="participationList__meta">{{ formatWhen(item.createdAt) }}</div>
              </div>
              <div class="drawerActions">
                <UiButton size="sm" :disabled="participation.busy" @click="onApproveParticipation(item.id)">Approve</UiButton>
                <UiButton size="sm" variant="ghost" :disabled="participation.busy" @click="onRejectParticipation(item.id)">
                  Reject
                </UiButton>
              </div>
            </div>
          </div>
        </div>

        <div class="participationList">
          <h4 class="participationList__heading">Members</h4>
          <div v-if="(participation.approvedPage?.content.length ?? 0) === 0" class="drawerMuted">No approved members yet.</div>
          <div v-else class="participationList__items">
            <div v-for="item in participation.approvedPage?.content ?? []" :key="`approved_${item.id}`" class="participationList__item">
              <div>
                <div class="participationList__user">{{ item.userNickname }}</div>
                <div class="participationList__meta">Joined</div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!isDrawerHost && !canRequestJoin && !myParticipation" class="drawerMuted">
          Participation is not available for this meetup status.
        </div>
      </section>

      <section class="drawerBlock">
        <h3 class="drawerH">Announcements</h3>
        <div class="comms">
          <div v-if="comms.loading" class="drawerMuted">Loading...</div>
          <div v-else-if="comms.page && comms.page.content.length === 0" class="drawerMuted">
            No announcements yet.
          </div>
          <div v-else class="comms__list">
            <div v-for="c in comms.page?.content ?? []" :key="c.id" class="comms__item">
              <div class="comms__content">{{ c.content }}</div>
              <div class="comms__meta">{{ formatWhen(c.createdAt) }}</div>
            </div>
          </div>

          <form v-if="isDrawerHost" class="comms__form" @submit.prevent="submitComm">
            <UiInput v-model="comms.draft" label="New message" placeholder="Short and clear" />
            <UiButton type="submit" :disabled="comms.sending || comms.draft.trim().length === 0">
              {{ comms.sending ? 'Sending...' : 'Send' }}
            </UiButton>
          </form>
          <p v-else class="drawerMuted">Only the meetup host can post announcements.</p>
        </div>
      </section>

      <section class="drawerBlock">
        <h3 class="drawerH">Chat</h3>
        <div class="comms">
          <div v-if="chat.loading" class="drawerMuted">Loading...</div>
          <div v-else-if="chat.page && chat.page.content.length === 0" class="drawerMuted">No chats yet.</div>
          <div v-else class="comms__list">
            <div v-for="m in chat.page?.content ?? []" :key="m.id" class="comms__item">
              <div class="comms__content">{{ m.content }}</div>
              <div class="comms__meta">{{ m.senderNickname }} · {{ formatWhen(m.createdAt) }}</div>
            </div>
          </div>

          <form v-if="canPostChat" class="comms__form" @submit.prevent="submitChat">
            <UiInput v-model="chat.draft" label="Chat message" placeholder="Say hi" />
            <UiButton type="submit" :disabled="chat.sending || chat.draft.trim().length === 0">
              {{ chat.sending ? 'Sending...' : 'Send' }}
            </UiButton>
          </form>
          <p v-else class="drawerMuted">Host and approved participants only.</p>
        </div>
      </section>
    </template>
  </UiDrawer>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'

import puppyUrl from '@/assets/puppy.png'
import { toApiClientError } from '@/api/http'
import {
  cancelMeetup,
  createMeetup,
  endMeetup,
  getMeetup,
  listMeetups,
  updateMeetup,
  type DogLevel,
  type DogSize,
  type Meetup,
} from '@/api/meetups'
import { createCommunication, listCommunications, type Communication } from '@/api/communications'
import { createChatMessage, listChatMessages, type ChatMessage } from '@/api/chats'
import {
  approveParticipation,
  getMyParticipation,
  listApprovedParticipations,
  listRequestedParticipations,
  rejectParticipation,
  requestParticipation,
  type Participation,
} from '@/api/participations'
import type { Page } from '@/api/types'
import UiBadge from '@/components/ui/UiBadge.vue'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import UiDrawer from '@/components/ui/UiDrawer.vue'
import UiInput from '@/components/ui/UiInput.vue'
import UiModal from '@/components/ui/UiModal.vue'
import UiTextarea from '@/components/ui/UiTextarea.vue'
import { useToastStore } from '@/stores/toasts'
import { useAuthStore } from '@/stores/auth'
import { formatWhen, toIsoFromLocalInput } from '@/utils/datetime'

const toasts = useToastStore()
const auth = useAuthStore()
const router = useRouter()

type DogSizeOrAny = '' | DogSize
type DogLevelOrAny = '' | DogLevel
type BooleanOrAny = '' | 'true' | 'false'

const filters = reactive<{ dogSize: DogSizeOrAny; sociabilityLevel: DogLevelOrAny; reactivityLevel: DogLevelOrAny; neutered: BooleanOrAny }>({
  dogSize: '',
  sociabilityLevel: '',
  reactivityLevel: '',
  neutered: '',
})

const state = reactive<{ loading: boolean; page: Page<Meetup> | null; pageNum: number }>({
  loading: true,
  page: null,
  pageNum: 0,
})

const modal = reactive<{ open: boolean; mode: 'create' | 'edit'; submitting: boolean; meetupId: number | null }>({
  open: false,
  mode: 'create',
  submitting: false,
  meetupId: null,
})

const form = reactive<{
  title: string
  description: string
  location: string
  maxParticipants: string
  scheduledAt: string
  dogSize: DogSizeOrAny
  sociabilityLevel: DogLevelOrAny
  reactivityLevel: DogLevelOrAny
  neutered: BooleanOrAny
}>({
  title: '',
  description: '',
  location: '',
  maxParticipants: '4',
  scheduledAt: '',
  dogSize: '',
  sociabilityLevel: '',
  reactivityLevel: '',
  neutered: '',
})

const drawer = reactive<{ open: boolean; meetup: Meetup | null; busy: boolean }>({
  open: false,
  meetup: null,
  busy: false,
})

const comms = reactive<{
  loading: boolean
  page: Page<Communication> | null
  draft: string
  sending: boolean
}>({
  loading: false,
  page: null,
  draft: '',
  sending: false,
})

const chat = reactive<{
  loading: boolean
  page: Page<ChatMessage> | null
  draft: string
  sending: boolean
}>({
  loading: false,
  page: null,
  draft: '',
  sending: false,
})

const participation = reactive<{
  loading: boolean
  busy: boolean
  page: Page<Participation> | null
  approvedPage: Page<Participation> | null
  mine: Participation | null
}>({
  loading: false,
  busy: false,
  page: null,
  approvedPage: null,
  mine: null,
})

const myParticipation = computed(() => participation.mine)

const isDrawerHost = computed(() => {
  if (!drawer.meetup || auth.userId === null) {
    return false
  }
  return drawer.meetup.hostUserId === auth.userId
})

const canRequestJoin = computed(() => {
  if (!drawer.meetup || auth.userId === null) {
    return false
  }
  if (drawer.meetup.hostUserId === auth.userId) {
    return false
  }
  if (drawer.meetup.status !== 'RECRUITING') {
    return false
  }
  if (!participation.mine) {
    return true
  }
  return participation.mine.status === 'REJECTED'
})

const canPostChat = computed(() => {
  if (!drawer.meetup || auth.userId === null) {
    return false
  }
  if (drawer.meetup.hostUserId === auth.userId) {
    return true
  }
  return participation.mine?.status === 'APPROVED'
})

async function refresh() {
  state.loading = true
  try {
    state.page = await listMeetups({
      page: state.pageNum,
      size: 12,
      dogSize: filters.dogSize === '' ? undefined : filters.dogSize,
      sociabilityLevel: filters.sociabilityLevel === '' ? undefined : filters.sociabilityLevel,
      reactivityLevel: filters.reactivityLevel === '' ? undefined : filters.reactivityLevel,
      neutered: filters.neutered === '' ? undefined : filters.neutered === 'true',
    })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.page = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 12, numberOfElements: 0, first: true, last: true }
  } finally {
    state.loading = false
  }
}

function requireLogin(): boolean {
  if (auth.isLoggedIn) {
    return true
  }
  toasts.push({ tone: 'info', title: 'Login needed', message: 'Please login to continue.' })
  router.push('/')
  return false
}

function resetFilters() {
  filters.dogSize = ''
  filters.sociabilityLevel = ''
  filters.reactivityLevel = ''
  filters.neutered = ''
  state.pageNum = 0
  void refresh()
}

function goPage(pageNum: number) {
  state.pageNum = Math.max(0, pageNum)
  void refresh()
}

function badgeTone(status: Meetup['status']) {
  if (status === 'RECRUITING') return 'sky'
  if (status === 'ENDED') return 'sun'
  return 'danger'
}

function openCreate() {
  if (!requireLogin()) {
    return
  }
  modal.open = true
  modal.mode = 'create'
  modal.meetupId = null
  form.title = ''
  form.description = ''
  form.location = ''
  form.maxParticipants = '4'
  form.scheduledAt = ''
  form.dogSize = ''
  form.sociabilityLevel = ''
  form.reactivityLevel = ''
  form.neutered = ''
}

function openEdit(m: Meetup) {
  if (!requireLogin()) {
    return
  }
  modal.open = true
  modal.mode = 'edit'
  modal.meetupId = m.id
  form.title = m.title
  form.description = m.description ?? ''
  form.location = m.location
  form.maxParticipants = String(m.maxParticipants)
  form.dogSize = m.dogSize ?? ''
  form.sociabilityLevel = m.sociabilityLevel ?? ''
  form.reactivityLevel = m.reactivityLevel ?? ''
  form.neutered = m.neutered === null ? '' : m.neutered ? 'true' : 'false'
  const d = new Date(m.scheduledAt)
  if (!Number.isNaN(d.getTime())) {
    const pad2 = (n: number) => String(n).padStart(2, '0')
    form.scheduledAt = `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}T${pad2(d.getHours())}:${pad2(d.getMinutes())}`
  } else {
    form.scheduledAt = ''
  }
}

function closeModal() {
  modal.open = false
}

async function submitModal() {
  if (!requireLogin()) {
    return
  }

  const title = form.title.trim()
  const location = form.location.trim()
  const maxParticipants = Number(form.maxParticipants)
  const scheduledAt = toIsoFromLocalInput(form.scheduledAt)

  if (!title || !location) {
    toasts.push({ tone: 'error', title: 'COMMON_VALIDATION_FAILED', message: 'Title and location are required.' })
    return
  }
  if (!Number.isFinite(maxParticipants) || maxParticipants < 1) {
    toasts.push({ tone: 'error', title: 'COMMON_VALIDATION_FAILED', message: 'Max participants must be 1 or more.' })
    return
  }
  if (!form.scheduledAt || Number.isNaN(new Date(scheduledAt).getTime())) {
    toasts.push({ tone: 'error', title: 'COMMON_VALIDATION_FAILED', message: 'Please select a valid schedule date/time.' })
    return
  }

  modal.submitting = true
  try {
    const payload = {
      title,
      description: form.description.trim() || undefined,
      location,
      maxParticipants,
      scheduledAt,
      dogSize: form.dogSize === '' ? undefined : form.dogSize,
      sociabilityLevel: form.sociabilityLevel === '' ? undefined : form.sociabilityLevel,
      reactivityLevel: form.reactivityLevel === '' ? undefined : form.reactivityLevel,
      neutered: form.neutered === '' ? undefined : form.neutered === 'true',
    }

    if (modal.mode === 'create') {
      await createMeetup(payload)
      toasts.push({ tone: 'success', title: 'Created', message: 'Meetup created.' })
    } else if (modal.meetupId !== null) {
      await updateMeetup(modal.meetupId, payload)
      toasts.push({ tone: 'success', title: 'Updated', message: 'Meetup updated.' })
    }
    modal.open = false
    state.pageNum = 0
    await refresh()
  } catch (e) {
    const err = toApiClientError(e)
    const detail = err.details.length > 0 ? ` (${err.details[0]})` : ''
    toasts.push({ tone: 'error', title: err.code, message: `${err.message}${detail}` })
  } finally {
    modal.submitting = false
  }
}

async function openDetails(m: Meetup) {
  drawer.open = true
  drawer.meetup = m
  comms.page = null
  comms.draft = ''
  chat.page = null
  chat.draft = ''
  participation.page = null
  participation.approvedPage = null
  participation.mine = null
  await loadMeetup(m.id)
  await Promise.all([
    loadComms(m.id),
    loadRequestedParticipations(m.id),
    loadApprovedParticipations(m.id),
    loadMyParticipation(m.id),
    loadChats(m.id),
  ])
}

async function loadMeetup(meetupId: number) {
  try {
    drawer.meetup = await getMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  }
}

async function loadComms(meetupId: number) {
  comms.loading = true
  try {
    comms.page = await listCommunications(meetupId, { page: 0, size: 20 })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    comms.loading = false
  }
}

async function loadChats(meetupId: number) {
  chat.loading = true
  try {
    chat.page = await listChatMessages(meetupId, { page: 0, size: 50 })
  } catch (e) {
    const err = toApiClientError(e)
    chat.page = null
    if (err.code !== 'CHAT_LIST_FORBIDDEN') {
      toasts.push({ tone: 'error', title: err.code, message: err.message })
    }
  } finally {
    chat.loading = false
  }
}

async function loadRequestedParticipations(meetupId: number) {
  if (!isDrawerHost.value) {
    participation.page = null
    return
  }
  participation.loading = true
  try {
    participation.page = await listRequestedParticipations(meetupId, { page: 0, size: 50 })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    participation.loading = false
  }
}

async function loadApprovedParticipations(meetupId: number) {
  try {
    participation.approvedPage = await listApprovedParticipations(meetupId, { page: 0, size: 50 })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  }
}

async function loadMyParticipation(meetupId: number) {
  if (auth.userId === null) {
    participation.mine = null
    return
  }
  try {
    participation.mine = await getMyParticipation(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  }
}

function closeDrawer() {
  drawer.open = false
  drawer.meetup = null
  participation.mine = null
  participation.approvedPage = null
  chat.page = null
  chat.draft = ''
}

async function onCancel(meetupId: number) {
  if (!requireLogin()) {
    return
  }
  if (!isDrawerHost.value) {
    toasts.push({ tone: 'error', title: 'MEETUP_CANCEL_FORBIDDEN', message: 'Only host can cancel meetup.' })
    return
  }
  drawer.busy = true
  try {
    await cancelMeetup(meetupId)
    toasts.push({ tone: 'success', title: 'Canceled', message: 'Meetup canceled.' })
    await refresh()
    await loadMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    drawer.busy = false
  }
}

async function onEnd(meetupId: number) {
  if (!requireLogin()) {
    return
  }
  if (!isDrawerHost.value) {
    toasts.push({ tone: 'error', title: 'MEETUP_END_FORBIDDEN', message: 'Only host can end meetup.' })
    return
  }
  drawer.busy = true
  try {
    await endMeetup(meetupId)
    toasts.push({ tone: 'success', title: 'Ended', message: 'Meetup ended.' })
    await refresh()
    await loadMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    drawer.busy = false
  }
}

async function submitComm() {
  if (!drawer.meetup) return
  if (!requireLogin()) {
    return
  }
  if (!isDrawerHost.value) {
    toasts.push({ tone: 'error', title: 'COMMUNICATION_CREATE_FORBIDDEN', message: 'Only host can post announcements.' })
    return
  }
  comms.sending = true
  try {
    await createCommunication(drawer.meetup.id, { content: comms.draft.trim() })
    comms.draft = ''
    await loadComms(drawer.meetup.id)
    toasts.push({ tone: 'success', title: 'Sent', message: 'Announcement posted.' })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    comms.sending = false
  }
}

async function submitChat() {
  if (!drawer.meetup) return
  if (!requireLogin()) {
    return
  }
  if (!canPostChat.value) {
    toasts.push({ tone: 'error', title: 'CHAT_CREATE_FORBIDDEN', message: 'Only host and approved participants can chat.' })
    return
  }
  chat.sending = true
  try {
    await createChatMessage(drawer.meetup.id, { content: chat.draft.trim() })
    chat.draft = ''
    await loadChats(drawer.meetup.id)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    chat.sending = false
  }
}

async function onRequestParticipation(meetupId: number) {
  if (!requireLogin()) {
    return
  }
  participation.busy = true
  try {
    participation.mine = await requestParticipation(meetupId)
    toasts.push({ tone: 'success', title: 'Requested', message: 'Participation request sent.' })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    participation.busy = false
  }
}

async function onApproveParticipation(participationId: number) {
  if (!drawer.meetup) {
    return
  }
  participation.busy = true
  try {
    await approveParticipation(drawer.meetup.id, participationId)
    toasts.push({ tone: 'success', title: 'Approved', message: 'Participation approved.' })
    await loadRequestedParticipations(drawer.meetup.id)
    await loadApprovedParticipations(drawer.meetup.id)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    participation.busy = false
  }
}

async function onRejectParticipation(participationId: number) {
  if (!drawer.meetup) {
    return
  }
  participation.busy = true
  try {
    await rejectParticipation(drawer.meetup.id, participationId)
    toasts.push({ tone: 'success', title: 'Rejected', message: 'Participation rejected.' })
    await loadRequestedParticipations(drawer.meetup.id)
    await loadApprovedParticipations(drawer.meetup.id)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    participation.busy = false
  }
}

void refresh()
</script>

<style scoped>
.pageHead {
  display: flex;
  gap: var(--s-4);
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
}

.title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 34px;
  letter-spacing: -0.03em;
}

.sub {
  margin: 8px 0 0;
  color: var(--c-ink-2);
  max-width: 68ch;
}

.filters {
  margin-top: var(--s-6);
  padding: var(--s-4);
}

.filters__grid {
  display: grid;
  gap: var(--s-4);
}

@media (min-width: 860px) {
  .filters__grid {
    grid-template-columns: 1fr 1fr 1fr auto;
    align-items: end;
  }
}

.filters__actions {
  display: flex;
  gap: var(--s-2);
  justify-content: flex-start;
  flex-wrap: wrap;
}

.loading {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.cards {
  margin-top: var(--s-6);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  align-items: start;
}

.meetupCard {
  text-align: left;
  border-radius: var(--r-lg);
  padding: var(--s-4);
  border: 1px solid rgba(27, 31, 35, 0.14);
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08), 0 14px 40px rgba(27, 31, 35, 0.12);
  cursor: pointer;
  transition: transform 140ms ease, box-shadow 140ms ease;
}

.meetupCard:hover {
  transform: translateY(-2px);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08), 0 20px 60px rgba(27, 31, 35, 0.14);
}

.meetupCard__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.meetupCard__when {
  font-size: 12px;
  color: var(--c-ink-2);
}

.meetupCard__title {
  margin-top: 10px;
  font-weight: 900;
  letter-spacing: -0.02em;
  font-size: 16px;
}

.meetupCard__meta {
  margin-top: 6px;
  font-size: 13px;
  color: var(--c-ink-2);
}

.pager {
  margin-top: var(--s-6);
  display: flex;
  gap: var(--s-2);
  align-items: center;
  justify-content: center;
  color: var(--c-ink-2);
}

.pager__mid {
  font-size: 13px;
}

.empty {
  margin-top: var(--s-10);
  display: grid;
  justify-items: center;
  gap: 8px;
  text-align: center;
}

.empty__img {
  width: 180px;
  height: 180px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: var(--sh-1);
}

.empty__title {
  font-family: var(--font-display);
  font-size: 22px;
  letter-spacing: -0.02em;
}

.empty__sub {
  color: var(--c-ink-2);
  margin-bottom: var(--s-2);
}

.form {
  display: grid;
  gap: var(--s-4);
}

.form__actions {
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}

.drawerBlock {
  padding: var(--s-4);
  border-radius: var(--r-lg);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(27, 31, 35, 0.12);
}

.drawerBlock + .drawerBlock {
  margin-top: var(--s-4);
}

.drawerBlock__grid {
  display: grid;
  gap: var(--s-3);
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.drawerLabel {
  font-size: 12px;
  color: var(--c-ink-2);
}

.drawerValue {
  margin-top: 2px;
  font-weight: 700;
}

.drawerDesc {
  margin: var(--s-4) 0 0;
  color: var(--c-ink-2);
}

.drawerActions {
  margin-top: var(--s-4);
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}

.drawerH {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.drawerMuted {
  margin: 8px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
}

.comms {
  margin-top: var(--s-3);
}

.comms__list {
  display: grid;
  gap: 10px;
  margin-top: var(--s-3);
}

.comms__item {
  padding: 10px 12px;
  border-radius: 16px;
  border: 1px solid rgba(27, 31, 35, 0.12);
  background: rgba(255, 255, 255, 0.72);
}

.comms__content {
  font-weight: 700;
  letter-spacing: -0.01em;
}

.comms__meta {
  margin-top: 4px;
  font-size: 12px;
  color: var(--c-ink-2);
}

.comms__form {
  display: grid;
  gap: var(--s-3);
  margin-top: var(--s-4);
}

.participationList {
  margin-top: var(--s-3);
}

.participationList__items {
  display: grid;
  gap: 10px;
}

.participationList__heading {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--c-ink-2);
  font-weight: 700;
}

.participationList__item {
  display: flex;
  gap: var(--s-3);
  justify-content: space-between;
  align-items: center;
  border: 1px solid rgba(27, 31, 35, 0.12);
  background: rgba(255, 255, 255, 0.72);
  border-radius: 14px;
  padding: 10px 12px;
}

.participationList__user {
  font-weight: 700;
}

.participationList__meta {
  margin-top: 2px;
  font-size: 12px;
  color: var(--c-ink-2);
}
</style>
