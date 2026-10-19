package com.foodydelivery.order_tracker.repository;

import com.foodydelivery.order_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
