import { useAuth } from '../context/AuthContext'

export function OrdersPage({ onNavigate }: { onNavigate: (path: string) => void }) {
  const { signOut } = useAuth()

  function handleSignOut() {
    signOut()
    onNavigate('/login')
  }

  return (
    <main className="orders-page">
      <nav className="topbar">
        <a className="brand" href="/" onClick={(event) => { event.preventDefault(); onNavigate('/') }}>
          <span className="brand-mark" aria-hidden="true">F</span>
          <span>foody<span className="brand-dot">.</span></span>
        </a>
        <button className="sign-out" type="button" onClick={handleSignOut}>Sair</button>
      </nav>
      <section className="orders-empty">
        <p className="eyebrow">area de pedidos</p>
        <h1>Seu proximo<br /><em>pedido começa aqui.</em></h1>
        <p>A listagem de pedidos entra na proxima etapa.</p>
      </section>
    </main>
  )
}
