import { ArrowLeft, Building2, CalendarDays, FolderX, Lock, Users, type LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link, NavLink, Outlet, useParams } from 'react-router-dom'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { EmptyState } from '@/components/common/EmptyState'
import { Skeleton } from '@/components/common/Skeleton'
import { ProjectRoleBadge } from '@/components/project/ProjectRoleBadge'
import { useCurrentUser } from '@/hooks/useAuth'
import { useJoinProject, useProject, useProjectMembers } from '@/hooks/useProjects'
import { paths } from '@/routes/paths'
import type { AppError } from '@/services/api/apiError'
import { cn } from '@/utils/cn'
import { formatDate, pluralize } from '@/utils/format'
import { canJoinProject } from '@/utils/permissions'
import { parseProjectId, type ProjectContextValue } from './projectContext'

export default function ProjectLayout() {
  const { projectId: rawProjectId } = useParams()
  const projectId = parseProjectId(rawProjectId)

  if (projectId === null) return <ProjectNotFound />
  // key: fully reset state when navigating between projects.
  return <ProjectLayoutContent key={projectId} projectId={projectId} />
}

function ProjectLayoutContent({ projectId }: { projectId: number }) {
  const user = useCurrentUser()
  const projectQuery = useProject(projectId)
  const membersQuery = useProjectMembers(projectId)
  const joinProject = useJoinProject()

  if (projectQuery.isPending) return <ProjectHeaderSkeleton />
  if (projectQuery.isError) {
    return <ProjectLoadError error={projectQuery.error} onRetry={() => void projectQuery.refetch()} />
  }

  const project = projectQuery.data
  const members = membersQuery.data ?? []
  const membersLoaded = membersQuery.isSuccess
  const myRole = members.find((member) => member.userId === user.id)?.role ?? null
  const isMember = myRole !== null
  const canJoin = membersLoaded && canJoinProject(user, { joined: isMember })

  const context: ProjectContextValue = {
    projectId,
    project,
    members,
    membersLoaded,
    myRole,
    isMember,
  }

  const joinButton = canJoin ? (
    <Button
      loading={joinProject.isPending}
      onClick={() => joinProject.mutate({ id: project.id, name: project.name })}
    >
      Join project
    </Button>
  ) : null

  return (
    <div className="space-y-6">
      <div>
        <Link
          to={paths.projects}
          className="inline-flex items-center gap-1.5 text-sm font-medium text-neutral-600 hover:text-neutral-900"
        >
          <ArrowLeft className="size-4" aria-hidden />
          All projects
        </Link>

        <div className="mt-4 flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div className="min-w-0 space-y-2">
            <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
              <h1 className="text-2xl font-semibold tracking-tight break-words text-neutral-900">
                {project.name}
              </h1>
              {membersLoaded && <ProjectRoleBadge joined={isMember} role={myRole} />}
            </div>
            {project.description && (
              <p className="max-w-3xl text-sm whitespace-pre-line text-neutral-600">{project.description}</p>
            )}
            <dl className="flex flex-wrap gap-x-6 gap-y-2 pt-1 text-sm text-neutral-600">
              <MetaItem icon={Building2} label="Organisation" value={project.orgName} />
              <MetaItem icon={CalendarDays} label="Created" value={formatDate(project.createdAt)} />
              {membersLoaded && (
                <MetaItem icon={Users} label="Members" value={pluralize(members.length, 'member')} />
              )}
            </dl>
          </div>
          {joinButton && <div className="shrink-0">{joinButton}</div>}
        </div>
      </div>

      <nav aria-label="Project sections" className="-mx-4 border-b border-neutral-200 px-4 sm:mx-0 sm:px-0">
        <ul className="-mb-px flex gap-6">
          <ProjectTab to={paths.project(projectId)} end>
            Tasks
          </ProjectTab>
          <ProjectTab to={paths.projectMembers(projectId)}>
            Members
            {membersLoaded && (
              <span className="ml-2 rounded-full bg-neutral-100 px-2 py-0.5 text-xs font-medium text-neutral-700">
                {members.length}
              </span>
            )}
          </ProjectTab>
        </ul>
      </nav>

      {membersLoaded && !isMember && (
        <Alert variant="info" title="You're viewing this project as a non-member">
          {canJoin
            ? 'You can browse tasks and members. Join the project to create tasks and take on work.'
            : 'You can browse tasks and members, but you are not a member of this project.'}
        </Alert>
      )}

      <Outlet context={context} />
    </div>
  )
}

function MetaItem({ icon: Icon, label, value }: { icon: LucideIcon; label: string; value: string }) {
  return (
    <div className="flex items-center gap-1.5">
      <Icon className="size-4 shrink-0 text-neutral-400" aria-hidden />
      <dt className="sr-only">{label}</dt>
      <dd>{value}</dd>
    </div>
  )
}

function ProjectTab({ to, end, children }: { to: string; end?: boolean; children: ReactNode }) {
  return (
    <li>
      <NavLink
        to={to}
        end={end}
        className={({ isActive }) =>
          cn(
            'inline-flex items-center border-b-2 px-1 pb-3 text-sm font-medium transition-colors',
            'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-neutral-900',
            isActive
              ? 'border-neutral-900 text-neutral-900'
              : 'border-transparent text-neutral-500 hover:border-neutral-300 hover:text-neutral-800',
          )
        }
      >
        {children}
      </NavLink>
    </li>
  )
}

function ProjectNotFound() {
  return (
    <EmptyState
      icon={FolderX}
      title="Project not found"
      description="This project doesn't exist or may have been removed."
      action={<BackToProjects />}
    />
  )
}

function ProjectLoadError({ error, onRetry }: { error: AppError; onRetry: () => void }) {
  if (error.status === 404) return <ProjectNotFound />
  if (error.status === 403) {
    return (
      <EmptyState
        icon={Lock}
        title="You don't have access to this project"
        description="It belongs to a different organisation."
        action={<BackToProjects />}
      />
    )
  }
  return (
    <Alert variant="error" title="Couldn't load this project">
      <p>{error.message}</p>
      <div className="mt-3 flex flex-wrap gap-2">
        <Button variant="secondary" size="sm" onClick={onRetry}>
          Try again
        </Button>
      </div>
    </Alert>
  )
}

function BackToProjects() {
  return (
    <Link
      to={paths.projects}
      className="inline-flex h-10 items-center gap-2 rounded-md bg-white px-4 text-sm font-semibold text-neutral-900 shadow-xs ring-1 ring-neutral-300 ring-inset hover:bg-neutral-50"
    >
      <ArrowLeft className="size-4" aria-hidden />
      Back to projects
    </Link>
  )
}

function ProjectHeaderSkeleton() {
  return (
    <div className="space-y-6" aria-busy="true" aria-label="Loading project">
      <Skeleton className="h-4 w-24" />
      <div className="space-y-3">
        <Skeleton className="h-8 w-64 max-w-full" />
        <Skeleton className="h-4 w-full max-w-xl" />
        <Skeleton className="h-4 w-72 max-w-full" />
      </div>
      <Skeleton className="h-9 w-48" />
      <Skeleton className="h-48 w-full" />
    </div>
  )
}
