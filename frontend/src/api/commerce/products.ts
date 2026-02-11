import { http, unwrap } from '@/api/http'

import type { ListProductsParams, Product, ProductEnvelope, ProductPageData, ProductPageEnvelope } from '@/api/commerce/types'

export async function listProducts(params: ListProductsParams): Promise<ProductPageData> {
  const res = await http.get<ProductPageEnvelope>('/v1/products', { params })
  return unwrap(res.data)
}

export async function getProduct(productId: number): Promise<Product> {
  const res = await http.get<ProductEnvelope>(`/v1/products/${productId}`)
  return unwrap(res.data)
}
