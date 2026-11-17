import type { IsoDateString } from './api'
import type { OrganisationRole } from './enums'

/** UserDto */
export interface User {
  id: number
  name: string
  email: string
  organisationId: number | null
  organisationRole: OrganisationRole | null
  createdAt: IsoDateString
}

/** LoginRequestDto */
export interface LoginRequest {
  email: string
  password: string
}

/** LoginResponseDto */
export interface LoginResponse {
  user: User
  jwtToken: string
}

/** CreateUserRequestDto */
export interface RegisterRequest {
  name: string
  email: string
  password: string
}
