package com.sparta.backoffice.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.product.dto.ProductCreateRequest;
import com.sparta.backoffice.product.dto.ProductCreateResponse;
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
		if (adminId == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}

		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

		Product product = request.toEntity(admin);

		Product savedProduct = productRepository.save(product);

		return ProductCreateResponse.from(savedProduct);
	}
}