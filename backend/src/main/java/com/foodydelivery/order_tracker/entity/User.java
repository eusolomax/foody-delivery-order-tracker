package com.foodydelivery.order_tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String password;

    protected User() {
    }

    public User(String name, String email, String deliveryAddress, String password) {
        this.name = name;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
        this.password = password;
    }

}
