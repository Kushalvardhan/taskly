import type { IsoDateString } from './api'
import type { ProjectRole } from './enums'

/** ProjectListDto */
export interface ProjectSummary {
  id: number
  name: string
  memberCount: number
  joined: boolean
  myRole: ProjectRole | null
}

/** ViewProjectDto */
export interface ProjectDetails {
  id: number
  name: string
  description: string | null
  orgName: string
  orgCreatorName: string
  createdAt: IsoDateString
}

/** ProjectDto – returned by create. `createdBy` is the creator's email. */
export interface CreatedProject {
  id: number
  name: string
  description: string | null
  organisationId: number
  createdBy: string
  createdAt: IsoDateString
}

/** CreateProjReqDto */
export interface CreateProjectRequest {
  name: string
  description?: string
}

/** ProjectMemberDto – returned by join. */
export interface ProjectMembership {
  id: number
  projectId: number
  userId: number
  role: ProjectRole
  joinedAt: IsoDateString
}

/** ProjectMemberListDto */
export interface ProjectMember {
  userId: number
  memberName: string
  memberEmail: string
  role: ProjectRole
}

/** UpdateProjectRoleRequestDto */
export interface UpdateProjectRoleRequest {
  role: ProjectRole
}

/** UpdateProjectRoleDto */
export interface ProjectRoleUpdate {
  projectId: number
  userId: number
  role: ProjectRole
}
