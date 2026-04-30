package com.sparta.backoffice.order.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.order.entity.Order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderCreateResponse {

	private final Long id;
	private final String orderNumber;
	private final String customerName;
	private final String productName;
	private final int quantity;
	private final int orderPrice;
	private final int totalPrice;
	private final String status;
	private final LocalDateTime createdAt;
	private final String adminName;
	private final String adminEmail;
	private final String adminRole;

	public static OrderCreateResponse from(Order order) {
		return new OrderCreateResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getCustomer().getName(),
			order.getProduct().getName(),
			order.getQuantity(),
			order.getOrderPrice(),
			order.getTotalPrice(),
			order.getStatus().getDescription(),
			order.getCreatedAt(),
			order.getAdmin() == null ? null : order.getAdmin().getName(),
			order.getAdmin() == null ? null : order.getAdmin().getEmail(),
			order.getAdmin() == null ? null : order.getAdmin().getRole().getDescription()
		);
	}
}
