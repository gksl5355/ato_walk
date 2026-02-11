import type { ProductCategory } from '@/api/commerce'

type ImageKey = 'feed' | 'snack' | 'nosewalk'

const assetImages = import.meta.glob('../assets/*.{png,jpg,jpeg,webp,avif}', {
  eager: true,
  import: 'default',
}) as Record<string, string>

function toImageKey(category: ProductCategory): ImageKey {
  if (category === 'FEED') {
    return 'feed'
  }
  if (category === 'SNACK') {
    return 'snack'
  }
  return 'nosewalk'
}

export function getProductImageCandidates(category: ProductCategory): string[] {
  const key = toImageKey(category)
  const localAsset = Object.entries(assetImages)
    .filter(([path]) => path.toLowerCase().includes(key))
    .map(([, url]) => url)

  const publicCandidates = [
    `/assets/${key}.png`,
    `/assets/${key}.jpg`,
    `/assets/${key}.jpeg`,
    `/${key}.png`,
    `/${key}.jpg`,
    `/${key}.jpeg`,
    `/images/${key}.png`,
    `/images/${key}.jpg`,
    `/images/${key}.jpeg`,
  ]

  return [...localAsset, ...publicCandidates]
}
