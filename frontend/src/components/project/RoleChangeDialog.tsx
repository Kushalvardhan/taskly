import { ArrowRight, Check, Minus, ShieldAlert, ShieldCheck } from 'lucide-react'
import { Avatar } from '@/components/common/Avatar'
import { Button } from '@/components/common/Button'
import { Modal } from '@/components/common/Modal'
import { PROJECT_ROLE_LABELS, type ProjectRole } from '@/types/enums'
import type { ProjectMember } from '@/types/project'
import { cn } from '@/utils/cn'

export interface RoleChange {
  member: ProjectMember
  role: ProjectRole
}

interface RoleChangeDialogProps {
  /** The change awaiting confirmation; null keeps the dialog closed. */
  change: RoleChange | null
  isCurrentUser: boolean
  pending: boolean
  onConfirm: () => void
  onCancel: () => void
}

/** What a project manager can do that a member cannot (enforced by the backend). */
const MANAGER_PERMISSIONS = [
  'Assign tasks to any project member',
  'Remove members from tasks',
  'Edit any task in this project',
] as const

/*
 * Colour is used sparingly so the dialog stays within the black/grey theme:
 * only the top accent bar, the icon and the confirm button carry it.
 */
const PANEL_CLASS = 'bg-neutral-50 ring-neutral-200'
const NEW_ROLE_CLASS = 'bg-neutral-900 text-white ring-neutral-900'
const BULLET_CLASS = 'text-neutral-500'

const tones = {
  promotion: {
    accent: 'success',
    button: 'success',
    icon: ShieldCheck,
    iconClass: 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-200',
  },
  demotion: {
    accent: 'danger',
    button: 'danger',
    icon: ShieldAlert,
    iconClass: 'bg-red-50 text-red-700 ring-1 ring-inset ring-red-200',
  },
} as const

export function RoleChangeDialog({ change, isCurrentUser, pending, onConfirm, onCancel }: RoleChangeDialogProps) {
  const promoting = change?.role === 'PROJECT_MANAGER'
  const tone = tones[promoting ? 'promotion' : 'demotion']
  const Icon = tone.icon

  const title = promoting
    ? 'Promote to project manager?'
    : isCurrentUser
      ? 'Demote yourself to member?'
      : 'Demote to member?'

  const confirmLabel = promoting ? 'Promote to project manager' : 'Demote to member'

  return (
    <Modal
      open={change !== null}
      onClose={onCancel}
      title={title}
      accent={tone.accent}
      preventClose={pending}
      footer={
        <>
          <Button variant="secondary" onClick={onCancel} disabled={pending}>
            Cancel
          </Button>
          <Button variant={tone.button} onClick={onConfirm} loading={pending} autoFocus>
            {confirmLabel}
          </Button>
        </>
      }
    >
      {change && (
        <div className="space-y-5">
          <div className={cn('rounded-lg p-4 ring-1 ring-inset', PANEL_CLASS)}>
            <div className="flex items-center gap-3">
              <span className={cn('flex size-10 shrink-0 items-center justify-center rounded-full', tone.iconClass)}>
                <Icon className="size-5" aria-hidden />
              </span>
              <div className="flex min-w-0 items-center gap-3">
                <Avatar name={change.member.memberName} />
                <div className="min-w-0">
                  <p className="truncate text-sm font-semibold text-neutral-900">
                    {change.member.memberName}
                    {isCurrentUser && <span className="font-normal text-neutral-500"> (you)</span>}
                  </p>
                  <p className="truncate text-sm text-neutral-600">{change.member.memberEmail}</p>
                </div>
              </div>
            </div>

            <div
              className="mt-4 flex flex-wrap items-center gap-2 text-sm"
              aria-label={`Role change from ${PROJECT_ROLE_LABELS[change.member.role]} to ${PROJECT_ROLE_LABELS[change.role]}`}
            >
              <RoleChip className="bg-white text-neutral-700 ring-neutral-300 line-through decoration-neutral-400">
                {PROJECT_ROLE_LABELS[change.member.role]}
              </RoleChip>
              <ArrowRight className="size-4 text-neutral-500" aria-hidden />
              <RoleChip className={NEW_ROLE_CLASS}>{PROJECT_ROLE_LABELS[change.role]}</RoleChip>
            </div>
          </div>

          <div>
            <p className="text-sm font-medium text-neutral-900">
              {promoting
                ? 'They will be able to:'
                : isCurrentUser
                  ? 'You will no longer be able to:'
                  : 'They will no longer be able to:'}
            </p>
            <ul className="mt-2 space-y-1.5">
              {MANAGER_PERMISSIONS.map((permission) => (
                <li key={permission} className="flex items-start gap-2 text-sm text-neutral-700">
                  {promoting ? (
                    <Check className={cn('mt-0.5 size-4 shrink-0', BULLET_CLASS)} aria-hidden />
                  ) : (
                    <Minus className={cn('mt-0.5 size-4 shrink-0', BULLET_CLASS)} aria-hidden />
                  )}
                  {permission}
                </li>
              ))}
            </ul>
          </div>

          {!promoting && isCurrentUser && (
            <p className="rounded-md bg-neutral-100 p-3 text-sm text-neutral-700">
              As organisation manager, you can promote yourself back at any time.
            </p>
          )}
        </div>
      )}
    </Modal>
  )
}

function RoleChip({ className, children }: { className: string; children: string }) {
  return (
    <span className={cn('inline-flex items-center rounded-md px-2.5 py-1 text-xs font-semibold ring-1 ring-inset', className)}>
      {children}
    </span>
  )
}
