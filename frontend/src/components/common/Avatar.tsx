import { cn } from '@/utils/cn'
import { getInitials } from '@/utils/format'

const sizes = {
  sm: 'size-7 text-[11px]',
  md: 'size-9 text-xs',
} as const

/** Initials avatar. Decorative: the name should be shown next to it. */
export function Avatar({ name, size = 'md', className }: { name: string; size?: keyof typeof sizes; className?: string }) {
  return (
    <span
      aria-hidden
      className={cn(
        'inline-flex shrink-0 items-center justify-center rounded-full bg-neutral-200 font-semibold text-neutral-800',
        sizes[size],
        className,
      )}
    >
      {getInitials(name)}
    </span>
  )
}
