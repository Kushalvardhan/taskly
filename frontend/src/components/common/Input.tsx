import { Eye, EyeOff } from 'lucide-react'
import { useState, type ComponentProps } from 'react'
import { cn } from '@/utils/cn'

export type InputProps = ComponentProps<'input'> & {
  invalid?: boolean
}

export function Input({ invalid = false, className, ...props }: InputProps) {
  return (
    <input
      aria-invalid={invalid || undefined}
      className={cn(
        // text-base on mobile prevents iOS zoom-on-focus.
        'block w-full rounded-md border-0 bg-white px-3 py-2 text-base text-neutral-900 shadow-xs sm:text-sm',
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

export function PasswordInput({ className, ...props }: Omit<InputProps, 'type'>) {
  const [visible, setVisible] = useState(false)
  const Icon = visible ? EyeOff : Eye

  return (
    <div className="relative">
      <Input type={visible ? 'text' : 'password'} className={cn('pr-10', className)} {...props} />
      <button
        type="button"
        onClick={() => setVisible((v) => !v)}
        aria-label={visible ? 'Hide password' : 'Show password'}
        aria-pressed={visible}
        className="absolute inset-y-0 right-0 flex w-10 items-center justify-center rounded-r-md text-neutral-400 hover:text-neutral-600 focus-visible:outline-2 focus-visible:outline-neutral-900"
      >
        <Icon className="size-4" aria-hidden />
      </button>
    </div>
  )
}
