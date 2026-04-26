package com.sparta.backoffice.order.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.enums.OrderStatus;

import lombok.Getter;

@Getter
public class OrderCreateResponse {

	private final Long id;
	private final String orderNumber;
	private final String customerName;
	private final String productName;
	private final int quantity;
	private final int orderPrice;
	private final int totalPrice;
	private final String status;// 재고 변경에 따른 상품 상태
	private final LocalDateTime createdAt;

	public OrderCreateResponse(Long id, String orderNumber,  String customerName, String productName,
		int quantity, int orderPrice, int totalPrice, String status, LocalDateTime createdAt) {
		this.id = id;
		this.orderNumber = orderNumber;
		this.customerName = customerName;
		this.productName = productName;
		this.quantity = quantity;
		this.orderPrice = orderPrice;
		this.totalPrice = totalPrice;
		this.status = status;
		this.createdAt = createdAt;
	}

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
			order.getCreatedAt()
		);
	}
}
