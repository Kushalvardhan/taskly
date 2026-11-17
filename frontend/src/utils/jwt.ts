/*
 * Minimal, dependency-free JWT payload reader.
 * Used only for UX (expiry timing). The backend still verifies every token.
 */

interface JwtPayload {
  sub?: string
  exp?: number
  iat?: number
  roles?: string
}

function decodeBase64Url(segment: string): string {
  const base64 = segment.replace(/-/g, '+').replace(/_/g, '/')
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=')
  const binary = atob(padded)
  const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0))
  return new TextDecoder().decode(bytes)
}

export function decodeJwtPayload(token: string): JwtPayload | null {
  const [, payload] = token.split('.')
  if (!payload) return null
  try {
    const parsed: unknown = JSON.parse(decodeBase64Url(payload))
    return typeof parsed === 'object' && parsed !== null ? (parsed as JwtPayload) : null
  } catch {
    return null
  }
}

/** Expiry as epoch milliseconds, or null if the token has no readable `exp`. */
export function getTokenExpiry(token: string): number | null {
  const exp = decodeJwtPayload(token)?.exp
  return typeof exp === 'number' ? exp * 1000 : null
}

/** True when the token is expired (or unreadable), with a small safety margin. */
export function isTokenExpired(token: string, marginMs = 10_000): boolean {
  const expiry = getTokenExpiry(token)
  return expiry === null || expiry - marginMs <= Date.now()
}
