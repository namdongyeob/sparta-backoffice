package com.sparta.backoffice.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.backoffice.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	@EntityGraph(attributePaths = {"customer", "product", "order"})
	@Query("SELECT r FROM Review r " +
		"WHERE (:keyword IS NULL OR r.customer.name LIKE CONCAT('%', :keyword, '%') " +
		"OR r.product.name LIKE CONCAT('%', :keyword, '%')) " +
		"AND (:rating IS NULL OR r.rating = :rating)")
	Page<Review> findAllReviews(
		@Param("keyword") String keyword,
		@Param("rating") Integer rating,
		Pageable pageable
	);
	// 특정 상품의 모든 리뷰
	@EntityGraph(attributePaths = {"customer"})
	@Query("SELECT r FROM Review r WHERE r.product.id = :productId")
	List<Review> findAllByProductId(@Param("productId") Long productId);
	// 최신 리뷰 3개
	@EntityGraph(attributePaths = {"customer"})
	@Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC LIMIT 3")
	List<Review> findTop3ByProductId(@Param("productId") Long productId);
}
