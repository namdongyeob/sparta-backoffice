package com.sparta.backoffice.order.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.repository.CustomerRepository;
import com.sparta.backoffice.order.dto.OrderCancelRequest;
import com.sparta.backoffice.order.dto.OrderCreateRequest;
import com.sparta.backoffice.order.dto.OrderCreateResponse;
import com.sparta.backoffice.order.dto.OrderGetAllRequest;
import com.sparta.backoffice.order.dto.OrderGetResponse;
import com.sparta.backoffice.order.dto.OrderStatusUpdateRequest;
import com.sparta.backoffice.order.dto.OrderStatusUpdateResponse;
import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.repository.OrderRepository;
import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductStatus;
import com.sparta.backoffice.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final AdminRepository adminRepository;

	// CS 관리자 주문 생성
	@Transactional
	public OrderCreateResponse createByAdmin(OrderCreateRequest request, Long adminId) {
		Customer customer = findCustomer(request.getCustomerId());
		Product product = findProduct(request.getProductId());
		Admin admin = findAdmin(adminId);

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new IllegalArgumentException("단종된 상품은 주문할 수 없습니다.");
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new IllegalArgumentException("품절된 상품은 주문할 수 없습니다.");
		}

		// 재고 검증
		if (product.getStock() < request.getQuantity()) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}

		product.updateStock(product.getStock() - request.getQuantity());

		Order order = Order.createByAdmin(
			generateOrderNumber(),
			request.getQuantity(),
			customer,
			product,
			admin
		);

		return OrderCreateResponse.from(orderRepository.save(order));
	}

	// 고객 주문 생성
	@Transactional
	public OrderCreateResponse createByCustomer(OrderCreateRequest request, Long customerId) {
		System.out.println("customer = " + request.getCustomerId());
		System.out.println("request = " + request.getProductId());
		Customer customer = findCustomer(customerId);
		Product product = findProduct(request.getProductId());

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new IllegalArgumentException("단종된 상품은 주문할 수 없습니다.");
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new IllegalArgumentException("품절된 상품은 주문할 수 없습니다.");
		}

		// 재고 검증
		if (product.getStock() < request.getQuantity()) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}

		product.updateStock(product.getStock() - request.getQuantity());

		Order order = Order.createByCustomer(
			generateOrderNumber(),
			request.getQuantity(),
			customer,
			product
		);

		return OrderCreateResponse.from(orderRepository.save(order));
	}

	// 주문 전체 조회
	@Transactional(readOnly = true)
	public Page<OrderGetResponse> getAll(OrderGetAllRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();

		// 최신순 정렬 기본값
		Sort.Direction direction = "asc".equalsIgnoreCase(request.getDirection())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1,
			request.getSize(),
			Sort.by(direction, sortBy)
		);

		return orderRepository.searchOrders(
			request.getKeyword(),
			request.getStatus(),
			pageable
		).map(OrderGetResponse::from);
	}

	// 주문 상세 조회
	@Transactional(readOnly = true)
	public OrderGetResponse getOne(Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 주문입니다.")
		);
		return OrderGetResponse.from(order);
	}

	// 주문 상태 수정
	@Transactional
	public OrderStatusUpdateResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 주문입니다.")
		);
		order.updateStatus(request.getStatus());

		return OrderStatusUpdateResponse.from(order);
	}

	// 주문 취소
	@Transactional
	public void cancle(Long orderId, OrderCancelRequest request) {
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 주문입니다")
		);
		order.cancel(request.getCancelReason());

		Product product = order.getProduct();
		product.updateStock(order.getQuantity());
	}

	// 고객 검증
	private Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 고객입니다."));
	}

	// 상품 검증
	private Product findProduct(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
	}

	// 관리자 검증
	private Admin findAdmin(Long adminId) {
		return adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 관리자입니다."));
	}

	// 주문번호 생성
	private String generateOrderNumber() {
		LocalDate today = LocalDate.now();
		String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		long count = orderRepository.countByCreatedAtBetween(
			today.atStartOfDay(),
			today.atTime(LocalTime.MAX)
		);

		return date + "-" + String.format("%03d", count + 1);
	}
}
