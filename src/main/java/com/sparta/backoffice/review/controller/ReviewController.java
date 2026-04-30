package com.sparta.backoffice.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.sparta.backoffice.common.constant.SessionConst;
import com.sparta.backoffice.common.dto.AdminInfo;
import com.sparta.backoffice.review.dto.ReviewGetAllRequest;
import com.sparta.backoffice.review.dto.ReviewGetAllResponse;
import com.sparta.backoffice.review.dto.ReviewGetOneResponse;
import com.sparta.backoffice.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

/**
 * 리뷰 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	/**
	 * 리뷰 목록을 페이징 및 필터링하여 조회합니다. (관리자 권한 필요)
	 *
	 * @param request   조회 조건 (페이징, 필터링)
	 * @param adminInfo 로그인한 관리자 정보
	 * @return 리뷰 목록 페이지와 함께 상태 코드 200 (OK)
	 */
	@GetMapping
	public ResponseEntity<Page<ReviewGetAllResponse>> getReviews(
		@ModelAttribute ReviewGetAllRequest request,
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
	) {
		return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviews(request));
	}

	/**
	 * 특정 리뷰의 상세 정보를 조회합니다. (관리자 권한 필요)
	 *
	 * @param id        조회할 리뷰 ID
	 * @param adminInfo 로그인한 관리자 정보
	 * @return 리뷰 상세 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ReviewGetOneResponse> getReview(
		@PathVariable Long id,
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
	) {
		return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReview(id));
	}

	/**
	 * 특정 리뷰를 삭제합니다. (슈퍼/운영 관리자만 가능)
	 *
	 * @param id        삭제할 리뷰 ID
	 * @param adminInfo 로그인한 관리자 정보
	 * @return 상태 코드 204 (No Content)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long id,
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
	) {
		reviewService.deleteReview(adminInfo, id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}