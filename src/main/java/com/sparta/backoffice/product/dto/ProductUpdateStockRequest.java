package com.sparta.backoffice.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateStockRequest {

	@NotNull(message = "재고는 필수입니다.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	private Integer stock;
}