import type { ApiResponse } from '@/types/api'
import type {
  CreatedProject,
  CreateProjectRequest,
  ProjectDetails,
  ProjectMember,
  ProjectMembership,
  ProjectRoleUpdate,
  ProjectSummary,
  UpdateProjectRoleRequest,
} from '@/types/project'
import axiosInstance, { unwrap } from './axiosInstance'

export const projectApi = {
  /** All projects in the caller's organisation, with membership flags. */
  list(): Promise<ProjectSummary[]> {
    return unwrap(axiosInstance.get<ApiResponse<ProjectSummary[]>>('/project'))
  },

  get(projectId: number): Promise<ProjectDetails> {
    return unwrap(axiosInstance.get<ApiResponse<ProjectDetails>>(`/project/${projectId}`))
  },

  /** ORGANISATION_MANAGER only. The creator becomes PROJECT_MANAGER. */
  create(body: CreateProjectRequest): Promise<CreatedProject> {
    return unwrap(axiosInstance.post<ApiResponse<CreatedProject>>('/project/create', body))
  },

  /** EMPLOYEE only. Joins as MEMBER. */
  join(projectId: number): Promise<ProjectMembership> {
    return unwrap(axiosInstance.post<ApiResponse<ProjectMembership>>(`/project/${projectId}/join`))
  },

  members(projectId: number): Promise<ProjectMember[]> {
    return unwrap(axiosInstance.get<ApiResponse<ProjectMember[]>>(`/project/${projectId}/members`))
  },

  /** ORGANISATION_MANAGER only. */
  updateMemberRole(
    projectId: number,
    userId: number,
    body: UpdateProjectRoleRequest,
  ): Promise<ProjectRoleUpdate> {
    return unwrap(
      axiosInstance.patch<ApiResponse<ProjectRoleUpdate>>(
        `/project/${projectId}/members/${userId}/role`,
        body,
      ),
    )
  },
}
