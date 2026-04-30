package com.sparta.backoffice.order.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.enums.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderGetResponse {

	private final Long id;
	private final String orderNumber;
	private final String customerName;
	private final String customerEmail;
	private final String productName;
	private final int quantity;
	private final int orderPrice;
	private final int totalPrice;
	private final LocalDateTime createdAt;
	private final OrderStatus status;
	private final String adminName;
	private final String adminEmail;
	private final String adminRole;

	public static OrderGetResponse from(Order order) {
		return new OrderGetResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getCustomer().getName(),
			order.getCustomer().getEmail(),
			order.getProduct().getName(),
			order.getQuantity(),
			order.getOrderPrice(),
			order.getTotalPrice(),
			order.getCreatedAt(),
			order.getStatus(),
			order.getAdmin() == null ? null : order.getAdmin().getName(),
			order.getAdmin() == null ? null : order.getAdmin().getEmail(),
			order.getAdmin() == null ? null : order.getAdmin().getRole().name()
		);
	}
}