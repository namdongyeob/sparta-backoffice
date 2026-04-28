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

import com.sparta.backoffice.review.dto.ReviewGetAllRequest;
import com.sparta.backoffice.review.dto.ReviewGetAllResponse;
import com.sparta.backoffice.review.dto.ReviewGetOneResponse;
import com.sparta.backoffice.review.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	// 리뷰 목록 페이징 및 필터 조회
	@GetMapping
	public ResponseEntity<Page<ReviewGetAllResponse>> getReviews(
		@ModelAttribute ReviewGetAllRequest request,
		HttpSession session
	) {
		Long adminId = (Long)session.getAttribute("adminId");
		if (adminId == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviews(request));
	}

	// 리뷰 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<ReviewGetOneResponse> getReview(
		@PathVariable Long id,
		HttpSession session
	) {
		Long adminId = (Long)session.getAttribute("adminId");
		if (adminId == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReview(id));
	}

	// 리뷰 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long id,
		HttpSession session) {
		Long adminId = (Long)session.getAttribute("adminId");
		if (adminId == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}
		reviewService.deleteReview(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
