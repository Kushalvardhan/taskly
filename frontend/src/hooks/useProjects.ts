import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { projectApi } from '@/services/api/projectApi'
import { queryKeys } from '@/services/queryKeys'
import { PROJECT_ROLE_LABELS, type ProjectRole } from '@/types/enums'
import type { CreateProjectRequest, ProjectMember, ProjectSummary } from '@/types/project'

export function useProjects() {
  return useQuery({
    queryKey: queryKeys.projects.list(),
    queryFn: projectApi.list,
  })
}

export function useProject(projectId: number) {
  return useQuery({
    queryKey: queryKeys.projects.detail(projectId),
    queryFn: () => projectApi.get(projectId),
  })
}

export function useProjectMembers(projectId: number) {
  return useQuery({
    queryKey: queryKeys.projects.members(projectId),
    queryFn: () => projectApi.members(projectId),
  })
}

interface RoleChange {
  member: ProjectMember
  role: ProjectRole
}

export function useUpdateMemberRole(projectId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ member, role }: RoleChange) =>
      projectApi.updateMemberRole(projectId, member.userId, { role }),
    onSuccess: (update, { member }) => {
      queryClient.setQueryData<ProjectMember[]>(queryKeys.projects.members(projectId), (members) =>
        members?.map((m) => (m.userId === update.userId ? { ...m, role: update.role } : m)),
      )
      toast.success(`${member.memberName} is now a ${PROJECT_ROLE_LABELS[update.role].toLowerCase()}`)
      // The project list shows "my role", which changes if I changed my own.
      return queryClient.invalidateQueries({ queryKey: queryKeys.projects.list() })
    },
    onError: (error, { member }) => {
      toast.error(`Couldn't change ${member.memberName}'s role`, { description: error.message })
      void queryClient.invalidateQueries({ queryKey: queryKeys.projects.members(projectId) })
    },
  })
}

export function useCreateProject() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: CreateProjectRequest) => projectApi.create(body),
    onSuccess: (project) => {
      toast.success(`Project "${project.name}" created`)
      return queryClient.invalidateQueries({ queryKey: queryKeys.projects.list() })
    },
  })
}

export function useJoinProject() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (project: Pick<ProjectSummary, 'id' | 'name'>) => projectApi.join(project.id),
    onSuccess: (membership, project) => {
      // Reflect the change immediately, then confirm with the server.
      queryClient.setQueryData<ProjectSummary[]>(queryKeys.projects.list(), (projects) =>
        projects?.map((p) =>
          p.id === project.id
            ? { ...p, joined: true, myRole: membership.role, memberCount: p.memberCount + 1 }
            : p,
        ),
      )
      toast.success(`You joined "${project.name}"`)
      return queryClient.invalidateQueries({ queryKey: queryKeys.projects.all })
    },
    onError: (error, project) => {
      toast.error(`Couldn't join "${project.name}"`, { description: error.message })
      // e.g. 409 "already joined": the list was stale, so refresh it.
      void queryClient.invalidateQueries({ queryKey: queryKeys.projects.list() })
    },
  })
}
