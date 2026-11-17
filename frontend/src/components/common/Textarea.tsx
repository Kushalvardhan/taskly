import type { ComponentProps } from 'react'
import { cn } from '@/utils/cn'

export type TextareaProps = ComponentProps<'textarea'> & {
  invalid?: boolean
}

export function Textarea({ invalid = false, className, rows = 4, ...props }: TextareaProps) {
  return (
    <textarea
      rows={rows}
      aria-invalid={invalid || undefined}
      className={cn(
        'block w-full resize-y rounded-md border-0 bg-white px-3 py-2 text-base text-neutral-900 shadow-xs sm:text-sm',
        'ring-1 ring-neutral-300 ring-inset placeholder:text-neutral-400',
        'focus:ring-2 focus:ring-neutral-900 focus:outline-none focus:ring-inset',
        'disabled:cursor-not-allowed disabled:bg-neutral-50 disabled:text-neutral-500',
        invalid && 'ring-red-400 focus:ring-red-500',
        className,
      )}
      {...props}
    />
  )
}
