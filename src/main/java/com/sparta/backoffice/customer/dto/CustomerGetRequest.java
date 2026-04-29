package com.sparta.backoffice.customer.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;

@Getter
public class CustomerGetRequest {
	private String keyword;       // 검색 키워드 (주문번호, 고객명)
	private int page = 1;         // 페이지 번호 (기본값: 1)
	private int size = 10;        // 페이지당 개수 (기본값: 10)
	private String sortBy;        // 정렬 기준
	private String sortOrder;     // 정렬 순서 (asc, desc)
	private CustomerStatus status;   // 상태 필터

	public CustomerGetRequest(CustomerStatus status, String sortOrder, String sortBy, int size, int page,
		String keyword) {
		this.status = status;
		this.sortOrder = sortOrder;
		this.sortBy = sortBy;
		this.size = size;
		this.page = page;
		this.keyword = keyword;
	}

	public Pageable getPageable() {
		Sort sort;

		if (sortOrder == null || sortOrder.isBlank()) {
			sort = Sort.by(
				Sort.Order.desc("createdAt")
			);
		} else {
			Sort.Direction sortDirection =
				"asc".equalsIgnoreCase(sortOrder)
					? Sort.Direction.ASC
					: Sort.Direction.DESC;

			sort = Sort.by(sortDirection, sortBy);
		}

		return PageRequest.of(
			page - 1,
			size,
			sort
		);
	}
}
