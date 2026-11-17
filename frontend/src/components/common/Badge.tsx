import type { ReactNode } from 'react'
import { cn } from '@/utils/cn'

const tones = {
  /** Default grey chip. */
  neutral: 'bg-neutral-100 text-neutral-700 ring-neutral-200',
  /** Strong emphasis: black chip. */
  solid: 'bg-neutral-900 text-white ring-neutral-900',
  /** Quiet emphasis: white chip with a border. */
  outline: 'bg-white text-neutral-700 ring-neutral-300',
  /** De-emphasised / inactive. */
  muted: 'bg-neutral-50 text-neutral-500 ring-neutral-200',
  /** Reserved for errors and critical items. */
  danger: 'bg-red-50 text-red-700 ring-red-200',
} as const

export type BadgeTone = keyof typeof tones

export function Badge({
  tone = 'neutral',
  className,
  children,
}: {
  tone?: BadgeTone
  className?: string
  children: ReactNode
}) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium whitespace-nowrap ring-1 ring-inset',
        tones[tone],
        className,
      )}
    >
      {children}
    </span>
  )
}
