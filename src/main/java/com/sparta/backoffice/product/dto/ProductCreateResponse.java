package com.sparta.backoffice.product.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductStatus;
import lombok.Getter;

@Getter
public class ProductCreateResponse {

	private final Long id;
	private final String name;
	private final ProductCategory category;
	private final int price;
	private final int stock;
	private final ProductStatus status;
	private final Long adminId;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;

	public ProductCreateResponse(
		Long id, String name, ProductCategory category, int price, int stock, ProductStatus status, Long adminId,
		LocalDateTime createdAt, LocalDateTime modifiedAt
	) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.price = price;
		this.stock = stock;
		this.status = status;
		this.adminId = adminId;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public static ProductCreateResponse from(Product product) {
		return new ProductCreateResponse(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice(),
			product.getStock(),
			product.getStatus(),
			product.getAdmin().getId(),
			product.getCreatedAt(),
			product.getModifiedAt()
		);
	}
}