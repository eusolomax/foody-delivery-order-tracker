import { useState, type FormEvent } from 'react'

import { useAuth } from '../context/AuthContext'
import { ApiRequestError, ordersApi } from '../services/api'
import type { OrderItemRequest } from '../types/orders'

type NewOrderPageProps = {
  onNavigate: (path: string) => void
}

function createKey() {
  return crypto.randomUUID()
}

export function NewOrderPage({ onNavigate }: NewOrderPageProps) {
  const { signOut } = useAuth()
  const [items, setItems] = useState<OrderItemRequest[]>([{ productName: '', quantity: 1 }])
  const [idempotencyKey, setIdempotencyKey] = useState(createKey)
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  function updateItems(nextItems: OrderItemRequest[]) {
    setItems(nextItems)
    setIdempotencyKey(createKey())
    setError('')
  }

  function updateItem(index: number, field: keyof OrderItemRequest, value: string) {
    const nextItems = items.map((item, itemIndex) => (
      itemIndex === index
        ? { ...item, [field]: field === 'quantity' ? Number(value) : value }
        : item
    ))
    updateItems(nextItems)
  }

  function addItem() {
    updateItems([...items, { productName: '', quantity: 1 }])
  }

  function removeItem(index: number) {
    if (items.length === 1) return
    updateItems(items.filter((_, itemIndex) => itemIndex !== index))
  }

  function validateItems() {
    if (items.length === 0) return 'Adicione pelo menos um item ao pedido.'
    if (items.some((item) => !item.productName.trim())) return 'Informe o nome de todos os produtos.'
    if (items.some((item) => !Number.isInteger(item.quantity) || item.quantity <= 0)) {
      return 'As quantidades devem ser números inteiros maiores que zero.'
    }
    return ''
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const validationError = validateItems()
    if (validationError) {
      setError(validationError)
      return
    }

    setIsSubmitting(true)
    setError('')
    try {
      await ordersApi.create(items, idempotencyKey)
      onNavigate('/orders?created=1')
    } catch (requestError) {
      if (requestError instanceof ApiRequestError && requestError.status === 401) {
        signOut()
        onNavigate('/login')
        return
      }
      setError(requestError instanceof Error ? requestError.message : 'Nao foi possivel criar o pedido.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="orders-page">
      <nav className="topbar">
        <a className="brand" href="/orders" onClick={(event) => { event.preventDefault(); onNavigate('/orders') }}>
          <span className="brand-mark" aria-hidden="true">F</span>
          <span>foody<span className="brand-dot">.</span></span>
        </a>
        <button className="sign-out" type="button" onClick={() => onNavigate('/orders')}>Voltar</button>
      </nav>
      <section className="new-order-content" aria-labelledby="new-order-title">
        <p className="eyebrow">cozinha aberta</p>
        <h1 id="new-order-title">Monte seu<br /><em>pedido.</em></h1>
        <form className="order-form" onSubmit={handleSubmit} noValidate>
          <div className="order-form-heading">
            <span>Itens do pedido</span>
            <span>{items.length} {items.length === 1 ? 'item' : 'itens'}</span>
          </div>
          {items.map((item, index) => (
            <div className="order-form-row" key={index}>
              <label>
                Produto
                <input
                  value={item.productName}
                  onChange={(event) => updateItem(index, 'productName', event.target.value)}
                  placeholder="Ex: Pizza da casa"
                  disabled={isSubmitting}
                />
              </label>
              <label className="quantity-field">
                Qtd.
                <input
                  type="number"
                  min="1"
                  step="1"
                  value={item.quantity}
                  onChange={(event) => updateItem(index, 'quantity', event.target.value)}
                  disabled={isSubmitting}
                />
              </label>
              <button className="remove-item" type="button" onClick={() => removeItem(index)} disabled={items.length === 1 || isSubmitting} aria-label={`Remover ${item.productName || 'item'}`}>
                ×
              </button>
            </div>
          ))}
          <button className="add-item" type="button" onClick={addItem} disabled={isSubmitting}>+ Adicionar item</button>
          {error && <p className="form-error" role="alert">{error}</p>}
          <div className="order-form-footer">
            <span className="idempotency-note">Chave da operação preservada<br />até a confirmação do pedido.</span>
            <button className="form-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Enviando...' : 'Confirmar pedido'} <span aria-hidden="true">↗</span>
            </button>
          </div>
        </form>
      </section>
    </main>
  )
}
