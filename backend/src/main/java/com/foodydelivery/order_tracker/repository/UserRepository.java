package com.foodydelivery.order_tracker.repository;

import java.util.Optional;
import java.util.UUID;

import com.foodydelivery.order_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(UUID uuid);
}
