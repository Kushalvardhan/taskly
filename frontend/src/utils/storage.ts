/*
 * localStorage access that never throws (private mode, blocked storage,
 * quota errors, corrupted JSON). Callers treat failures as "nothing stored".
 */

export function readJson(key: string): unknown {
  try {
    const raw = window.localStorage.getItem(key)
    return raw === null ? null : (JSON.parse(raw) as unknown)
  } catch {
    return null
  }
}

export function writeJson(key: string, value: unknown): void {
  try {
    window.localStorage.setItem(key, JSON.stringify(value))
  } catch {
    // Storage unavailable: the session simply won't survive a reload.
  }
}

export function removeItem(key: string): void {
  try {
    window.localStorage.removeItem(key)
  } catch {
    // Ignore.
  }
}
