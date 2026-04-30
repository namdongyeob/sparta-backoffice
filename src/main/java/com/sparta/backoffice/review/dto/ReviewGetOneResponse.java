package com.sparta.backoffice.review.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.review.entity.Review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewGetOneResponse {
	private final String productName;    // 상품명
	private final String customerName;   // 고객명
	private final String customerEmail;  // 고객 이메일
	private final LocalDateTime createdAt; // 작성일
	private final int rating;            // 평점
	private final String content;        // 리뷰 내용

	public static ReviewGetOneResponse from(Review review) {
		return new ReviewGetOneResponse(
			review.getProduct().getName(),
			review.getCustomer().getName(),
			review.getCustomer().getEmail(),
			review.getCreatedAt(),
			review.getRating(),
			review.getContent()
		);
	}
}
