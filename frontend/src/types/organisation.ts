import type { IsoDateString } from './api'
import type { OrganisationRole } from './enums'

/** OrganisationResponseDto */
export interface Organisation {
  id: number
  name: string
  code: string
  createdById: number
  createdAt: IsoDateString
  updatedAt: IsoDateString | null
}

/** OrganisationUserDto */
export interface OrganisationUser {
  id: number
  name: string
  email: string
  organisationRole: OrganisationRole
}

/** OrgAuthDataDto – returned by register/join; carries a NEW token. */
export interface OrganisationAuthData {
  organisation: Organisation
  user: OrganisationUser
  jwtToken: string
}

/** CreateOrgRequestDto */
export interface CreateOrganisationRequest {
  name: string
  code: string
}

/** JoinOrgRequestDto */
export interface JoinOrganisationRequest {
  organisationCode: string
}
