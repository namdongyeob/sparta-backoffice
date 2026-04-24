package com.sparta.backoffice.product.service;

import org.springframework.stereotype.Service;

import com.sparta.backoffice.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
}
