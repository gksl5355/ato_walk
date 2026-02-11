import { http, unwrap } from '@/api/http'

import type {
  CreateOrderRequest,
  ListOrdersParams,
  Order,
  OrderEnvelope,
  OrderSummaryPageData,
  OrderSummaryPageEnvelope,
} from '@/api/commerce/types'

export async function createOrder(req: CreateOrderRequest = { paymentMethod: 'POINT' }): Promise<Order> {
  const res = await http.post<OrderEnvelope>('/v1/orders', req)
  return unwrap(res.data)
}

export async function listOrders(params: ListOrdersParams): Promise<OrderSummaryPageData> {
  const res = await http.get<OrderSummaryPageEnvelope>('/v1/orders', { params })
  return unwrap(res.data)
}

export async function getOrder(orderId: number): Promise<Order> {
  const res = await http.get<OrderEnvelope>(`/v1/orders/${orderId}`)
  return unwrap(res.data)
}

export async function cancelOrder(orderId: number): Promise<Order> {
  const res = await http.post<OrderEnvelope>(`/v1/orders/${orderId}/cancel`)
  return unwrap(res.data)
}
