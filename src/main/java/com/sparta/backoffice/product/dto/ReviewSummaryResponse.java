package com.sparta.backoffice.product.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.review.entity.Review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewSummaryResponse {
	private final String customerName;     // 고객명
	private final int rating;              // 평점
	private final String content;          // 리뷰 내용
	private final LocalDateTime createdAt; // 작성일

	public static ReviewSummaryResponse from(Review review) {
		return new ReviewSummaryResponse(
			review.getCustomer().getName(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}
}
