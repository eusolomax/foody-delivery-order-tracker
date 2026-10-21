export function NewOrderPlaceholderPage({ onNavigate }: { onNavigate: (path: string) => void }) {
  return (
    <main className="orders-page">
      <nav className="topbar">
        <a className="brand" href="/orders" onClick={(event) => { event.preventDefault(); onNavigate('/orders') }}>
          <span className="brand-mark" aria-hidden="true">F</span>
          <span>foody<span className="brand-dot">.</span></span>
        </a>
      </nav>
      <section className="orders-empty">
        <p className="eyebrow">novo pedido</p>
        <h1>Estamos preparando<br /><em>essa mesa.</em></h1>
        <p>O formulário de criação de pedidos estará disponível em breve.</p>
        <button className="new-order-button placeholder-back" type="button" onClick={() => onNavigate('/orders')}>Voltar aos pedidos</button>
      </section>
    </main>
  )
}
