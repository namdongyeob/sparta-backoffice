package com.sparta.backoffice.order.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.service.AdminService;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
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

/**
 * 주문 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductService productService;
	private final CustomerService customerService;
	private final AdminService adminService;

	/**
	 * 관리자가 고객을 대신하여 주문을 생성합니다.
	 * 상품 재고를 확인하고 차감한 후 주문을 생성합니다.
	 *
	 * @param request 주문 생성 요청 정보
	 * @param adminId 주문을 생성하는 관리자 ID
	 * @return 생성된 주문 정보
	 * @throws CustomException 상품이 단종되었거나 품절된 경우
	 */
	@Transactional
	public OrderCreateResponse createByAdmin(OrderCreateRequest request, Long adminId) {
		Product product = productService.findProduct(request.getProductId());

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new CustomException(ErrorCode.PRODUCT_DISCONTINUED);
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new CustomException(ErrorCode.PRODUCT_SOLD_OUT);
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

	/**
	 * 고객이 직접 주문을 생성합니다.
	 * 상품 재고를 확인하고 차감한 후 주문을 생성합니다.
	 *
	 * @param request    주문 생성 요청 정보
	 * @param customerId 주문을 생성하는 고객 ID
	 * @return 생성된 주문 정보
	 * @throws CustomException 상품이 단종되었거나 품절된 경우
	 */
	@Transactional
	public OrderCreateResponse createByCustomer(OrderCreateRequest request, Long customerId) {
		Product product = productService.findProduct(request.getProductId());

		// 상품 상태 검증
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new CustomException(ErrorCode.PRODUCT_DISCONTINUED);
		}
		if (product.getStatus() == ProductStatus.SOLD_OUT) {
			throw new CustomException(ErrorCode.PRODUCT_SOLD_OUT);
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

	/**
	 * 주문 목록을 페이징 및 필터링하여 조회합니다.
	 *
	 * @param request 목록 조회 요청 정보 (페이징, 키워드, 상태 필터)
	 * @return 주문 정보 목록 페이지
	 */
	@Transactional(readOnly = true)
	public Page<OrderGetResponse> getAll(OrderGetAllRequest request) {

		return orderRepository.searchOrders(
			request.getKeyword(),
			request.getStatus(),
			request.getPageable()
		).map(OrderGetResponse::from);
	}

	/**
	 * 특정 주문의 상세 정보를 조회합니다.
	 *
	 * @param orderId 조회할 주문의 ID
	 * @return 주문 상세 정보 응답 객체
	 */
	@Transactional(readOnly = true)
	public OrderGetResponse getOne(Long orderId) {
		Order order = findOrder(orderId);

		return OrderGetResponse.from(order);
	}

	/**
	 * 특정 주문의 상태를 수정합니다.
	 *
	 * @param orderId 상태를 수정할 주문의 ID
	 * @param request 새로운 상태 정보
	 * @return 상태가 변경된 주문 정보
	 */
	@Transactional
	public OrderStatusUpdateResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
		Order order = findOrder(orderId);
		order.updateStatus(request.getStatus());

		return OrderStatusUpdateResponse.from(order);
	}

	/**
	 * 특정 주문을 취소하고 상품 재고를 복구합니다.
	 *
	 * @param orderId 취소할 주문의 ID
	 * @param request 취소 사유 정보
	 */
	@Transactional
	public void cancel(Long orderId, OrderCancelRequest request) {
		Order order = findOrder(orderId);
		order.cancel(request.getCancelReason());

		Product product = order.getProduct();

		product.increaseStock(order.getQuantity());
	}

	/**
	 * 주문 ID로 엔티티를 조회합니다.
	 *
	 * @param orderId 조회할 주문의 ID
	 * @return 주문 엔티티
	 * @throws CustomException 주문을 찾을 수 없는 경우
	 */
	public Order findOrder(Long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
	}

	/**
	 * 고유한 주문 번호를 생성합니다.
	 * 형식: YYYYMMDD-UUID12자리
	 *
	 * @return 생성된 주문 번호 문자열
	 */
	private String generateOrderNumber() {
		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

		return date + "-" + uuid;
	}
}