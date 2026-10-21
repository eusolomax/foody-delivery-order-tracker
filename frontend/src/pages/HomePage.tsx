export function HomePage() {
  return (
    <main className="home-page">
      <nav className="topbar" aria-label="Navegacao principal">
        <a className="brand" href="/" aria-label="Foody Delivery inicio">
          <span className="brand-mark" aria-hidden="true">F</span>
          <span>foody<span className="brand-dot">.</span></span>
        </a>
      </nav>

      <section className="hero" aria-labelledby="hero-title">
        <div className="hero-copy">
          <p className="eyebrow">delivery, sem ruido</p>
          <h1 id="hero-title">Do forno<br /><em>ate a porta.</em></h1>
          <p className="hero-text">
            Uma forma mais clara de acompanhar cada pedido. O Foody conecta cozinha,
            entrega e cliente em um so lugar.
          </p>
          <div className="hero-actions">
            <a className="primary-action" href="/login">
              Entrar no aplicativo <span aria-hidden="true">↗</span>
            </a>
            <span className="hero-note">feito para operacoes que<br />nao param</span>
          </div>
        </div>

        <div className="hero-art" aria-label="Ilustracao de um pedido sendo preparado">
          <div className="sun" />
          <div className="art-card order-card">
            <div className="card-kicker">pedido #0048</div>
            <div className="order-line"><span>Pizza da casa</span><strong>01</strong></div>
            <div className="order-line"><span>Cha gelado</span><strong>02</strong></div>
            <div className="card-divider" />
            <div className="order-total"><span>total</span><strong>R$ 68,90</strong></div>
          </div>
          <div className="delivery-tag"><span aria-hidden="true">✦</span> a caminho</div>
          <div className="art-sticker">bom<br />apetite!</div>
          <div className="plate" aria-hidden="true"><span /></div>
        </div>
      </section>

      <footer className="footer">
        <span>FOODY DELIVERY <span className="footer-year">/ 2026</span></span>
        <span>feito com cuidado, do pedido a entrega</span>
      </footer>
    </main>
  )
}
