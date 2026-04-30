package com.sparta.backoffice.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
	ON_SALE("판매중"),
	SOLD_OUT("품절"),
	DISCONTINUED("단종");

	private final String description;
}