package com.sparta.backoffice.order.dto;

import com.sparta.backoffice.order.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusUpdateRequest {

	@NotNull(message = "주문 상태는 필수입니다.")
	private OrderStatus status;
}
