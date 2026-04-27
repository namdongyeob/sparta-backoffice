package com.sparta.backoffice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.backoffice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}