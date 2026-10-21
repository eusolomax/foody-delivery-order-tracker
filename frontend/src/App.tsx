import { useEffect, useState } from 'react'

import { useAuth } from './context/AuthContext'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { OrdersPage } from './pages/OrdersPage'
import { RegisterPage } from './pages/RegisterPage'

export function App() {
  const [path, setPath] = useState(() => window.location.pathname)
  const { isAuthenticated } = useAuth()

  useEffect(() => {
    const handlePopState = () => setPath(window.location.pathname)
    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  function navigate(nextPath: string) {
    const [pathname] = nextPath.split('?')
    window.history.pushState({}, '', nextPath)
    setPath(pathname)
  }

  if (path === '/login') return <LoginPage onNavigate={navigate} />
  if (path === '/register') return <RegisterPage onNavigate={navigate} />
  if (path === '/orders') {
    if (!isAuthenticated) {
      return <LoginPage onNavigate={navigate} />
    }
    return <OrdersPage onNavigate={navigate} />
  }
  return <HomePage />
}
