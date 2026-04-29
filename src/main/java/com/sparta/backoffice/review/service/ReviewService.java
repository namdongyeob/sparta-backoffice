package com.sparta.backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.common.dto.AdminInfo;
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
		return reviewRepository.findAllReviews(
			request.getKeyword(),
			request.getRating(),
			request.getPageable()
		).map(ReviewGetAllResponse::from);
	}

	// 리뷰 상세 조회
	@Transactional(readOnly = true)
	public ReviewGetOneResponse getReview(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 리뷰입니다."));
		return ReviewGetOneResponse.from(review);
	}

	// 리뷰 삭제 - 슈퍼/운영 관리자만 가능
	@Transactional
	public void deleteReview(AdminInfo adminInfo, Long id) {
		if (adminInfo.getAdminRole() == AdminRole.CS_ADMIN) {
			throw new IllegalArgumentException("삭제 권한이 없습니다.");
		}
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 리뷰입니다."));
		reviewRepository.delete(review);
	}
}