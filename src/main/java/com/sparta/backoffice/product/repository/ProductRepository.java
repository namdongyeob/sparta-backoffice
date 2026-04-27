package com.sparta.backoffice.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.enums.ProductCategory;
import com.sparta.backoffice.product.enums.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("SELECT p FROM Product p JOIN FETCH p.admin WHERE "
		+ "(:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:category IS NULL OR p.category = :category) "
		+ "AND (:status IS NULL OR p.status = :status)")
	Page<Product> searchProducts(
		@Param("keyword") String keyword,
		@Param("category") ProductCategory category,
		@Param("status") ProductStatus status,
		Pageable pageable
	);
}