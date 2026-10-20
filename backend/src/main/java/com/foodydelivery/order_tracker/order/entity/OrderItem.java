package com.foodydelivery.order_tracker.order.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    @Setter
    private String productName;

    @Column(nullable = false)
    @Setter
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Order order;

    @PrePersist
    void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    protected OrderItem() {
    }

    public OrderItem(String productName, Integer quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}
