import type { FieldValues, Path, UseFormSetError } from 'react-hook-form'
import type { AppError } from '@/services/api/apiError'

/** Ids for a control's aria-describedby, matching FormField's hint/error elements. */
export function describedBy(id: string, { error, hint }: { error?: string; hint?: string }) {
  const ids = [hint && !error ? `${id}-hint` : null, error ? `${id}-error` : null].filter(Boolean)
  return ids.length ? ids.join(' ') : undefined
}

/**
 * Copies server-side field errors (ApiErrorResponse.errors) onto a form.
 * Returns true if at least one known field received an error, so the caller
 * can fall back to a general alert otherwise.
 */
export function applyServerFieldErrors<T extends FieldValues>(
  error: AppError,
  setError: UseFormSetError<T>,
  fields: readonly Path<T>[],
): boolean {
  let applied = false
  for (const [field, message] of Object.entries(error.fieldErrors)) {
    const match = fields.find((name) => name === field)
    if (match) {
      setError(match, { type: 'server', message }, { shouldFocus: !applied })
      applied = true
    }
  }
  return applied
}
