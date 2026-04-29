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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	// CS 관리자 주문 생성
	@PostMapping("/admin/orders")
	public ResponseEntity<OrderCreateResponse> createByAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@Valid @RequestBody OrderCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByAdmin(request, adminInfo.getId()));
	}

	// 고객 주문 생성
	@PostMapping("/customer/orders")
	public ResponseEntity<OrderCreateResponse> createByCustomer(
		@Valid @RequestBody OrderCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(orderService.createByCustomer(request, request.getCustomerId()));
	}

	// 주문 전체 조회
	@GetMapping("/orders")
	public ResponseEntity<Page<OrderGetResponse>> getOrders(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@ModelAttribute OrderGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getAll(adminInfo.getId(), request));
	}

	// 주문 상세 조회
	@GetMapping("/orders/{orderId}")
	public ResponseEntity<OrderGetResponse> getOrder(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getOne(adminInfo.getId(), orderId));
	}

	// 주문 상태 수정
	@PatchMapping("/orders/{orderId}/status")
	public ResponseEntity<OrderStatusUpdateResponse> updateStatus(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId,
		@Valid @RequestBody OrderStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(orderService.updateStatus(adminInfo.getId(), orderId, request));
	}

	// 주문 취소
	@PatchMapping("/orders/{orderId}/cancel")
	public ResponseEntity<Void> cancelOrder(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long orderId,
		@Valid @RequestBody OrderCancelRequest request
	) {
		orderService.cancel(adminInfo.getId(), orderId, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
