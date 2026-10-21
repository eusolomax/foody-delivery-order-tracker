export type OrderStatus =
  | 'RECEBIDO'
  | 'EM_PREPARO'
  | 'SAIU_PARA_ENTREGA'
  | 'ENTREGUE'
  | 'CANCELADO'

export type OrderItem = {
  uuid: string
  productName: string
  quantity: number
}

export type OrderItemRequest = {
  productName: string
  quantity: number
}

export type OrderResponse = {
  uuid: string
  customerName: string
  deliveryAddress: string
  status: OrderStatus
  createdAt: string
  items: OrderItem[]
}
