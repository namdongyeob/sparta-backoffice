package com.sparta.backoffice.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
	ELECTRONICS("전자기기"),
	FASHION("패션/의류"),
	FOOD("식품");

	private final String description;
}