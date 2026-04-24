package com.sparta.backoffice.product.repository;

import com.sparta.backoffice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
