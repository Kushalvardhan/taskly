import type { ApiResponse } from '@/types/api'
import type {
  CreateOrganisationRequest,
  JoinOrganisationRequest,
  OrganisationAuthData,
} from '@/types/organisation'
import axiosInstance, { unwrap } from './axiosInstance'

export const organisationApi = {
  /** Creates an organisation; the caller becomes ORGANISATION_MANAGER. Returns a new token. */
  create(body: CreateOrganisationRequest): Promise<OrganisationAuthData> {
    return unwrap(axiosInstance.post<ApiResponse<OrganisationAuthData>>('/organisation/register', body))
  },

  /** Joins by code; the caller becomes EMPLOYEE. Returns a new token. */
  join(body: JoinOrganisationRequest): Promise<OrganisationAuthData> {
    return unwrap(axiosInstance.post<ApiResponse<OrganisationAuthData>>('/organisation/join', body))
  },
}
