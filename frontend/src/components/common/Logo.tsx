import { ListChecks } from 'lucide-react'
import { cn } from '@/utils/cn'

export function Logo({ className, showText = true }: { className?: string; showText?: boolean }) {
  return (
    <span className={cn('inline-flex items-center gap-2 font-bold text-neutral-900', className)}>
      <span className="flex size-8 items-center justify-center rounded-md bg-neutral-900 text-white">
        <ListChecks className="size-5" aria-hidden />
      </span>
      {showText && <span className="text-lg tracking-tight">Taskly</span>}
    </span>
  )
}
