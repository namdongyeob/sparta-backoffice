package com.sparta.backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.common.dto.AdminInfo;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
import com.sparta.backoffice.review.dto.ReviewGetAllRequest;
import com.sparta.backoffice.review.dto.ReviewGetAllResponse;
import com.sparta.backoffice.review.dto.ReviewGetOneResponse;
import com.sparta.backoffice.review.entity.Review;
import com.sparta.backoffice.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 리뷰 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	/**
	 * 리뷰 목록을 페이징 및 필터링하여 조회합니다.
	 *
	 * @param request 리뷰 목록 조회 요청 정보 (페이징, 키워드, 평점 필터)
	 * @return 리뷰 정보 목록 페이지
	 */
	@Transactional(readOnly = true)
	public Page<ReviewGetAllResponse> getReviews(ReviewGetAllRequest request) {
		return reviewRepository.findAllReviews(
			request.getKeyword(),
			request.getRating(),
			request.getPageable()
		).map(ReviewGetAllResponse::from);
	}

	/**
	 * 특정 리뷰의 상세 정보를 조회합니다.
	 *
	 * @param id 조회할 리뷰의 ID
	 * @return 리뷰 상세 정보 응답 객체
	 * @throws CustomException 리뷰를 찾을 수 없는 경우
	 */
	@Transactional(readOnly = true)
	public ReviewGetOneResponse getReview(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() ->  new CustomException(ErrorCode.REVIEW_NOT_FOUND));
		return ReviewGetOneResponse.from(review);
	}

	/**
	 * 특정 리뷰를 삭제합니다. (슈퍼/운영 관리자만 가능)
	 *
	 * @param adminInfo 삭제를 요청하는 관리자 정보
	 * @param id        삭제할 리뷰의 ID
	 * @throws IllegalArgumentException CS 관리자에게는 삭제 권한이 없는 경우
	 * @throws CustomException          리뷰를 찾을 수 없는 경우
	 */
	@Transactional
	public void deleteReview(AdminInfo adminInfo, Long id) {
		if (adminInfo.getAdminRole() == AdminRole.CS_ADMIN) {
			throw new CustomException(ErrorCode.REVIEW_UNAUTHORIZED);
		}
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
		reviewRepository.delete(review);
	}
}