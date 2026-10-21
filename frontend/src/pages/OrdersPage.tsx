import { useEffect, useState } from 'react'

import { useAuth } from '../context/AuthContext'
import { ApiRequestError, ordersApi } from '../services/api'
import type { OrderResponse, OrderStatus } from '../types/orders'

type OrdersPageProps = {
  onNavigate: (path: string) => void
}

type OrderCardProps = {
  order: OrderResponse
  isUpdating: boolean
  onUpdateStatus: (order: OrderResponse, status: OrderStatus) => void
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

function OrderCard({ order, isUpdating, onUpdateStatus }: OrderCardProps) {
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
      <div className="order-actions">
        {allowedTransitions[order.status].map((nextStatus) => (
          <button
            className={nextStatus === 'CANCELADO' ? 'order-action order-action-danger' : 'order-action'}
            disabled={isUpdating}
            key={nextStatus}
            type="button"
            onClick={() => onUpdateStatus(order, nextStatus)}
          >
            {isUpdating ? 'Atualizando...' : statusActionLabels[nextStatus]}
          </button>
        ))}
      </div>
    </article>
  )
}

const allowedTransitions: Record<OrderStatus, OrderStatus[]> = {
  RECEBIDO: ['EM_PREPARO', 'CANCELADO'],
  EM_PREPARO: ['SAIU_PARA_ENTREGA', 'CANCELADO'],
  SAIU_PARA_ENTREGA: ['ENTREGUE'],
  ENTREGUE: [],
  CANCELADO: [],
}

const statusActionLabels: Record<OrderStatus, string> = {
  RECEBIDO: 'Marcar como recebido',
  EM_PREPARO: 'Enviar para preparo',
  SAIU_PARA_ENTREGA: 'Marcar como saiu',
  ENTREGUE: 'Marcar como entregue',
  CANCELADO: 'Cancelar pedido',
}

export function OrdersPage({ onNavigate }: OrdersPageProps) {
  const { signOut } = useAuth()
  const [orders, setOrders] = useState<OrderResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [updatingOrderUuid, setUpdatingOrderUuid] = useState<string | null>(null)
  const [statusMessage, setStatusMessage] = useState('')
  const [successMessage] = useState(() => (
    new URLSearchParams(window.location.search).has('created')
      ? 'Pedido criado com sucesso.'
      : ''
  ))

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

  async function handleStatusUpdate(order: OrderResponse, nextStatus: OrderStatus) {
    const isIrreversible = nextStatus === 'ENTREGUE' || nextStatus === 'CANCELADO'
    if (isIrreversible && !window.confirm(`Confirmar: ${statusActionLabels[nextStatus].toLowerCase()}?`)) {
      return
    }

    setUpdatingOrderUuid(order.uuid)
    setStatusMessage('')
    try {
      const updatedOrder = await ordersApi.updateStatus(order.uuid, nextStatus)
      setOrders((currentOrders) => currentOrders.map((currentOrder) => (
        currentOrder.uuid === updatedOrder.uuid ? updatedOrder : currentOrder
      )))
      setStatusMessage('Status atualizado com sucesso.')
    } catch (requestError) {
      if (requestError instanceof ApiRequestError && requestError.status === 401) {
        signOut()
        onNavigate('/login')
        return
      }
      setError(requestError instanceof Error ? requestError.message : 'Nao foi possivel atualizar o status.')
    } finally {
      setUpdatingOrderUuid(null)
    }
  }

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
        {successMessage && <p className="orders-success" role="status">{successMessage}</p>}
        {statusMessage && <p className="orders-success" role="status">{statusMessage}</p>}

        {isLoading && <p className="orders-feedback">Carregando seus pedidos...</p>}
        {!isLoading && error && <div className="orders-feedback feedback-error" role="alert"><strong>Algo saiu do forno errado.</strong><span>{error}</span><button type="button" onClick={() => window.location.reload()}>Tentar novamente</button></div>}
        {!isLoading && !error && orders.length === 0 && <div className="orders-feedback"><strong>Nenhum pedido por aqui.</strong><span>Quando você fizer um pedido, ele aparecerá nesta área.</span><button type="button" onClick={() => onNavigate('/orders/new')}>Começar um pedido <span aria-hidden="true">↗</span></button></div>}
        {!isLoading && !error && orders.length > 0 && <div className="orders-list">{orders.map((order) => <OrderCard key={order.uuid} order={order} isUpdating={updatingOrderUuid === order.uuid} onUpdateStatus={handleStatusUpdate} />)}</div>}
      </section>
    </main>
  )
}
