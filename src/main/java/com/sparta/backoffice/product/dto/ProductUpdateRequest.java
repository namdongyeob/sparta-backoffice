package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.enums.ProductCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

	@Size(min = 1, max = 100, message = "상품명은 1자 이상 100자 이하여야 합니다.")
	private String name;

	private ProductCategory category;

	@Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
	private Integer price;
}