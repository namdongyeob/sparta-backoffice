package com.sparta.backoffice.review.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.review.entity.Review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewGetAllResponse {
	private final Long id;
	private final String orderNumber;       // 주문번호
	private final String customerName;      // 고객명
	private final String productName;       // 상품명
	private final int rating;               // 평점
	private final String content;          // 리뷰 내용
	private final LocalDateTime createdAt;  // 작성일

	public static ReviewGetAllResponse from(Review review) {
		return new ReviewGetAllResponse(
			review.getId(),
			review.getOrder().getOrderNumber(),
			review.getCustomer().getName(),
			review.getProduct().getName(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}
}
