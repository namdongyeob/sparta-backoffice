package com.sparta.backoffice.product.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
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
			() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

		Product product = request.toEntity(admin);

		Product savedProduct = productRepository.save(product);

		return ProductCreateResponse.from(savedProduct);
	}

	@Transactional(readOnly = true)
	public Page<ProductGetAllResponse> getAll(ProductGetAllRequest request) {
		return productRepository.searchProducts(
			request.getNormalizedKeyword(),
			request.getCategory(),
			request.getStatus(),
			request.toPageable()
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
		return productRepository.findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	private void validateAdmin(Long adminId) {
		if (!adminRepository.existsById(adminId)) {
			throw new CustomException(ErrorCode.ADMIN_NOT_FOUND);
		}
	}
}