package com.sparta.backoffice.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.backoffice.product.dto.ProductCreateRequest;
import com.sparta.backoffice.product.dto.ProductCreateResponse;
import com.sparta.backoffice.product.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<ProductCreateResponse> create(
		HttpSession session,
		@Valid @RequestBody ProductCreateRequest request
	) {
		Long adminId = (Long) session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(adminId, request));
	}
}