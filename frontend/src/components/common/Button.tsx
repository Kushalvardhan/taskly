import type { ComponentProps } from 'react'
import { cn } from '@/utils/cn'
import { Spinner } from './Spinner'

const variants = {
  primary:
    'bg-neutral-900 text-white shadow-xs hover:bg-neutral-800 focus-visible:outline-neutral-900',
  secondary:
    'bg-white text-neutral-900 shadow-xs ring-1 ring-inset ring-neutral-300 hover:bg-neutral-50 focus-visible:outline-neutral-900',
  ghost: 'text-neutral-700 hover:bg-neutral-100 focus-visible:outline-neutral-900',
  /** Destructive / negative actions (classic deep red). */
  danger: 'bg-red-700 text-white shadow-xs hover:bg-red-800 focus-visible:outline-red-700',
  /** Positive confirmations such as promotions (classic deep green). */
  success: 'bg-emerald-700 text-white shadow-xs hover:bg-emerald-800 focus-visible:outline-emerald-700',
} as const

const sizes = {
  sm: 'h-8 gap-1.5 px-3 text-sm',
  md: 'h-10 gap-2 px-4 text-sm',
  icon: 'size-9',
} as const

export type ButtonProps = ComponentProps<'button'> & {
  variant?: keyof typeof variants
  size?: keyof typeof sizes
  loading?: boolean
  fullWidth?: boolean
}

export function Button({
  variant = 'primary',
  size = 'md',
  loading = false,
  fullWidth = false,
  type = 'button',
  disabled,
  className,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      className={cn(
        'inline-flex shrink-0 items-center justify-center rounded-md font-semibold whitespace-nowrap transition-colors',
        'focus-visible:outline-2 focus-visible:outline-offset-2',
        'disabled:cursor-not-allowed disabled:opacity-60',
        variants[variant],
        sizes[size],
        fullWidth && 'w-full',
        className,
      )}
      {...props}
    >
      {loading && <Spinner className="size-4" />}
      {children}
    </button>
  )
}
