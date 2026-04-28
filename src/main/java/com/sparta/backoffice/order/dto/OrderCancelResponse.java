package com.sparta.backoffice.order.dto;

import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.enums.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderCancelResponse {

	private final OrderStatus status;
	private final String statusDescription;

	public static OrderCancelResponse from(Order order) {
		return new OrderCancelResponse(
			order.getStatus(),
			order.getStatus().getDescription()
		);
	}
}