package com.sparta.backoffice.product.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductUpdateResponse {

	private final Long id;
	private final String name;
	private final ProductCategory category;
	private final int price;
	private final int stock;
	private final ProductStatus status;
	private final String adminName;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;

	public static ProductUpdateResponse from(Product product) {
		return new ProductUpdateResponse(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice(),
			product.getStock(),
			product.getStatus(),
			product.getAdmin().getName(),
			product.getCreatedAt(),
			product.getModifiedAt()
		);
	}
}