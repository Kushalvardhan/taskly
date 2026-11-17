import type { ApiResponse } from '@/types/api'
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types/auth'
import axiosInstance, { unwrap } from './axiosInstance'

export const authApi = {
  login(body: LoginRequest): Promise<LoginResponse> {
    return unwrap(axiosInstance.post<ApiResponse<LoginResponse>>('/auth/login', body))
  },

  register(body: RegisterRequest): Promise<string> {
    return unwrap(axiosInstance.post<ApiResponse<string>>('/auth/register', body))
  },
}
