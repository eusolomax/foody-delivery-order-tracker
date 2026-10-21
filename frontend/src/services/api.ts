const API_URL = import.meta.env.VITE_API_URL ?? ''
const TOKEN_KEY = 'foody.accessToken'

type ApiErrorResponse = {
  message?: string
}

type LoginResponse = {
  accessToken: string
  expiresIn: number
}

type RegisterResponse = {
  uuid: string
  name: string
  email: string
  deliveryAddress: string
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')

  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers })
  if (response.ok) {
    return response.json() as Promise<T>
  }

  const error = await response.json().catch(() => null) as ApiErrorResponse | null
  throw new Error(error?.message ?? 'Nao foi possivel concluir a solicitacao.')
}

export const authApi = {
  login(email: string, password: string) {
    return request<LoginResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })
  },

  register(name: string, email: string, password: string, deliveryAddress: string) {
    return request<RegisterResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password, deliveryAddress }),
    })
  },
}

export { TOKEN_KEY }
