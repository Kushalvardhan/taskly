import type { ReactNode } from 'react'

interface FormFieldProps {
  id: string
  label: string
  error?: string
  hint?: string
  /** Optional element on the right of the label row (e.g. a link). */
  labelAside?: ReactNode
  children: ReactNode
}

export function FormField({ id, label, error, hint, labelAside, children }: FormFieldProps) {
  return (
    <div className="space-y-1.5">
      <div className="flex items-center justify-between gap-2">
        <label htmlFor={id} className="block text-sm font-medium text-neutral-900">
          {label}
        </label>
        {labelAside}
      </div>
      {children}
      {hint && !error && (
        <p id={`${id}-hint`} className="text-xs text-neutral-500">
          {hint}
        </p>
      )}
      {error && (
        <p id={`${id}-error`} className="text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  )
}
