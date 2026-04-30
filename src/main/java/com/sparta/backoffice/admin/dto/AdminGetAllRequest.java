package com.sparta.backoffice.admin.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminGetAllRequest {
	private String keyword;       // 검색 키워드 (이름, 이메일)
	private int page = 1;         // 페이지 번호 (기본값: 1)
	private int size = 10;        // 페이지당 개수 (기본값: 10)
	private String sortBy;        // 정렬 기준
	private String direction;     // 정렬 순서 (asc, desc)
	private AdminRole role;       // 역할 필터
	private AdminStatus status;   // 상태 필터

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

