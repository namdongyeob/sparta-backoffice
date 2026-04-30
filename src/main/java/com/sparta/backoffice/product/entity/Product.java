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

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE products SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
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

	private void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_NAME);
		}
	}

	private void validatePrice(int price) {
		if (price < 1) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_PRICE);
		}
	}

	private void validateStock(int stock) {
		if (stock < 0) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_STOCK);
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

	private ProductStatus determineStatus(int stock) {
		return stock == 0 ? ProductStatus.SOLD_OUT : ProductStatus.ON_SALE;
	}

	public void updateStatus(ProductStatus status) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new CustomException(ErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
		}

		this.status = status;
	}

	public void increaseStock(int quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.PRODUCT_INVALID_QUANTITY);
		}

		this.stock += quantity;

		if (this.status == ProductStatus.SOLD_OUT) {
			this.status = ProductStatus.ON_SALE;
		}
	}

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