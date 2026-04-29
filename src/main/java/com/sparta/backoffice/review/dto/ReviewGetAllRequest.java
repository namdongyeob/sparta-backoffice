package com.sparta.backoffice.review.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewGetAllRequest {
	private String keyword;
	private int page = 1;
	private int size = 10;
	private String sortBy;
	private String direction;
	private Integer rating;

	public Pageable getPageable() {
		Sort sort;

		if (sortBy == null || sortBy.isBlank()) {
			sort = Sort.by(Sort.Order.desc("createdAt"));
		} else {
			Sort.Direction sortDirection =
				"asc".equalsIgnoreCase(direction)
					? Sort.Direction.ASC
					: Sort.Direction.DESC;
			sort = Sort.by(sortDirection, sortBy);
		}

		return PageRequest.of(page - 1, size, sort);
	}
}