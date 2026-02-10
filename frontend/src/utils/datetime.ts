export function formatWhen(value: string): string {
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(d)
}

export function toIsoFromLocalInput(value: string): string {
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) {
    return value
  }
  return d.toISOString()
}
