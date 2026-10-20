package com.foodydelivery.order_tracker.order.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.foodydelivery.order_tracker.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "idempotency_key", unique = true, length = 255, updatable = false)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private OrderStatus status = OrderStatus.RECEBIDO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    protected Order() {
    }

    public Order(User user) {
        this.user = user;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
