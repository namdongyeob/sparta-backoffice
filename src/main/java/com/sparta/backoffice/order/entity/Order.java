package com.sparta.backoffice.order.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
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

/**
 * 주문 정보를 나타내는 엔티티 클래스.
 * 데이터베이스의 'orders' 테이블과 매핑됩니다.
 * 소프트 삭제(Soft Delete)가 적용되어 있습니다.
 */
@Getter
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
	/**
	 * 주문의 고유 식별자 (기본 키)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 주문 번호 (유일값)
	 */
	@Column(nullable = false, unique = true, length = 25)
	private String orderNumber;

	/**
	 * 주문 수량
	 */
	@Column(nullable = false)
	private int quantity;

	/**
	 * 주문 당시의 상품 단가
	 */
	@Column(nullable = false)
	private int orderPrice;

	/**
	 * 총 주문 금액 (단가 * 수량)
	 */
	@Column(nullable = false)
	private int totalPrice;

	/**
	 * 주문의 현재 상태 (예: PREPARING, SHIPPING, DELIVERED, CANCELLED)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	/**
	 * 주문 취소 사유
	 */
	private String cancelReason;

	/**
	 * 주문을 한 고객
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	/**
	 * 주문된 상품
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	/**
	 * CS 관리자가 생성한 경우, 해당 관리자 정보 (nullable)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;

	/**
	 * 주문 엔티티를 생성하는 생성자.
	 *
	 * @param orderNumber 주문 번호
	 * @param quantity    수량
	 * @param orderPrice  주문 단가
	 * @param totalPrice  총 금액
	 * @param status      주문 상태
	 * @param customer    고객
	 * @param product     상품
	 * @param admin       관리자 (nullable)
	 */
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

	/**
	 * 관리자가 주문을 생성하는 정적 팩토리 메서드.
	 * 초기 상태는 PREPARING(상품 준비중)으로 설정됩니다.
	 *
	 * @param orderNumber 주문 번호
	 * @param quantity    수량
	 * @param customer    고객
	 * @param product     상품
	 * @param admin       주문을 생성한 관리자
	 * @return 생성된 Order 객체
	 */
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

	/**
	 * 고객이 주문을 생성하는 정적 팩토리 메서드.
	 * 초기 상태는 PREPARING(상품 준비중)으로 설정됩니다.
	 *
	 * @param orderNumber 주문 번호
	 * @param quantity    수량
	 * @param customer    주문을 생성한 고객
	 * @param product     상품
	 * @return 생성된 Order 객체
	 */
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

	/**
	 * 주문 상태를 다음 단계로 변경합니다. (준비중 → 배송중 → 배송완료)
	 * 유효하지 않은 상태 변경 시 예외를 발생시킵니다.
	 *
	 * @param status 변경할 새로운 상태
	 * @throws CustomException 이미 취소/배송완료되었거나, 유효하지 않은 상태 변경 순서일 경우
	 */
	public void updateStatus(OrderStatus status) {
		if (this.status == OrderStatus.CANCELLED) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELED);
		}
		if (this.status == OrderStatus.DELIVERED) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_DELIVERED);
		}
		if (this.status == OrderStatus.PREPARING && status == OrderStatus.SHIPPING) {
			this.status = status;
			return;
		}
		if (this.status == OrderStatus.SHIPPING && status == OrderStatus.DELIVERED) {
			this.status = status;
			return;
		}
		throw new CustomException(ErrorCode.ORDER_INVALID_STATUS);
	}

	/**
	 * 주문을 취소합니다.
	 * '상품 준비중' 상태에서만 취소가 가능합니다.
	 *
	 * @param cancelReason 주문 취소 사유
	 * @throws CustomException 취소가 허용되지 않는 상태일 경우
	 */
	public void cancel(String cancelReason) {
		if (this.status != OrderStatus.PREPARING) {
			throw new CustomException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
		}

		this.status = OrderStatus.CANCELLED;
		this.cancelReason = cancelReason;
	}
}