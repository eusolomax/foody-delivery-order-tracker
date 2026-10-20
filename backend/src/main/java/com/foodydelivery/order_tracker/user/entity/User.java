package com.foodydelivery.order_tracker.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private String email;

    @Column(nullable = false)
    @Setter
    private String deliveryAddress;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String password;

    protected User() {
    }

    public User(String name, String email, String deliveryAddress, String password) {
        this.name = name;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
        this.password = password;
    }

    @PrePersist
    void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public void setPasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

}
