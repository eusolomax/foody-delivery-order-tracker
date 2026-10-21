import { useState, type FormEvent } from 'react'

import { useAuth } from '../context/AuthContext'

type LoginPageProps = {
  onNavigate: (path: string) => void
}

export function LoginPage({ onNavigate }: LoginPageProps) {
  const { signIn } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [notice] = useState(() => (
    new URLSearchParams(window.location.search).has('registered')
      ? 'Conta criada. Entre para continuar.'
      : ''
  ))
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!email.trim() || !password) {
      setError('Informe seu e-mail e sua senha para continuar.')
      return
    }

    setIsSubmitting(true)
    try {
      await signIn(email.trim(), password)
      onNavigate('/orders')
    } catch (submissionError) {
      setError(submissionError instanceof Error ? submissionError.message : 'Nao foi possivel entrar.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="auth-page">
      <a className="auth-brand" href="/" onClick={(event) => { event.preventDefault(); onNavigate('/') }}>
        <span className="brand-mark" aria-hidden="true">F</span>
        <span>foody<span className="brand-dot">.</span></span>
      </a>
      <section className="auth-card" aria-labelledby="login-title">
        <p className="eyebrow">bem-vindo de volta</p>
        <h1 id="login-title">Entre no<br /><em>seu ritmo.</em></h1>
        <p className="auth-intro">Acompanhe seus pedidos de onde estiver.</p>
        <form className="auth-form" onSubmit={handleSubmit} noValidate>
          <label>
            E-mail
            <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} autoComplete="email" required />
          </label>
          <label>
            Senha
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required />
          </label>
          {notice && <p className="form-success" role="status">{notice}</p>}
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="form-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'} <span aria-hidden="true">↗</span>
          </button>
        </form>
        <p className="auth-switch">Ainda nao tem uma conta? <button type="button" onClick={() => onNavigate('/register')}>Cadastre-se</button></p>
      </section>
    </main>
  )
}
