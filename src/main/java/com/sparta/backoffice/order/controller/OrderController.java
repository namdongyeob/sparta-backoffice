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

import com.sparta.backoffice.order.dto.OrderCancelRequest;
import com.sparta.backoffice.order.dto.OrderCreateRequest;
import com.sparta.backoffice.order.dto.OrderCreateResponse;
import com.sparta.backoffice.order.dto.OrderGetAllRequest;
import com.sparta.backoffice.order.dto.OrderGetResponse;
import com.sparta.backoffice.order.dto.OrderStatusUpdateRequest;
import com.sparta.backoffice.order.dto.OrderStatusUpdateResponse;
import com.sparta.backoffice.order.service.OrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	// CS 관리자 주문 생성
	@PostMapping("/admin")
	public ResponseEntity<OrderCreateResponse> createOrderByAdmin(
		HttpSession session,
		@Valid @RequestBody OrderCreateRequest request
	) {
		Long adminId = (Long)session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByAdmin(request, adminId));
	}

	// 고객 주문 생성
	@PostMapping("/customer")
	public ResponseEntity<OrderCreateResponse> createOrderByCustomer(
		HttpSession session,
		@Valid @RequestBody OrderCreateRequest request
	) {
		Long customerId = (Long)session.getAttribute("customerId");
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByCustomer(request, customerId));
	}

	// 주문 전체 조회
	@GetMapping
	public ResponseEntity<Page<OrderGetResponse>> getOrders(
		@ModelAttribute OrderGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getAll(request));
	}

	// 주문 상세 조회
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderGetResponse> getOrder(
		@PathVariable Long orderId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getOne(orderId));
	}

	// 주문 상태 수정
	@PatchMapping("/{orderId}/status")
	public ResponseEntity<OrderStatusUpdateResponse> updateStatus(
		@PathVariable Long orderId,
		@Valid @RequestBody OrderStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.updateStatus(orderId, request));
	}

	// 주문 취소
	@PatchMapping("/{orderId}/cancle")
	public ResponseEntity<Void> cancelOrder(
		@PathVariable Long orderId,
		@Valid @RequestBody OrderCancelRequest request
	) {
		orderService.cancel(orderId, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
