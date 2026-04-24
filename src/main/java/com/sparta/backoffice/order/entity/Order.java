package com.sparta.backoffice.order.entity;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.order.enums.OrderStatus;
import com.sparta.backoffice.product.entity.Product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String orderNumber;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private int totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(nullable = false)
	private int orderPrice;

	private String cancelReason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin; // CS 주문 등록 관리자 (nullable)

	public Order(String orderNumber, int quantity, int totalPrice,
		Customer customer, Product product, Admin admin, int orderPrice) {
		this.orderNumber = orderNumber;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.status = OrderStatus.PREPARING;
		this.customer = customer;
		this.product = product;
		this.admin = admin;
		this.orderPrice = orderPrice;
	}

	// 주문 상태 수정 (준비중 → 배송중 → 배송완료)
	public void updateStatus(OrderStatus status) {
		this.status = status;
	}

	// 주문 취소
	public void cancel(String cancelReason) {
		this.status = OrderStatus.CANCELLED;
		this.cancelReason = cancelReason;
	}
}
