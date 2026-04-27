package com.sparta.backoffice.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.order.dto.OrderCreateRequest;
import com.sparta.backoffice.order.dto.OrderCreateResponse;
import com.sparta.backoffice.order.service.OrderService;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/admin")
	public ResponseEntity<OrderCreateResponse> createOrderByAdmin(
		HttpSession session,
		@Valid @RequestBody OrderCreateRequest request
	) {
		Long adminId = (Long) session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByAdmin(request, adminId));
	}

	@PostMapping("/customer")
	public ResponseEntity<OrderCreateResponse> createOrderByCustomer(
		HttpSession session,
		@Valid @RequestBody OrderCreateRequest request
	) {
		Long customerId = (Long) session.getAttribute("customerId");
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createByCustomer(request, customerId));
	}
}
