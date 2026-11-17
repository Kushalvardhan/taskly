# Taskly – Frontend

React + TypeScript + Vite client for the Taskly backend (`../backend`).

## Stack

- **React 19**, **TypeScript**, **Vite**
- **React Router** (data router, lazy-loaded pages)
- **TanStack Query** for server state, **Axios** for HTTP
- **React Hook Form** + **Zod** for forms and validation
- **Tailwind CSS v4** for styling, **lucide-react** icons, **sonner** toasts
- **Context API** for the auth session

## Getting started

```bash
cp .env.example .env   # first time only
npm install
npm run dev            # http://localhost:5173
```

The backend must be running (default `http://localhost:8080`). In development,
the browser calls `/api/...` on the Vite server, which proxies to the backend
(`API_PROXY_TARGET` in `.env`), so CORS is not involved.

| Variable            | Used by       | Default                 |
| ------------------- | ------------- | ----------------------- |
| `VITE_API_BASE_URL` | browser       | `/api`                  |
| `API_PROXY_TARGET`  | dev server    | `http://localhost:8080` |

## Scripts

| Command           | Description                       |
| ----------------- | --------------------------------- |
| `npm run dev`     | Start the dev server              |
| `npm run build`   | Type-check and build to `dist/`   |
| `npm run preview` | Serve the production build        |
| `npm run lint`    | Run ESLint                        |

## Project structure

```
src/
├── components/
│   ├── common/      # Button, Input, FormField, Alert, Badge, Spinner, Logo
│   └── layout/      # RootLayout, AppLayout, AuthLayout
├── config/          # Environment config
├── context/         # AuthProvider + context definition
├── hooks/           # useAuth, useCurrentUser
├── pages/           # Route pages (lazy-loaded)
├── routes/          # Router, route guards, path helpers
├── services/
│   ├── api/         # Axios instance, error normalisation, API modules
│   ├── auth/        # Session token holder, persisted auth storage
│   └── queryClient.ts
├── types/           # Backend DTO and enum types
└── utils/           # cn, jwt, storage, forms, format helpers
```

## Auth & errors

- The JWT from login (or from creating/joining an organisation) is stored in
  `localStorage` and sent as `Authorization: Bearer <token>`.
- Sessions end automatically when the token expires (24h), when the backend
  rejects the token, or when another tab signs out.
- Every API failure is normalised into an `AppError`
  (`status`, `message`, `fieldErrors`) – see `src/services/api/apiError.ts`.
