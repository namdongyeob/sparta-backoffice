package com.sparta.backoffice.product.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

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
 * 상품 정보를 나타내는 엔티티 클래스.
 * 데이터베이스의 'products' 테이블과 매핑됩니다.
 * 소프트 삭제(Soft Delete)가 적용되어 있습니다.
 */
@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE products SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Product extends BaseEntity {

	/**
	 * 상품의 고유 식별자 (기본 키)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 상품의 이름
	 */
	@Column(nullable = false, length = 100)
	private String name;

	/**
	 * 상품의 카테고리
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductCategory category;

	/**
	 * 상품의 가격
	 */
	@Column(nullable = false)
	private int price;

	/**
	 * 상품의 재고 수량
	 */
	@Column(nullable = false)
	private int stock;

	/**
	 * 상품의 판매 상태 (예: ON_SALE, SOLD_OUT, DISCONTINUED)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus status;

	/**
	 * 상품을 등록한 관리자
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	/**
	 * 새로운 상품 엔티티를 생성하는 생성자.
	 * 초기 상태는 재고 수량에 따라 ON_SALE 또는 SOLD_OUT으로 결정됩니다.
	 *
	 * @param name     상품명
	 * @param category 카테고리
	 * @param price    가격
	 * @param stock    재고
	 * @param admin    등록한 관리자
	 */
	public Product(String name, ProductCategory category, int price, int stock, Admin admin) {
		validateName(name);
		validatePrice(price);
		validateStock(stock);

		this.name = name;
		this.category = category;
		this.price = price;
		this.stock = stock;
		this.admin = admin;
		this.status = determineStatus(stock);
	}

	/**
	 * 상품의 기본 정보를 수정합니다.
	 * null이 아닌 값만 업데이트됩니다.
	 *
	 * @param name     수정할 상품명
	 * @param category 수정할 카테고리
	 * @param price    수정할 가격
	 */
	public void updateInfo(String name, ProductCategory category, Integer price) {
		if (name != null) {
			this.name = name;
		}

		if (category != null) {
			this.category = category;
		}

		if (price != null) {
			this.price = price;
		}
	}

	/**
	 * 상품명이 유효한지 검증합니다.
	 * @param name 검증할 상품명
	 * @throws CustomException 상품명이 null이거나 비어있는 경우
	 */
	private void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_NAME);
		}
	}

	/**
	 * 가격이 유효한지 검증합니다.
	 * @param price 검증할 가격
	 * @throws CustomException 가격이 1보다 작은 경우
	 */
	private void validatePrice(int price) {
		if (price < 1) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_PRICE);
		}
	}

	/**
	 * 재고 수량이 유효한지 검증합니다.
	 * @param stock 검증할 재고 수량
	 * @throws CustomException 재고가 0보다 작은 경우
	 */
	private void validateStock(int stock) {
		if (stock < 0) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_STOCK);
		}
	}

	/**
	 * 상품의 재고를 수정하고, 재고 상태에 따라 판매 상태를 업데이트합니다.
	 * 단종된 상품의 상태는 변경하지 않습니다.
	 *
	 * @param stock 새로운 재고 수량
	 */
	public void updateStock(int stock) {
		validateStock(stock);

		this.stock = stock;

		// 단종이면 상태 유지
		if (this.status != ProductStatus.DISCONTINUED) {
			this.status = determineStatus(stock);
		}
	}

	/**
	 * 재고 수량에 따라 상품 상태를 결정합니다.
	 * @param stock 현재 재고 수량
	 * @return 재고가 0이면 SOLD_OUT, 아니면 ON_SALE
	 */
	private ProductStatus determineStatus(int stock) {
		return stock == 0 ? ProductStatus.SOLD_OUT : ProductStatus.ON_SALE;
	}

	/**
	 * 상품의 판매 상태를 수정합니다.
	 * 단종된 상품의 상태는 변경할 수 없습니다.
	 *
	 * @param status 변경할 새로운 판매 상태
	 * @throws CustomException 이미 단종된 상품의 상태를 변경하려는 경우
	 */
	public void updateStatus(ProductStatus status) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new CustomException(ErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
		}

		this.status = status;
	}

	/**
	 * 상품의 재고를 증가시킵니다. (예: 주문 취소 시)
	 * 품절 상태였던 경우, 판매중 상태로 변경합니다.
	 *
	 * @param quantity 증가시킬 수량
	 * @throws CustomException 수량이 0 이하인 경우
	 */
	public void increaseStock(int quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_QUANTITY);
		}

		this.stock += quantity;

		if (this.status == ProductStatus.SOLD_OUT) {
			this.status = ProductStatus.ON_SALE;
		}
	}

	/**
	 * 상품의 재고를 감소시킵니다. (예: 주문 발생 시)
	 * 재고가 0이 되면 품절 상태로 변경합니다.
	 *
	 * @param quantity 감소시킬 수량
	 * @throws CustomException 수량이 0 이하, 상품이 단종/품절, 재고가 부족한 경우
	 */
	public void decreaseStock(int quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_QUANTITY);
		}

		if (this.status == ProductStatus.DISCONTINUED) {
			throw new CustomException(ErrorCode.PRODUCT_DISCONTINUED);
		}

		if (this.status == ProductStatus.SOLD_OUT) {
			throw new CustomException(ErrorCode.PRODUCT_SOLD_OUT);
		}

		if (this.stock < quantity) {
			throw new CustomException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
		}

		this.stock -= quantity;

		if (this.stock == 0) {
			this.status = ProductStatus.SOLD_OUT;
		}
	}
}