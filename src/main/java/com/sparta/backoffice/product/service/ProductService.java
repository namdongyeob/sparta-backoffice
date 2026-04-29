package com.sparta.backoffice.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.product.dto.ProductCreateRequest;
import com.sparta.backoffice.product.dto.ProductCreateResponse;
import com.sparta.backoffice.product.dto.ProductGetAllRequest;
import com.sparta.backoffice.product.dto.ProductGetAllResponse;
import com.sparta.backoffice.product.dto.ProductGetResponse;
import com.sparta.backoffice.product.dto.ProductUpdateRequest;
import com.sparta.backoffice.product.dto.ProductUpdateResponse;
import com.sparta.backoffice.product.dto.ProductUpdateStatusRequest;
import com.sparta.backoffice.product.dto.ProductUpdateStatusResponse;
import com.sparta.backoffice.product.dto.ProductUpdateStockRequest;
import com.sparta.backoffice.product.dto.ProductUpdateStockResponse;
import com.sparta.backoffice.product.entity.Product;
import com.sparta.backoffice.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final AdminRepository adminRepository;

	@Transactional
	public ProductCreateResponse create(Long adminId, ProductCreateRequest request) {

		Admin admin = adminRepository.findById(adminId).orElseThrow(
			() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

		Product product = request.toEntity(admin);

		Product savedProduct = productRepository.save(product);

		return ProductCreateResponse.from(savedProduct);
	}

	@Transactional(readOnly = true)
	public Page<ProductGetAllResponse> getAll(ProductGetAllRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();

		List<String> allowedSort = List.of("price", "stock", "createdAt");
		if (!allowedSort.contains(sortBy)) {
			sortBy = "createdAt";
		}

		Sort.Direction direction = "asc".equalsIgnoreCase(request.getDirection())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1, request.getSize(), Sort.by(direction, sortBy)
		);

		String keyword = request.getKeyword();
		if (keyword != null && keyword.isBlank()) {
			keyword = null;
		}

		return productRepository.searchProducts(
			keyword,
			request.getCategory(),
			request.getStatus(),
			pageable
		).map(ProductGetAllResponse::from);
	}

	@Transactional(readOnly = true)
	public ProductGetResponse getOne(Long productId) {
		Product product = findProduct(productId);

		return ProductGetResponse.from(product);
	}

	@Transactional
	public ProductUpdateResponse update(Long adminId, Long productId, ProductUpdateRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateInfo(
			request.getName(),
			request.getCategory(),
			request.getPrice()
		);

		return ProductUpdateResponse.from(product);
	}

	@Transactional
	public ProductUpdateStockResponse updateStock(Long adminId, Long productId, ProductUpdateStockRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateStock(request.getStock());

		return ProductUpdateStockResponse.from(product);
	}

	@Transactional
	public ProductUpdateStatusResponse updateStatus(Long adminId, Long productId, ProductUpdateStatusRequest request) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		product.updateStatus(request.getStatus());

		return ProductUpdateStatusResponse.from(product);
	}

	@Transactional
	public void delete(Long adminId, Long productId) {

		validateAdmin(adminId);

		Product product = findProduct(productId);

		productRepository.delete(product);
	}

	public Product findProduct(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
	}

	private void validateAdmin(Long adminId) {
		if (!adminRepository.existsById(adminId)) {
			throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
		}
	}
}