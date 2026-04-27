package com.sparta.backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.review.dto.ReviewGetAllRequest;
import com.sparta.backoffice.review.dto.ReviewGetAllResponse;
import com.sparta.backoffice.review.dto.ReviewGetOneResponse;
import com.sparta.backoffice.review.entity.Review;
import com.sparta.backoffice.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// 리뷰 목록 페이징 및 필터 조회
	@Transactional(readOnly = true)
	public Page<ReviewGetAllResponse> getReviews(ReviewGetAllRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();
		Sort.Direction direction = "asc".equalsIgnoreCase(request.getDirection())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1,
			request.getSize(),
			Sort.by(direction, sortBy)
		);

		return reviewRepository.findAllReviews(
			request.getKeyword(),
			request.getRating(),
			pageable
		).map(ReviewGetAllResponse::from);
	}

	// 리뷰 상세 조회
	@Transactional(readOnly = true)
	public ReviewGetOneResponse getReview(Long id) {
		Review review = reviewRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 리뷰입니다."));
		return ReviewGetOneResponse.from(review);

	}

	// 리뷰 삭제 (소프트 딜리트)
	@Transactional
	public void deleteReview(Long id) {
		Review review = reviewRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 리뷰입니다."));

		review.delete();
	}
}
