package com.sparta.backoffice.product.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductGetResponse {

	private final String name;
	private final ProductCategory category;
	private final int price;
	private final int stock;
	private final ProductStatus status;
	private final LocalDateTime createdAt;
	private final String adminName;
	private final String adminEmail;

	public static ProductGetResponse from(Product product) {
		return new ProductGetResponse(
			product.getName(),
			product.getCategory(),
			product.getPrice(),
			product.getStock(),
			product.getStatus(),
			product.getCreatedAt(),
			product.getAdmin().getName(),
			product.getAdmin().getEmail()
		);
	}
}