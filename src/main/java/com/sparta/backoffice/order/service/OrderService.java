package com.sparta.backoffice.order.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.repository.CustomerRepository;
import com.sparta.backoffice.order.dto.OrderCreateRequest;
import com.sparta.backoffice.order.dto.OrderCreateResponse;
import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.order.repository.OrderRepository;
import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.repository.ProductRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final AdminRepository adminRepository;

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

	@Transactional
	public OrderCreateResponse createByCustomer(@Valid OrderCreateRequest request, Long customerId) {
		Customer customer = findCustomer(request.getCustomerId());
		Product product = findProduct(request.getProductId());

		product.updateStock(request.getQuantity());

		Order order = Order.createByCustomer(
			generateOrderNumber(),
			request.getQuantity(),
			customer,
			product
		);

		Order savedOrder = orderRepository.save(order);

		return OrderCreateResponse.from(savedOrder);
	@Transactional
	public OrderCreateResponse createByCustomer(OrderCreateRequest request, Long customerId) {
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

	private Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 고객입니다."));
	}

	private Product findProduct(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
	}

	private Admin findAdmin(Long adminId) {
		return adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 관리자입니다."));
	}

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
