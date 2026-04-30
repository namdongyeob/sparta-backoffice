package com.sparta.backoffice.order.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.service.AdminService;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.service.CustomerService;
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
import com.sparta.backoffice.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductService productService;
	private final CustomerService customerService;
	private final AdminService adminService;

	// CS 관리자 주문 생성
	@Transactional
	public OrderCreateResponse createByAdmin(OrderCreateRequest request, Long adminId) {
		Product product = productService.findProduct(request.getProductId());

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new IllegalArgumentException("단종된 상품은 주문할 수 없습니다.");
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new IllegalArgumentException("품절된 상품은 주문할 수 없습니다.");
		}

		Customer customer = customerService.findCustomer(request.getCustomerId());
		Admin admin = adminService.findAdmin(adminId);

		product.decreaseStock(request.getQuantity());

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
		Product product = productService.findProduct(request.getProductId());

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new IllegalArgumentException("단종된 상품은 주문할 수 없습니다.");
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new IllegalArgumentException("품절된 상품은 주문할 수 없습니다.");
		}

		Customer customer = customerService.findCustomer(customerId);

		product.decreaseStock(request.getQuantity());

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

		return orderRepository.searchOrders(
			request.getKeyword(),
			request.getStatus(),
			request.getPageable()
		).map(OrderGetResponse::from);
	}

	// 주문 상세 조회
	@Transactional(readOnly = true)
	public OrderGetResponse getOne(Long orderId) {
		Order order = findOrder(orderId);

		return OrderGetResponse.from(order);
	}

	// 주문 상태 수정
	@Transactional
	public OrderStatusUpdateResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
		Order order = findOrder(orderId);
		order.updateStatus(request.getStatus());

		return OrderStatusUpdateResponse.from(order);
	}

	// 주문 취소
	@Transactional
	public void cancel(Long orderId, OrderCancelRequest request) {
		Order order = findOrder(orderId);
		order.cancel(request.getCancelReason());

		Product product = order.getProduct();

		product.increaseStock(order.getQuantity());
	}

	// 주문 검증
	public Order findOrder(Long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
	}

	// 주문번호 생성
	private String generateOrderNumber() {
		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

		return date + "-" + uuid;
	}
}
