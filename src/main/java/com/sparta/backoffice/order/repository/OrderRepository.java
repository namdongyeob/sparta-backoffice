package com.sparta.backoffice.order.repository;

import java.time.LocalDateTime;

import com.sparta.backoffice.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	long countByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
