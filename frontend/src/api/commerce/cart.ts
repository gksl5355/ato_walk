import { http, unwrap, unwrapNullable } from '@/api/http'

import type {
  CartItem,
  CartItemEnvelope,
  CartItemListEnvelope,
  CreateCartItemRequest,
  UpdateCartItemRequest,
  VoidEnvelope,
} from '@/api/commerce/types'

export async function getCartItems(): Promise<CartItem[]> {
  const res = await http.get<CartItemListEnvelope>('/v1/cart-items')
  return unwrap(res.data)
}

export async function addCartItem(req: CreateCartItemRequest): Promise<CartItem> {
  const res = await http.post<CartItemEnvelope>('/v1/cart-items', req)
  return unwrap(res.data)
}

export async function updateCartItem(cartItemId: number, req: UpdateCartItemRequest): Promise<CartItem> {
  const res = await http.put<CartItemEnvelope>(`/v1/cart-items/${cartItemId}`, req)
  return unwrap(res.data)
}

export async function deleteCartItem(cartItemId: number): Promise<void> {
  const res = await http.delete<VoidEnvelope>(`/v1/cart-items/${cartItemId}`)
  unwrapNullable(res.data)
}
