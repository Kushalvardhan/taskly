import { Search, SearchX, Users } from 'lucide-react'
import { useMemo, useState } from 'react'
import { Alert } from '@/components/common/Alert'
import { Avatar } from '@/components/common/Avatar'
import { Badge } from '@/components/common/Badge'
import { EmptyState } from '@/components/common/EmptyState'
import { Input } from '@/components/common/Input'
import { Select } from '@/components/common/Select'
import { Skeleton } from '@/components/common/Skeleton'
import { RoleChangeDialog, type RoleChange } from '@/components/project/RoleChangeDialog'
import { useCurrentUser } from '@/hooks/useAuth'
import { useProjectMembers, useUpdateMemberRole } from '@/hooks/useProjects'
import { PROJECT_ROLE_LABELS, PROJECT_ROLES, type ProjectRole } from '@/types/enums'
import type { ProjectMember } from '@/types/project'
import { pluralize } from '@/utils/format'
import { canManageMemberRoles } from '@/utils/permissions'
import { useProjectContext } from './projectContext'

/** Project managers first, then alphabetical. */
function sortMembers(members: ProjectMember[]): ProjectMember[] {
  return [...members].sort((a, b) => {
    if (a.role !== b.role) return a.role === 'PROJECT_MANAGER' ? -1 : 1
    return a.memberName.localeCompare(b.memberName)
  })
}

export default function ProjectMembersPage() {
  const user = useCurrentUser()
  const { projectId } = useProjectContext()
  const membersQuery = useProjectMembers(projectId)
  const updateRole = useUpdateMemberRole(projectId)
  const [search, setSearch] = useState('')
  // A role change waits here until confirmed in the dialog.
  const [pendingChange, setPendingChange] = useState<RoleChange | null>(null)

  const confirmRoleChange = () => {
    if (!pendingChange) return
    // Close either way: success shows a toast, failure shows an error toast.
    updateRole.mutate(pendingChange, { onSettled: () => setPendingChange(null) })
  }

  const canManageRoles = canManageMemberRoles(user)
  const members = useMemo(() => sortMembers(membersQuery.data ?? []), [membersQuery.data])
  const visibleMembers = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return members
    return members.filter(
      (m) => m.memberName.toLowerCase().includes(term) || m.memberEmail.toLowerCase().includes(term),
    )
  }, [members, search])

  if (membersQuery.isPending) return <MembersSkeleton />
  if (membersQuery.isError) {
    return (
      <Alert variant="error" title="Couldn't load members">
        {membersQuery.error.message}
      </Alert>
    )
  }

  const managerCount = members.filter((m) => m.role === 'PROJECT_MANAGER').length

  return (
    <section aria-labelledby="members-heading" className="space-y-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h2 id="members-heading" className="text-base font-semibold text-neutral-900">
            Members
          </h2>
          <p className="text-sm text-neutral-600">
            {pluralize(members.length, 'member')} · {pluralize(managerCount, 'project manager')}
            {canManageRoles && ' · As organisation manager, you can change project roles.'}
          </p>
        </div>
        {members.length > 0 && (
          <div className="relative w-full sm:max-w-xs">
            <Search
              className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-neutral-400"
              aria-hidden
            />
            <Input
              type="search"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Search by name or email"
              aria-label="Search members"
              className="pl-9"
            />
          </div>
        )}
      </div>

      {members.length === 0 ? (
        <EmptyState icon={Users} title="No members yet" />
      ) : visibleMembers.length === 0 ? (
        <EmptyState icon={SearchX} title="No matching members" description="Try a different name or email." />
      ) : (
        <ul className="divide-y divide-neutral-200 overflow-hidden rounded-lg border border-neutral-200 bg-white">
          {visibleMembers.map((member) => (
            <MemberRow
              key={member.userId}
              member={member}
              isCurrentUser={member.userId === user.id}
              canManageRoles={canManageRoles}
              updating={updateRole.isPending && updateRole.variables?.member.userId === member.userId}
              onRoleChange={(role) => {
                if (role !== member.role) setPendingChange({ member, role })
              }}
            />
          ))}
        </ul>
      )}

      {canManageRoles && (
        <RoleChangeDialog
          change={pendingChange}
          isCurrentUser={pendingChange?.member.userId === user.id}
          pending={updateRole.isPending}
          onConfirm={confirmRoleChange}
          onCancel={() => setPendingChange(null)}
        />
      )}
    </section>
  )
}

interface MemberRowProps {
  member: ProjectMember
  isCurrentUser: boolean
  canManageRoles: boolean
  updating: boolean
  onRoleChange: (role: ProjectRole) => void
}

function MemberRow({ member, isCurrentUser, canManageRoles, updating, onRoleChange }: MemberRowProps) {
  const roleSelectId = `member-role-${member.userId}`

  return (
    <li className="flex flex-col gap-3 px-4 py-3 sm:flex-row sm:items-center sm:justify-between sm:px-5">
      <div className="flex min-w-0 items-center gap-3">
        <Avatar name={member.memberName} />
        <div className="min-w-0">
          <p className="flex items-center gap-2 text-sm font-medium text-neutral-900">
            <span className="truncate">{member.memberName}</span>
            {isCurrentUser && <Badge tone="muted">You</Badge>}
          </p>
          <p className="truncate text-sm text-neutral-500">{member.memberEmail}</p>
        </div>
      </div>

      <div className="pl-12 sm:pl-0">
        {canManageRoles ? (
          <>
            <label htmlFor={roleSelectId} className="sr-only">
              Project role for {member.memberName}
            </label>
            <Select
              id={roleSelectId}
              selectSize="sm"
              className="w-full sm:w-44"
              value={member.role}
              disabled={updating}
              aria-busy={updating || undefined}
              onChange={(event) => onRoleChange(event.target.value as ProjectRole)}
            >
              {PROJECT_ROLES.map((role) => (
                <option key={role} value={role}>
                  {PROJECT_ROLE_LABELS[role]}
                </option>
              ))}
            </Select>
          </>
        ) : (
          <Badge tone={member.role === 'PROJECT_MANAGER' ? 'solid' : 'outline'}>
            {PROJECT_ROLE_LABELS[member.role]}
          </Badge>
        )}
      </div>
    </li>
  )
}

function MembersSkeleton() {
  return (
    <div className="space-y-4" aria-busy="true" aria-label="Loading members">
      <Skeleton className="h-5 w-40" />
      <div className="divide-y divide-neutral-200 rounded-lg border border-neutral-200 bg-white">
        {Array.from({ length: 4 }, (_, index) => (
          <div key={index} className="flex items-center gap-3 px-5 py-3">
            <Skeleton className="size-9 rounded-full" />
            <div className="flex-1 space-y-2">
              <Skeleton className="h-4 w-40" />
              <Skeleton className="h-3 w-56" />
            </div>
            <Skeleton className="h-6 w-24" />
          </div>
        ))}
      </div>
    </div>
  )
}
