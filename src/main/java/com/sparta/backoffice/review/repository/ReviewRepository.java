package com.sparta.backoffice.review.repository;

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
}
