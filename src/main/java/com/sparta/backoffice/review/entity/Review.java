package com.sparta.backoffice.review.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.order.entity.Order;
import com.sparta.backoffice.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 리뷰 정보를 나타내는 엔티티 클래스.
 * 데이터베이스의 'reviews' 테이블과 매핑됩니다.
 * 소프트 삭제(Soft Delete)가 적용되어 있습니다.
 */
@SQLDelete(sql = "UPDATE reviews SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
	/**
	 * 리뷰의 고유 식별자 (기본 키)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 리뷰 평점 (예: 1~5)
	 */
	@Column(nullable = false)
	private int rating;

	/**
	 * 리뷰 내용
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * 리뷰를 작성한 고객
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/**
	 * 리뷰가 작성된 대상 상품
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	/**
	 * 리뷰와 연관된 주문 내역
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
}
