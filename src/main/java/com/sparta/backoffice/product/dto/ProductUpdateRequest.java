package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.enums.ProductCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

	@NotBlank(message = "상품명은 필수입니다.")
	private String name;

	@NotNull(message = "카테고리는 필수입니다.")
	private ProductCategory category;

	@NotNull(message = "가격은 필수입니다.")
	@PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
	private Integer price;
}