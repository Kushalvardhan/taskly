import { ChevronDown } from 'lucide-react'
import type { ComponentProps } from 'react'
import { cn } from '@/utils/cn'

export type SelectProps = ComponentProps<'select'> & {
  invalid?: boolean
  selectSize?: 'sm' | 'md'
}

/** Styled native <select>: keeps the platform picker on mobile. */
export function Select({ invalid = false, selectSize = 'md', className, children, ...props }: SelectProps) {
  return (
    <div className={cn('relative', className)}>
      <select
        aria-invalid={invalid || undefined}
        className={cn(
          'block w-full appearance-none rounded-md border-0 bg-white pr-9 text-neutral-900 shadow-xs',
          'ring-1 ring-neutral-300 ring-inset',
          'focus:ring-2 focus:ring-neutral-900 focus:outline-none focus:ring-inset',
          'disabled:cursor-not-allowed disabled:bg-neutral-50 disabled:text-neutral-500',
          selectSize === 'sm' ? 'h-8 pl-2.5 text-sm' : 'h-10 pl-3 text-base sm:text-sm',
          invalid && 'ring-red-400 focus:ring-red-500',
        )}
        {...props}
      >
        {children}
      </select>
      <ChevronDown
        className="pointer-events-none absolute top-1/2 right-2.5 size-4 -translate-y-1/2 text-neutral-500"
        aria-hidden
      />
    </div>
  )
}
