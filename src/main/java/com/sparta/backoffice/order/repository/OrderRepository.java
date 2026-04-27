package com.sparta.backoffice.order.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o "
		+ "JOIN FETCH o.customer c "
		+ "JOIN o.product p "
		+ "LEFT JOIN o.admin a "
		+ "WHERE (:keyword IS NULL OR o.orderNumber LIKE CONCAT('%', :keyword, '%') "
		+ "OR c.name LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:status IS NULL OR o.status = :status)")
	Page<Order> searchOrders(
		@Param("keyword") String keyword,
		@Param("status") OrderStatus status,
		Pageable pageable
	);

	long countByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}