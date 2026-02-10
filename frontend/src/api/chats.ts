import { http, unwrap } from '@/api/http'
import type { ApiResponse, Page } from '@/api/types'

export type ChatMessage = {
  id: number
  meetupId: number
  senderUserId: number
  senderNickname: string
  content: string
  createdAt: string
}

export async function listChatMessages(meetupId: number, params: { page?: number; size?: number }) {
  const res = await http.get<ApiResponse<Page<ChatMessage>>>(`/v1/meetups/${meetupId}/chats`, { params })
  return unwrap(res.data)
}

export async function createChatMessage(meetupId: number, req: { content: string }) {
  const res = await http.post<ApiResponse<ChatMessage>>(`/v1/meetups/${meetupId}/chats`, req)
  return unwrap(res.data)
}
