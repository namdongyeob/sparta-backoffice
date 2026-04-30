package com.sparta.backoffice.order.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.backoffice.order.enums.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderGetAllRequest {

	private String keyword;       // 검색 키워드 (주문번호, 고객명)
	private int page = 1;         // 페이지 번호 (기본값: 1)
	private int size = 10;        // 페이지당 개수 (기본값: 10)
	private String sortBy;        // 정렬 기준
	private String direction;     // 정렬 순서 (asc, desc)
	private OrderStatus status;   // 상태 필터

	public Pageable getPageable() {
		String sortField = (sortBy == null || sortBy.isBlank())
			? "createdAt"
			: sortBy;

		Sort.Direction sortDirection;

		if (direction == null || direction.isBlank()) {
			sortDirection = Sort.Direction.DESC;
		} else {
			sortDirection = "asc".equalsIgnoreCase(direction)
				? Sort.Direction.ASC
				: Sort.Direction.DESC;
		}

		Sort sort;

		// 기본 정렬: 최신순 + 같은 시간에서는 주문번호 순서
		if ("createdAt".equals(sortField)) {
			sort = Sort.by(
				Sort.Order.desc("createdAt"),
				Sort.Order.asc("orderNumber")
			);
		} else {
			sort = Sort.by(sortDirection, sortField);
		}

		return PageRequest.of(
			page - 1,
			size,
			sort
		);
	}
}