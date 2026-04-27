package com.sparta.backoffice.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.product.dto.ProductCreateRequest;
import com.sparta.backoffice.product.dto.ProductCreateResponse;
import com.sparta.backoffice.product.dto.ProductGetAllRequest;
import com.sparta.backoffice.product.dto.ProductGetAllResponse;
import com.sparta.backoffice.product.dto.ProductGetResponse;
import com.sparta.backoffice.product.dto.ProductUpdateRequest;
import com.sparta.backoffice.product.dto.ProductUpdateResponse;
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
		Long adminId = (Long)session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(adminId, request));
	}

	@GetMapping
	public ResponseEntity<Page<ProductGetAllResponse>> getProducts(
		@Valid @ModelAttribute ProductGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getAll(request));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductGetResponse> getProduct(
		@PathVariable Long productId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getOne(productId));
	}

	@PatchMapping("/{productId}")
	public ResponseEntity<ProductUpdateResponse> update(
		HttpSession session,
		@PathVariable Long productId,
		@Valid @RequestBody ProductUpdateRequest request
	) {
		Long adminId = (Long)session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.OK).body(productService.update(adminId, productId, request));
	}
}