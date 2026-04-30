package com.sparta.backoffice.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.sparta.backoffice.common.constant.SessionConst;
import com.sparta.backoffice.common.dto.AdminInfo;
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
import com.sparta.backoffice.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 상품 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	/**
	 * 새로운 상품을 등록합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   상품 생성 요청 정보
	 * @return 생성된 상품 정보와 함께 상태 코드 201 (Created)
	 */
	@PostMapping
	public ResponseEntity<ProductCreateResponse> create(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@Valid @RequestBody ProductCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(adminInfo.getId(), request));
	}

	/**
	 * 상품 목록을 페이징 및 필터링하여 조회합니다.
	 *
	 * @param request 조회 조건 (페이징, 필터링)
	 * @return 상품 목록 페이지와 함께 상태 코드 200 (OK)
	 */
	@GetMapping
	public ResponseEntity<Page<ProductGetAllResponse>> getProducts(
		@Valid @ModelAttribute ProductGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getAll(request));
	}

	/**
	 * 특정 상품의 상세 정보를 조회합니다.
	 *
	 * @param productId 조회할 상품 ID
	 * @return 상품 상세 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/{productId}")
	public ResponseEntity<ProductGetResponse> getProduct(
		@PathVariable Long productId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getOne(productId));
	}

	/**
	 * 특정 상품의 정보를 수정합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param productId 수정할 상품 ID
	 * @param request   수정할 상품 정보
	 * @return 수정된 상품 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{productId}")
	public ResponseEntity<ProductUpdateResponse> update(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long productId,
		@Valid @RequestBody ProductUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.update(adminInfo.getId(), productId, request));
	}

	/**
	 * 특정 상품의 재고를 수정합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param productId 재고를 수정할 상품 ID
	 * @param request   수정할 재고 정보
	 * @return 수정된 상품 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{productId}/stock")
	public ResponseEntity<ProductUpdateStockResponse> updateStock(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long productId,
		@Valid @RequestBody ProductUpdateStockRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.updateStock(adminInfo.getId(), productId, request));
	}

	/**
	 * 특정 상품의 상태를 수정합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param productId 상태를 수정할 상품 ID
	 * @param request   새로운 상태 정보
	 * @return 수정된 상품 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{productId}/status")
	public ResponseEntity<ProductUpdateStatusResponse> updateStatus(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long productId,
		@Valid @RequestBody ProductUpdateStatusRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.updateStatus(adminInfo.getId(), productId, request));
	}

	/**
	 * 특정 상품을 삭제합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param productId 삭제할 상품 ID
	 * @return 상태 코드 204 (No Content)
	 */
	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> delete(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long productId
	) {
		productService.delete(adminInfo.getId(), productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}