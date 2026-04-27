package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.enums.ProductStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateStatusRequest {

	@NotNull(message = "변경할 상품 상태는 필수입니다.")
	private ProductStatus status;
}