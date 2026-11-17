import { ArrowRight, Users } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { paths } from '@/routes/paths'
import type { ProjectSummary } from '@/types/project'
import { ProjectRoleBadge } from './ProjectRoleBadge'

interface ProjectCardProps {
  project: ProjectSummary
  canJoin: boolean
  joining: boolean
  onJoin: (project: ProjectSummary) => void
}

export function ProjectCard({ project, canJoin, joining, onJoin }: ProjectCardProps) {
  const memberLabel = `${project.memberCount} ${project.memberCount === 1 ? 'member' : 'members'}`

  return (
    <article className="group relative flex flex-col rounded-lg border border-neutral-200 bg-white p-5 shadow-xs transition-shadow hover:border-neutral-300 hover:shadow-md">
      <div className="flex items-start justify-between gap-3">
        <h3 className="min-w-0 text-base font-semibold break-words text-neutral-900">
          {/* Stretched link: the whole card is clickable. */}
          <Link
            to={paths.project(project.id)}
            className="after:absolute after:inset-0 after:rounded-lg focus-visible:outline-none focus-visible:after:ring-2 focus-visible:after:ring-neutral-900"
          >
            {project.name}
          </Link>
        </h3>
        <ProjectRoleBadge joined={project.joined} role={project.myRole} />
      </div>

      <p className="mt-2 mb-5 flex items-center gap-1.5 text-sm text-neutral-600">
        <Users className="size-4 shrink-0" aria-hidden />
        {memberLabel}
      </p>

      <div className="mt-auto flex items-center justify-between gap-3 border-t border-neutral-100 pt-4">
        <span className="inline-flex items-center gap-1 text-sm font-medium text-neutral-500 transition-colors group-hover:text-neutral-900">
          {project.joined ? 'Open project' : 'View project'}
          <ArrowRight className="size-4 transition-transform group-hover:translate-x-0.5" aria-hidden />
        </span>
        {canJoin && (
          // Raised above the stretched link so it stays clickable.
          <Button
            size="sm"
            className="relative z-10"
            loading={joining}
            onClick={() => onJoin(project)}
            aria-label={`Join ${project.name}`}
          >
            Join
          </Button>
        )}
      </div>
    </article>
  )
}
