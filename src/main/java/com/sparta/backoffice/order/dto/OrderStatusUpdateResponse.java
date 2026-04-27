package com.sparta.backoffice.order.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.enums.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderStatusUpdateResponse {

	private final Long id;
	private final String orderNumber;
	private final OrderStatus status;
	private final String statusDescription;
	private final LocalDateTime modifiedAt;

	public static OrderStatusUpdateResponse from(Order order) {
		return new OrderStatusUpdateResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getStatus(),
			order.getStatus().getDescription(),
			order.getModifiedAt()
		);
	}
}
