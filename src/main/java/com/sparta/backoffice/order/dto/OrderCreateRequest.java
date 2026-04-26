package com.sparta.backoffice.order.dto;

import lombok.Getter;

@Getter
public class OrderCreateRequest {

	private Long customerId;
	private Long productId;
	private int quantity;

}
