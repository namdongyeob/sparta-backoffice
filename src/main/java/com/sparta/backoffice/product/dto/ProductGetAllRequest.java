package com.sparta.backoffice.product.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductGetAllRequest {

	@Size(max = 50, message = "검색어는 50자 이내여야 합니다.")
	private String keyword;        // 상품명 검색

	@Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
	private int page = 1;          // 기본값

	@Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
	private int size = 10;         // 기본값

	@Pattern(regexp = "price|stock|createdAt", message = "정렬 기준은 price, stock, createdAt 중 하나여야 합니다.")
	private String sortBy;         // price, stock, createdAt

	@Pattern(regexp = "asc|desc", message = "정렬 순서는 asc 또는 desc 중 하나여야 합니다.")
	private String direction;      // asc, desc

	private ProductCategory category; // 카테고리 필터
	private ProductStatus status;     // 상태 필터

	public Pageable toPageable() {
		String sortField = (sortBy == null || sortBy.isBlank())
			? "createdAt" : sortBy;

		Sort sort;

		if (direction == null || direction.isBlank()) {
			// 기본 정렬
			sort = Sort.by(
				Sort.Order.desc(sortField)
			);
		} else {
			Sort.Direction sortDirection =
				"asc".equalsIgnoreCase(direction)
					? Sort.Direction.ASC
					: Sort.Direction.DESC;

			sort = Sort.by(sortDirection, sortField);
		}

		return PageRequest.of(page - 1, size, sort);
	}

	public String getNormalizedKeyword() {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return keyword;
	}
}