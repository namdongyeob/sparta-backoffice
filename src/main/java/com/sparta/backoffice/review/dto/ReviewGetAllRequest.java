package com.sparta.backoffice.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewGetAllRequest {
	private String keyword;       // 검색 키워드 (이름, 이메일)
	private int page = 1;         // 페이지 번호 (기본값: 1)
	private int size = 10;        // 페이지당 개수 (기본값: 10)
	private String sortBy = "createdAt";       // 정렬 기준
	private String direction = "desc";     // 정렬 순서 (asc, desc)
	private Integer rating;
}
