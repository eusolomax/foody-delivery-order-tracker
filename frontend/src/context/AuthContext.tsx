import { createContext, useContext, useState, type PropsWithChildren } from 'react'

import { authApi, TOKEN_KEY } from '../services/api'

type AuthContextValue = {
  isAuthenticated: boolean
  signIn: (email: string, password: string) => Promise<void>
  signOut: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: PropsWithChildren) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY))

  async function signIn(email: string, password: string) {
    const response = await authApi.login(email, password)
    localStorage.setItem(TOKEN_KEY, response.accessToken)
    setToken(response.accessToken)
  }

  function signOut() {
    localStorage.removeItem(TOKEN_KEY)
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated: Boolean(token), signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }
  return context
}
