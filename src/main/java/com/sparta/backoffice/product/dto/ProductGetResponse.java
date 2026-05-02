package com.sparta.backoffice.product.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductGetResponse {

	private final String name;
	private final ProductCategory category;
	private final int price;
	private final int stock;
	private final ProductStatus status;
	private final LocalDateTime createdAt;
	private final String adminName;
	private final String adminEmail;
	private final double averageRating;
	private final long totalReviewCount;
	private final Map<Integer, Long> ratingCounts;
	private final List<ReviewSummaryResponse> recentReviews;

	public static ProductGetResponse from(
		Product product,
		double averageRating,
		long totalReviewCount,
		Map<Integer, Long> ratingCounts,
		List<ReviewSummaryResponse> recentReviews) {
		return new ProductGetResponse(
			product.getName(),
			product.getCategory(),
			product.getPrice(),
			product.getStock(),
			product.getStatus(),
			product.getCreatedAt(),
			product.getAdmin().getName(),
			product.getAdmin().getEmail(),
			averageRating,
			totalReviewCount,
			ratingCounts,
			recentReviews
		);
	}
}