import { useState, type FormEvent } from 'react'

import { authApi } from '../services/api'

type RegisterPageProps = {
  onNavigate: (path: string) => void
}

export function RegisterPage({ onNavigate }: RegisterPageProps) {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [deliveryAddress, setDeliveryAddress] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!name.trim() || !email.trim() || !password || !deliveryAddress.trim()) {
      setError('Preencha todos os campos para criar sua conta.')
      return
    }

    setIsSubmitting(true)
    try {
      await authApi.register(name.trim(), email.trim(), password, deliveryAddress.trim())
      onNavigate('/login?registered=1')
    } catch (submissionError) {
      setError(submissionError instanceof Error ? submissionError.message : 'Nao foi possivel criar sua conta.')
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
      <section className="auth-card register-card" aria-labelledby="register-title">
        <p className="eyebrow">comece por aqui</p>
        <h1 id="register-title">Crie seu<br /><em>foody.</em></h1>
        <form className="auth-form" onSubmit={handleSubmit} noValidate>
          <label>
            Nome
            <input value={name} onChange={(event) => setName(event.target.value)} autoComplete="name" required />
          </label>
          <label>
            E-mail
            <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} autoComplete="email" required />
          </label>
          <label>
            Senha
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="new-password" required />
          </label>
          <label>
            Endereco de entrega
            <input value={deliveryAddress} onChange={(event) => setDeliveryAddress(event.target.value)} autoComplete="street-address" required />
          </label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="form-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Criando...' : 'Criar conta'} <span aria-hidden="true">↗</span>
          </button>
        </form>
        <p className="auth-switch">Ja tem uma conta? <button type="button" onClick={() => onNavigate('/login')}>Entrar</button></p>
      </section>
    </main>
  )
}
