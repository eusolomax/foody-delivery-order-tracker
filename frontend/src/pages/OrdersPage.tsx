import { useEffect, useState } from 'react'

import { useAuth } from '../context/AuthContext'
import { ApiRequestError, ordersApi } from '../services/api'
import type { OrderResponse, OrderStatus } from '../types/orders'

type OrdersPageProps = {
  onNavigate: (path: string) => void
}

const statusLabels: Record<OrderStatus, string> = {
  RECEBIDO: 'Recebido',
  EM_PREPARO: 'Em preparo',
  SAIU_PARA_ENTREGA: 'Saiu para entrega',
  ENTREGUE: 'Entregue',
  CANCELADO: 'Cancelado',
}

const statusClasses: Record<OrderStatus, string> = {
  RECEBIDO: 'status-received',
  EM_PREPARO: 'status-preparing',
  SAIU_PARA_ENTREGA: 'status-delivering',
  ENTREGUE: 'status-delivered',
  CANCELADO: 'status-cancelled',
}

function formatDate(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return 'Data indisponivel'
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date)
}

function OrderCard({ order }: { order: OrderResponse }) {
  return (
    <article className="order-summary">
      <header className="order-summary-header">
        <div>
          <p className="order-number">pedido / {order.uuid}</p>
          <p className="order-date">{formatDate(order.createdAt)}</p>
        </div>
        <span className={`order-status ${statusClasses[order.status]}`}>
          <span aria-hidden="true" />
          {statusLabels[order.status]}
        </span>
      </header>
      <div className="order-summary-body">
        <div>
          <p className="order-customer">{order.customerName}</p>
          <p className="order-address">{order.deliveryAddress}</p>
        </div>
        <ul className="order-items" aria-label="Itens do pedido">
          {order.items.map((item) => (
            <li key={item.uuid}>
              <span>{item.productName}</span>
              <strong>x{item.quantity}</strong>
            </li>
          ))}
        </ul>
      </div>
    </article>
  )
}

export function OrdersPage({ onNavigate }: OrdersPageProps) {
  const { signOut } = useAuth()
  const [orders, setOrders] = useState<OrderResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let isCurrent = true

    async function loadOrders() {
      try {
        const response = await ordersApi.list()
        if (isCurrent) setOrders(response)
      } catch (requestError) {
        if (!isCurrent) return
        if (requestError instanceof ApiRequestError && requestError.status === 401) {
          signOut()
          onNavigate('/login')
          return
        }
        setError(requestError instanceof Error ? requestError.message : 'Nao foi possivel carregar os pedidos.')
      } finally {
        if (isCurrent) setIsLoading(false)
      }
    }

    loadOrders()
    return () => { isCurrent = false }
  }, [onNavigate, signOut])

  return (
    <main className="orders-page">
      <nav className="topbar">
        <a className="brand" href="/" onClick={(event) => { event.preventDefault(); onNavigate('/') }}>
          <span className="brand-mark" aria-hidden="true">F</span>
          <span>foody<span className="brand-dot">.</span></span>
        </a>
        <div className="orders-nav-actions">
          <button className="new-order-button" type="button" onClick={() => onNavigate('/orders/new')}>Novo pedido <span aria-hidden="true">+</span></button>
          <button className="sign-out" type="button" onClick={() => { signOut(); onNavigate('/login') }}>Sair</button>
        </div>
      </nav>

      <section className="orders-content" aria-labelledby="orders-title">
        <div className="orders-heading">
          <div>
            <p className="eyebrow">area de pedidos</p>
            <h1 id="orders-title">Seus <em>pedidos.</em></h1>
          </div>
          {!isLoading && !error && <span className="orders-count">{orders.length} {orders.length === 1 ? 'pedido' : 'pedidos'}</span>}
        </div>

        {isLoading && <p className="orders-feedback">Carregando seus pedidos...</p>}
        {!isLoading && error && <div className="orders-feedback feedback-error" role="alert"><strong>Algo saiu do forno errado.</strong><span>{error}</span><button type="button" onClick={() => window.location.reload()}>Tentar novamente</button></div>}
        {!isLoading && !error && orders.length === 0 && <div className="orders-feedback"><strong>Nenhum pedido por aqui.</strong><span>Quando você fizer um pedido, ele aparecerá nesta área.</span><button type="button" onClick={() => onNavigate('/orders/new')}>Começar um pedido <span aria-hidden="true">↗</span></button></div>}
        {!isLoading && !error && orders.length > 0 && <div className="orders-list">{orders.map((order) => <OrderCard key={order.uuid} order={order} />)}</div>}
      </section>
    </main>
  )
}
