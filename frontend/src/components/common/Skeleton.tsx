import { cn } from '@/utils/cn'

/** Grey placeholder block shown while content loads. */
export function Skeleton({ className }: { className?: string }) {
  return <div aria-hidden className={cn('animate-pulse rounded-md bg-neutral-200/70', className)} />
}
