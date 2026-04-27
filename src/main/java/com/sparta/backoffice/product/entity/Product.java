package com.sparta.backoffice.product.entity;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductCategory category;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int stock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

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

	private ProductStatus determineStatus(int stock) {
		return stock == 0 ? ProductStatus.SOLD_OUT : ProductStatus.ON_SALE;
	}

	public void updateInfo(String name, ProductCategory category, Integer price) {
		if (name != null) {
			validateName(name);
			this.name = name;
		}

		if (category != null) {
			this.category = category;
		}

		if (price != null) {
			validatePrice(price);
			this.price = price;
		}
	}

	private void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("상품명은 비어있을 수 없습니다.");
		}
	}

	private void validatePrice(int price) {
		if (price < 0) {
			throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
		}
	}

	private void validateStock(int stock) {
		if (stock < 0) {
			throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
		}
	}

	public void updateStock(int stock) {
		validateStock(stock);

		this.stock = stock;

		// 단종이면 상태 유지
		if (this.status != ProductStatus.DISCONTINUED) {
			this.status = determineStatus(stock);
		}
	}

	public void updateStatus(ProductStatus status) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new IllegalStateException("단종 상품은 상태를 변경할 수 없습니다.");
		}

		this.status = status;
	}
}