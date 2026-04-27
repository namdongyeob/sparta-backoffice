package com.sparta.backoffice.product.dto;

import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductGetAllRequest {

	private String keyword;        // 상품명 검색

	@Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
	private int page = 1;          // 기본값

	@Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
	private int size = 10;         // 기본값

	private String sortBy;         // price, stock, createdAt
	private String direction;      // asc, desc

	private ProductCategory category; // 카테고리 필터
	private ProductStatus status;     // 상태 필터
}