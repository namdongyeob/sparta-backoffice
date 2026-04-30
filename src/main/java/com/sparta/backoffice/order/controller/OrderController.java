package com.sparta.backoffice.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.sparta.backoffice.order.dto.OrderCancelRequest;
import com.sparta.backoffice.order.dto.OrderCreateRequest;
import com.sparta.backoffice.order.dto.OrderCreateResponse;
import com.sparta.backoffice.order.dto.OrderGetAllRequest;
import com.sparta.backoffice.order.dto.OrderGetResponse;
import com.sparta.backoffice.order.dto.OrderStatusUpdateRequest;
import com.sparta.backoffice.order.dto.OrderStatusUpdateResponse;
import com.sparta.backoffice.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 주문 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	/**
	 * 관리자가 고객을 대신하여 주문을 생성합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   주문 생성 요청 정보
	 * @return 생성된 주문 정보와 함께 상태 코드 201 (Created)
	 */
	@PostMapping("/admin/orders")
	public ResponseEntity<OrderCreateResponse> createByAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@Valid @RequestBody OrderCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByAdmin(request, adminInfo.getId()));
	}

	/**
	 * 고객이 직접 주문을 생성합니다.
	 *
	 * @param request 주문 생성 요청 정보 (customerId 포함)
	 * @return 생성된 주문 정보와 함께 상태 코드 201 (Created)
	 */
	@PostMapping("/customer/orders")
	public ResponseEntity<OrderCreateResponse> createByCustomer(
		@Valid @RequestBody OrderCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(orderService.createByCustomer(request, request.getCustomerId()));
	}

	/**
	 * 주문 목록을 페이징 및 필터링하여 조회합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   조회 조건 (페이징, 필터링)
	 * @return 주문 목록 페이지와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/orders")
	public ResponseEntity<Page<OrderGetResponse>> getOrders(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@ModelAttribute OrderGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getAll(request));
	}

	/**
	 * 특정 주문의 상세 정보를 조회합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param orderId   조회할 주문 ID
	 * @return 주문 상세 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/orders/{orderId}")
	public ResponseEntity<OrderGetResponse> getOrder(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getOne(orderId));
	}

	/**
	 * 특정 주문의 상태를 수정합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param orderId   상태를 수정할 주문 ID
	 * @param request   새로운 상태 정보
	 * @return 수정된 주문 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/orders/{orderId}/status")
	public ResponseEntity<OrderStatusUpdateResponse> updateStatus(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId,
		@Valid @RequestBody OrderStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(orderService.updateStatus(orderId, request));
	}

	/**
	 * 특정 주문을 취소합니다. (관리자 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param orderId   취소할 주문 ID
	 * @param request   취소 사유
	 * @return 상태 코드 204 (No Content)
	 */
	@PatchMapping("/orders/{orderId}/cancel")
	public ResponseEntity<Void> cancelOrder(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId,
		@Valid @RequestBody OrderCancelRequest request
	) {
		orderService.cancel(orderId, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}