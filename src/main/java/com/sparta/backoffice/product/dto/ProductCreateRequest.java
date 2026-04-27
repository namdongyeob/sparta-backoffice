package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.entity.Product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProductCreateRequest {

	@NotBlank(message = "상품명은 필수값입니다.")
	@Size(min = 1, message = "상품명은 비어있을 수 없습니다.")
	private String name;

	@NotNull(message = "카테고리는 필수값입니다.")
	private ProductCategory category;

	@Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
	private int price;

	@Min(value = 1, message = "재고는 1개 이상이어야 합니다.")
	private int stock;

	public Product toEntity(Admin admin) {
		return new Product(
			this.name,
			this.category,
			this.price,
			this.stock,
			admin
		);
	}
}