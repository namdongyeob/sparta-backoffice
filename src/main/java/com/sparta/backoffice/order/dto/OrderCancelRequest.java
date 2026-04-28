package com.sparta.backoffice.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCancelRequest {

	@NotBlank(message = "주문 취소 사유는 필수입니다.")
	private String cancelReason;
}