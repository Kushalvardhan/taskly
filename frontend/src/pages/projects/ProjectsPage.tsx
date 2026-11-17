import { FolderKanban, FolderPlus, Plus, Search, SearchX } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { EmptyState } from '@/components/common/EmptyState'
import { Input } from '@/components/common/Input'
import { PageHeader } from '@/components/common/PageHeader'
import { SegmentedControl } from '@/components/common/SegmentedControl'
import { Skeleton } from '@/components/common/Skeleton'
import { CreateProjectModal } from '@/components/project/CreateProjectModal'
import { ProjectCard } from '@/components/project/ProjectCard'
import { useAuth, useCurrentUser } from '@/hooks/useAuth'
import { useJoinProject, useProjects } from '@/hooks/useProjects'
import type { ProjectSummary } from '@/types/project'
import { canCreateProject, canJoinProject, isOrganisationManager } from '@/utils/permissions'

const FILTERS = [
  { value: 'all', label: 'All' },
  { value: 'joined', label: 'Joined' },
  { value: 'not-joined', label: 'Not joined' },
] as const

type Filter = (typeof FILTERS)[number]['value']

function parseFilter(value: string | null): Filter {
  return FILTERS.some((f) => f.value === value) ? (value as Filter) : 'all'
}

function filterProjects(projects: ProjectSummary[], filter: Filter, search: string): ProjectSummary[] {
  const term = search.trim().toLowerCase()
  return projects.filter((project) => {
    if (filter === 'joined' && !project.joined) return false
    if (filter === 'not-joined' && project.joined) return false
    return !term || project.name.toLowerCase().includes(term)
  })
}

export default function ProjectsPage() {
  const user = useCurrentUser()
  const { organisation } = useAuth()
  const [searchParams, setSearchParams] = useSearchParams()
  const [createOpen, setCreateOpen] = useState(false)

  const projectsQuery = useProjects()
  const joinProject = useJoinProject()

  // Search and filter live in the URL so they survive refresh and back/forward.
  const search = searchParams.get('q') ?? ''
  const filter = parseFilter(searchParams.get('filter'))
  const setParam = (key: string, value: string) =>
    setSearchParams(
      (previous) => {
        const next = new URLSearchParams(previous)
        if (value) next.set(key, value)
        else next.delete(key)
        return next
      },
      { replace: true },
    )
  const clearFilters = () => setSearchParams({}, { replace: true })

  const projects = useMemo(() => projectsQuery.data ?? [], [projectsQuery.data])
  const visibleProjects = useMemo(() => filterProjects(projects, filter, search), [projects, filter, search])

  const canCreate = canCreateProject(user)
  // The organisation manager is a member of every project they created,
  // so the membership filter is only useful for employees.
  const showMembershipFilter = !isOrganisationManager(user)
  const joinedCount = projects.filter((p) => p.joined).length

  const description = projectsQuery.isSuccess
    ? [
        organisation?.name,
        `${projects.length} ${projects.length === 1 ? 'project' : 'projects'}`,
        showMembershipFilter ? `${joinedCount} joined` : null,
      ]
        .filter(Boolean)
        .join(' · ')
    : organisation?.name

  const newProjectButton = canCreate ? (
    <Button onClick={() => setCreateOpen(true)}>
      <Plus className="size-4" aria-hidden />
      New project
    </Button>
  ) : null

  return (
    <div className="space-y-6">
      <PageHeader title="Projects" description={description} actions={newProjectButton} />

      {projectsQuery.isSuccess && projects.length > 0 && (
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div className="relative w-full sm:max-w-xs">
            <Search
              className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-neutral-400"
              aria-hidden
            />
            <Input
              type="search"
              value={search}
              onChange={(event) => setParam('q', event.target.value)}
              placeholder="Search projects"
              aria-label="Search projects"
              className="pl-9"
            />
          </div>
          {showMembershipFilter && (
            <SegmentedControl
              label="Filter by membership"
              options={FILTERS}
              value={filter}
              onChange={(value) => setParam('filter', value === 'all' ? '' : value)}
              className="w-full sm:w-auto"
            />
          )}
        </div>
      )}

      <p className="sr-only" aria-live="polite">
        {projectsQuery.isSuccess ? `${visibleProjects.length} projects shown` : ''}
      </p>

      {projectsQuery.isPending ? (
        <ProjectGridSkeleton />
      ) : projectsQuery.isError ? (
        <Alert variant="error" title="Couldn't load projects">
          <p>{projectsQuery.error.message}</p>
          <Button
            variant="secondary"
            size="sm"
            className="mt-3"
            loading={projectsQuery.isRefetching}
            onClick={() => void projectsQuery.refetch()}
          >
            Try again
          </Button>
        </Alert>
      ) : projects.length === 0 ? (
        canCreate ? (
          <EmptyState
            icon={FolderPlus}
            title="No projects yet"
            description="Create the first project for your organisation. Employees can then join it."
            action={newProjectButton}
          />
        ) : (
          <EmptyState
            icon={FolderKanban}
            title="No projects yet"
            description="When your organisation manager creates a project, it will appear here for you to join."
          />
        )
      ) : visibleProjects.length === 0 ? (
        <EmptyState
          icon={SearchX}
          title="No matching projects"
          description="Try a different search term or filter."
          action={
            <Button variant="secondary" onClick={clearFilters}>
              Clear filters
            </Button>
          }
        />
      ) : (
        <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {visibleProjects.map((project) => (
            <li key={project.id} className="flex">
              <div className="flex w-full flex-col [&>article]:flex-1">
                <ProjectCard
                  project={project}
                  canJoin={canJoinProject(user, project)}
                  joining={joinProject.isPending && joinProject.variables?.id === project.id}
                  onJoin={(p) => joinProject.mutate(p)}
                />
              </div>
            </li>
          ))}
        </ul>
      )}

      {canCreate && <CreateProjectModal open={createOpen} onClose={() => setCreateOpen(false)} />}
    </div>
  )
}

function ProjectGridSkeleton() {
  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3" aria-busy="true" aria-label="Loading projects">
      {Array.from({ length: 6 }, (_, index) => (
        <div key={index} className="rounded-lg border border-neutral-200 bg-white p-5">
          <div className="flex items-start justify-between gap-3">
            <Skeleton className="h-5 w-2/3" />
            <Skeleton className="h-5 w-16" />
          </div>
          <Skeleton className="mt-3 h-4 w-24" />
          <div className="mt-5 border-t border-neutral-100 pt-4">
            <Skeleton className="h-4 w-28" />
          </div>
        </div>
      ))}
    </div>
  )
}
