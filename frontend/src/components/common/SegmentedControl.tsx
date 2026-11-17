import type { ReactNode } from 'react'
import { cn } from '@/utils/cn'

interface SegmentedControlProps<T extends string> {
  label: string
  options: readonly { value: T; label: ReactNode }[]
  value: T
  onChange: (value: T) => void
  className?: string
}

/** A compact single-choice filter (toggle buttons in a labelled group). */
export function SegmentedControl<T extends string>({
  label,
  options,
  value,
  onChange,
  className,
}: SegmentedControlProps<T>) {
  return (
    <div
      role="group"
      aria-label={label}
      className={cn('inline-flex rounded-md bg-neutral-100 p-1', className)}
    >
      {options.map((option) => {
        const selected = option.value === value
        return (
          <button
            key={option.value}
            type="button"
            aria-pressed={selected}
            onClick={() => onChange(option.value)}
            className={cn(
              'flex-1 rounded px-3 py-1.5 text-sm font-medium whitespace-nowrap transition-colors',
              'focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-neutral-900',
              selected
                ? 'bg-white text-neutral-900 shadow-xs ring-1 ring-neutral-200'
                : 'text-neutral-600 hover:text-neutral-900',
            )}
          >
            {option.label}
          </button>
        )
      })}
    </div>
  )
}
