package com.sparta.backoffice.order.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.order.enums.OrderStatus;
import com.sparta.backoffice.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 25)
	private String orderNumber;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private int orderPrice;

	@Column(nullable = false)
	private int totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	private String cancelReason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin; // CS 주문 등록 관리자 (nullable)

	public Order(
		String orderNumber, int quantity, int orderPrice, int totalPrice, OrderStatus status, Customer customer,
		Product product, Admin admin
	) {
		this.orderNumber = orderNumber;
		this.quantity = quantity;
		this.orderPrice = orderPrice;
		this.totalPrice = totalPrice;
		this.status = status;
		this.customer = customer;
		this.product = product;
		this.admin = admin;
	}

	public static Order createByAdmin(
		String orderNumber,
		int quantity,
		Customer customer,
		Product product,
		Admin admin
	) {
		int orderPrice = product.getPrice();
		int totalPrice = orderPrice * quantity;

		return new Order(
			orderNumber,
			quantity,
			orderPrice,
			totalPrice,
			OrderStatus.PREPARING,
			customer,
			product,
			admin
		);
	}

	public static Order createByCustomer(
		String orderNumber,
		int quantity,
		Customer customer,
		Product product
	) {
		int orderPrice = product.getPrice();
		int totalPrice = orderPrice * quantity;

		return new Order(
			orderNumber,
			quantity,
			orderPrice,
			totalPrice,
			OrderStatus.PREPARING,
			customer,
			product,
			null
		);
	}

	// 주문 상태 수정 (준비중 → 배송중 → 배송완료)
	public void updateStatus(OrderStatus status) {
		if (this.status == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException("취소된 주문은 변경 불가합니다.");
		}
		if (this.status == OrderStatus.DELIVERED) {
			throw new IllegalArgumentException("배송완료 주문은 변경 불가합니다.");
		}
		if (this.status == OrderStatus.PREPARING && status == OrderStatus.SHIPPING) {
			this.status = status;
			return;
		}
		if (this.status == OrderStatus.SHIPPING && status == OrderStatus.DELIVERED) {
			this.status = status;
			return;
		}
		throw new IllegalArgumentException("잘못된 상태 변경입니다.");
	}

	// 주문 취소
	public void cancel(String cancelReason) {
		if (this.status != OrderStatus.PREPARING) {
			throw new IllegalArgumentException("준비중 상태만 취소 가능합니다.");
		}

		this.status = OrderStatus.CANCELLED;
		this.cancelReason = cancelReason;
	}
}


