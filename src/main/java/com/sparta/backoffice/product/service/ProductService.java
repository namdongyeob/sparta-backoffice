package com.sparta.backoffice.product.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
import com.sparta.backoffice.product.dto.ProductCreateRequest;
import com.sparta.backoffice.product.dto.ProductCreateResponse;
import com.sparta.backoffice.product.dto.ProductGetAllRequest;
import com.sparta.backoffice.product.dto.ProductGetAllResponse;
import com.sparta.backoffice.product.dto.ProductGetResponse;
import com.sparta.backoffice.product.dto.ProductUpdateRequest;
import com.sparta.backoffice.product.dto.ProductUpdateResponse;
import com.sparta.backoffice.product.dto.ProductUpdateStatusRequest;
import com.sparta.backoffice.product.dto.ProductUpdateStatusResponse;
import com.sparta.backoffice.product.dto.ProductUpdateStockRequest;
import com.sparta.backoffice.product.dto.ProductUpdateStockResponse;
import com.sparta.backoffice.product.dto.ReviewSummaryResponse;
import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.repository.ProductRepository;
import com.sparta.backoffice.review.entity.Review;
import com.sparta.backoffice.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final AdminRepository adminRepository;
	private final ReviewRepository reviewRepository;

	/**
	 * 새로운 상품을 생성합니다.
	 *
	 * @param adminId 상품을 등록하는 관리자의 ID
	 * @param request 상품 생성 요청 정보
	 * @return 생성된 상품 정보
	 * @throws CustomException 관리자를 찾을 수 없는 경우
	 */
	@Transactional
	public ProductCreateResponse create(Long adminId, ProductCreateRequest request) {

		Admin admin = adminRepository.findById(adminId).orElseThrow(
			() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

		Product product = request.toEntity(admin);

		Product savedProduct = productRepository.save(product);

		return ProductCreateResponse.from(savedProduct);
	}

	/**
	 * 상품 목록을 페이징 및 필터링하여 조회합니다.
	 *
	 * @param request 목록 조회 요청 정보 (페이징, 키워드, 카테고리, 상태 필터)
	 * @return 상품 정보 목록 페이지
	 */
	@Transactional(readOnly = true)
	public Page<ProductGetAllResponse> getAll(ProductGetAllRequest request) {
		return productRepository.searchProducts(
			request.getNormalizedKeyword(),
			request.getCategory(),
			request.getStatus(),
			request.toPageable()
		).map(ProductGetAllResponse::from);
	}

	/**
	 * 특정 상품의 상세 정보를 조회합니다.
	 *
	 * @param productId 조회할 상품의 ID
	 * @return 상품 상세 정보 응답 객체
	 */
	@Transactional(readOnly = true)
	public ProductGetResponse getOne(Long productId) {
		Product product = findProduct(productId);
		List<Review> reviews = reviewRepository.findAllByProductId(productId);
		double averageRating = reviews.stream()  // 리뷰 목록을 스트림으로
			.mapToInt(Review::getRating)         // 각 리뷰에서 평점만 뽑기
			.average()                           // 평균 계산
			.orElse(0.0);                  // 리뷰가 없으면 0.0 반환
		long totalReviewCount = reviews.size();
		Map<Integer, Long> ratingCounts = reviews.stream()
			.collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
		List<ReviewSummaryResponse> recentReviews = reviewRepository.findTop3ByProductId(productId)
			.stream()
			.map(ReviewSummaryResponse::from)
			.toList();
		return ProductGetResponse.from(product,averageRating,totalReviewCount,ratingCounts,recentReviews);
	}

	/**
	 * 상품의 기본 정보를 수정합니다.
	 *
	 * @param adminId   수정을 요청하는 관리자 ID
	 * @param productId 수정할 상품의 ID
	 * @param request   수정할 상품 정보
	 * @return 수정된 상품 정보
	 */
	@Transactional
	public ProductUpdateResponse update(Long adminId, Long productId, ProductUpdateRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateInfo(
			request.getName(),
			request.getCategory(),
			request.getPrice()
		);

		return ProductUpdateResponse.from(product);
	}

	/**
	 * 상품의 재고 수량을 수정합니다.
	 *
	 * @param adminId   수정을 요청하는 관리자 ID
	 * @param productId 재고를 수정할 상품의 ID
	 * @param request   수정할 재고 수량 정보
	 * @return 재고가 수정된 상품 정보
	 */
	@Transactional
	public ProductUpdateStockResponse updateStock(Long adminId, Long productId, ProductUpdateStockRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateStock(request.getStock());

		return ProductUpdateStockResponse.from(product);
	}

	/**
	 * 상품의 상태를 수정합니다.
	 *
	 * @param adminId   수정을 요청하는 관리자 ID
	 * @param productId 상태를 수정할 상품의 ID
	 * @param request   새로운 상태 정보
	 * @return 상태가 수정된 상품 정보
	 */
	@Transactional
	public ProductUpdateStatusResponse updateStatus(Long adminId, Long productId, ProductUpdateStatusRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateStatus(request.getStatus());

		return ProductUpdateStatusResponse.from(product);
	}

	/**
	 * 상품을 시스템에서 삭제합니다.
	 *
	 * @param adminId   삭제를 요청하는 관리자 ID
	 * @param productId 삭제할 상품의 ID
	 */
	@Transactional
	public void delete(Long adminId, Long productId) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		productRepository.delete(product);
	}

	/**
	 * 상품 ID로 엔티티를 조회합니다.
	 *
	 * @param productId 조회할 상품의 ID
	 * @return 상품 엔티티
	 * @throws CustomException 상품을 찾을 수 없는 경우
	 */
	public Product findProduct(Long productId) {
		return productRepository.findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	/**
	 * 관리자가 존재하는지 검증합니다.
	 *
	 * @param adminId 검증할 관리자의 ID
	 * @throws CustomException 관리자가 존재하지 않는 경우
	 */
	private void validateAdmin(Long adminId) {
		if (!adminRepository.existsById(adminId)) {
			throw new CustomException(ErrorCode.ADMIN_NOT_FOUND);
		}
	}
}