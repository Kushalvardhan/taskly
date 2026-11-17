import { LoaderCircle } from 'lucide-react'
import { cn } from '@/utils/cn'

export function Spinner({ className, label }: { className?: string; label?: string }) {
  return (
    <LoaderCircle
      className={cn('size-5 animate-spin', className)}
      aria-hidden={label ? undefined : true}
      aria-label={label}
      role={label ? 'status' : undefined}
    />
  )
}

export function FullPageSpinner() {
  return (
    <div className="flex min-h-dvh items-center justify-center">
      <Spinner className="size-8 text-neutral-900" label="Loading" />
    </div>
  )
}
