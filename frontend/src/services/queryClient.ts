import { QueryClient } from '@tanstack/react-query'
import { AppError } from '@/services/api/apiError'

// Every query/mutation error is an AppError (see the axios response interceptor).
declare module '@tanstack/react-query' {
  interface Register {
    defaultError: AppError
  }
}

const MAX_RETRIES = 2

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      // Retry only transient failures (network / 5xx), never 4xx.
      retry: (failureCount, error) => {
        const transient = error.isNetworkError || (error.status !== null && error.status >= 500)
        return transient && failureCount < MAX_RETRIES
      },
    },
    mutations: {
      retry: false,
    },
  },
})
