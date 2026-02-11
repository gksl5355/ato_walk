import { http, unwrap } from '@/api/http'

import type { PointBalance, PointBalanceEnvelope } from '@/api/commerce/types'

export async function getMyPointBalance(): Promise<PointBalance> {
  const res = await http.get<PointBalanceEnvelope>('/v1/users/me/points')
  return unwrap(res.data)
}
