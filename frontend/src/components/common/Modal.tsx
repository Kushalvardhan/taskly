import { X } from 'lucide-react'
import { useEffect, useId, useRef, type MouseEvent, type ReactNode } from 'react'
import { cn } from '@/utils/cn'

const sizes = {
  sm: 'max-w-sm',
  md: 'max-w-lg',
  lg: 'max-w-2xl',
} as const

/** Thin top bar that signals the nature of the dialog. */
const accents = {
  none: '',
  danger: 'border-t-4 border-red-700',
  success: 'border-t-4 border-emerald-700',
} as const

interface ModalProps {
  open: boolean
  onClose: () => void
  title: string
  description?: ReactNode
  children: ReactNode
  /** Usually the action buttons. */
  footer?: ReactNode
  size?: keyof typeof sizes
  accent?: keyof typeof accents
  /** Blocks Esc / backdrop / close button, e.g. while saving. */
  preventClose?: boolean
}

/**
 * Accessible modal on the native <dialog> element: focus is trapped and
 * restored by the browser, Esc closes it, and the page behind is inert.
 * Content is unmounted while closed, so forms reset on every open.
 */
export function Modal({
  open,
  onClose,
  title,
  description,
  children,
  footer,
  size = 'md',
  accent = 'none',
  preventClose = false,
}: ModalProps) {
  const dialogRef = useRef<HTMLDialogElement>(null)
  const titleId = useId()
  const descriptionId = useId()

  useEffect(() => {
    const dialog = dialogRef.current
    if (!dialog) return
    if (open && !dialog.open) dialog.showModal()
    else if (!open && dialog.open) dialog.close()
  }, [open])

  const requestClose = () => {
    if (!preventClose) onClose()
  }

  // Clicks on the ::backdrop are reported with the dialog itself as target.
  const onDialogClick = (event: MouseEvent<HTMLDialogElement>) => {
    if (event.target === event.currentTarget) requestClose()
  }

  return (
    <dialog
      ref={dialogRef}
      aria-labelledby={titleId}
      aria-describedby={description ? descriptionId : undefined}
      onCancel={(event) => {
        // Esc: keep React state as the source of truth.
        event.preventDefault()
        requestClose()
      }}
      onClick={onDialogClick}
      className={cn(
        'm-auto w-[calc(100%-2rem)] rounded-xl bg-white p-0 text-neutral-900 shadow-xl ring-1 ring-neutral-200',
        'backdrop:bg-neutral-950/50 backdrop:backdrop-blur-[2px]',
        sizes[size],
        accents[accent],
      )}
    >
      {open && (
        <div className="flex max-h-[calc(100dvh-2rem)] flex-col">
          <div className="flex items-start justify-between gap-4 border-b border-neutral-200 px-5 py-4 sm:px-6">
            <div className="min-w-0 space-y-1">
              <h2 id={titleId} className="text-base font-semibold text-neutral-900 sm:text-lg">
                {title}
              </h2>
              {description && (
                <p id={descriptionId} className="text-sm text-neutral-600">
                  {description}
                </p>
              )}
            </div>
            <button
              type="button"
              onClick={requestClose}
              disabled={preventClose}
              aria-label="Close"
              className="-mr-1 rounded-md p-1 text-neutral-500 hover:bg-neutral-100 hover:text-neutral-900 focus-visible:outline-2 focus-visible:outline-neutral-900 disabled:opacity-50"
            >
              <X className="size-5" aria-hidden />
            </button>
          </div>

          <div className="overflow-y-auto px-5 py-5 sm:px-6">{children}</div>

          {footer && (
            <div className="flex flex-col-reverse gap-2 border-t border-neutral-200 bg-neutral-50 px-5 py-4 sm:flex-row sm:justify-end sm:px-6">
              {footer}
            </div>
          )}
        </div>
      )}
    </dialog>
  )
}
