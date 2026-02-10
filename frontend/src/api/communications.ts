import { http, unwrap } from '@/api/http'
import type { ApiResponse, Page } from '@/api/types'

export type Communication = {
  id: number
  meetupId: number
  content: string
  createdAt: string
}

export async function listCommunications(meetupId: number, params: { page?: number; size?: number }) {
  const res = await http.get<ApiResponse<Page<Communication>>>(`/v1/meetups/${meetupId}/communications`, {
    params,
  })
  return unwrap(res.data)
}

export async function createCommunication(meetupId: number, req: { content: string }) {
  const res = await http.post<ApiResponse<Communication>>(`/v1/meetups/${meetupId}/communications`, req)
  return unwrap(res.data)
}
