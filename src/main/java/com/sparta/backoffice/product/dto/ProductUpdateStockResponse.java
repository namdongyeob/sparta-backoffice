package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductUpdateStockResponse {

	private final Long id;
	private final String name;
	private final int stock;
	private final ProductStatus status;

	public static ProductUpdateStockResponse from(Product product) {
		return new ProductUpdateStockResponse(
			product.getId(),
			product.getName(),
			product.getStock(),
			product.getStatus()
		);
	}
}