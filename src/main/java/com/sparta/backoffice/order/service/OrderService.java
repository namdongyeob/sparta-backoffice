package com.sparta.backoffice.order.service;

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
	// adminId 세션으로 대체
	public OrderCreateResponse create(@Valid OrderCreateRequest request, Long adminId) {
		Customer customer = customerRepository.findById(request.getCustomerId())
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 고객입니다."));

		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));

		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 관리자입니다."));

		product.updateStock(request.getQuantity());

		Order order = Order.create(
			generateOrderNumber(),
			request.getQuantity(),
			customer,
			product,
			admin
		);

		Order savedOrder = orderRepository.save(order);

		return OrderCreateResponse.from(savedOrder);
	}

	private String generateOrderNumber() {
		return "ORD-" + System.currentTimeMillis();
	}

	private int getOrderPrice(int price, int quantity) {
		return price * quantity;
	}
}
