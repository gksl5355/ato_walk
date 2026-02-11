import type { ApiResponse } from '@/api/types'

export type ProductCategory = 'FEED' | 'SNACK' | 'TOY'
export type ProductStatus = 'ON_SALE' | 'SOLD_OUT' | 'HIDDEN'
export type OrderStatus = 'CREATED' | 'CANCELED'

export type Product = {
  id: number
  name: string
  category: ProductCategory
  price: number
  stockQuantity: number
  status: ProductStatus
  description: string | null
  createdAt: string
}

export type ProductSummary = {
  id: number
  name: string
  category: ProductCategory
  price: number
  stockQuantity: number
  status: ProductStatus
}

export type CartItem = {
  id: number
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  subtotalPrice: number
}

export type OrderItem = {
  id: number
  productId: number
  quantity: number
  unitPrice: number
  subtotalPrice: number
}

export type Order = {
  id: number
  status: OrderStatus
  totalPrice: number
  createdAt: string
  items: OrderItem[]
}

export type OrderSummary = {
  id: number
  status: OrderStatus
  totalPrice: number
  createdAt: string
}

export type PageMeta = {
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type ProductPageData = {
  content: ProductSummary[]
  page: PageMeta
}

export type OrderSummaryPageData = {
  content: OrderSummary[]
  page: PageMeta
}

export type CreateCartItemRequest = {
  productId: number
  quantity: number
}

export type UpdateCartItemRequest = {
  quantity: number
}

export type ListProductsParams = {
  page?: number
  size?: number
  category?: ProductCategory
  status?: ProductStatus
}

export type ListOrdersParams = {
  page?: number
  size?: number
}

export type PaymentMethod = 'POINT'

export type CreateOrderRequest = {
  paymentMethod: PaymentMethod
}

export type PointBalance = {
  balance: number
}

export type ProductEnvelope = ApiResponse<Product>
export type ProductPageEnvelope = ApiResponse<ProductPageData>
export type CartItemEnvelope = ApiResponse<CartItem>
export type CartItemListEnvelope = ApiResponse<CartItem[]>
export type OrderEnvelope = ApiResponse<Order>
export type OrderSummaryPageEnvelope = ApiResponse<OrderSummaryPageData>
export type VoidEnvelope = ApiResponse<Record<string, never> | null>
export type PointBalanceEnvelope = ApiResponse<PointBalance>
