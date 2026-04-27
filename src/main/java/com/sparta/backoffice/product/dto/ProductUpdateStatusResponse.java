package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductUpdateStatusResponse {

	private final Long id;
	private final String name;
	private final ProductStatus status;

	public static ProductUpdateStatusResponse from(Product product) {
		return new ProductUpdateStatusResponse(
			product.getId(),
			product.getName(),
			product.getStatus()
		);
	}
}