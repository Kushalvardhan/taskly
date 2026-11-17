/**
 * Framework-free holder for the current access token, so the axios
 * interceptors can read it without importing React state.
 * AuthProvider is the only writer.
 */

type UnauthorizedHandler = () => void

let accessToken: string | null = null
let unauthorizedHandler: UnauthorizedHandler | null = null

export const authSession = {
  getToken(): string | null {
    return accessToken
  },

  setToken(token: string | null): void {
    accessToken = token
  },

  /** Registers the callback run when the backend rejects the session. Returns an unsubscribe function. */
  setUnauthorizedHandler(handler: UnauthorizedHandler): () => void {
    unauthorizedHandler = handler
    return () => {
      if (unauthorizedHandler === handler) unauthorizedHandler = null
    }
  },

  notifyUnauthorized(): void {
    unauthorizedHandler?.()
  },
}
