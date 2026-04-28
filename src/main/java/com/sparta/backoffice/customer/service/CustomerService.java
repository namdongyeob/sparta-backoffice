package com.sparta.backoffice.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.customer.dto.CustomerGetOneResponse;
import com.sparta.backoffice.customer.dto.CustomerGetRequest;
import com.sparta.backoffice.customer.dto.CustomerGetResponse;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateResponse;
import com.sparta.backoffice.customer.dto.CustomerUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerUpdateResponse;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

	// 고객 전체 조회
	@Transactional(readOnly = true)
	public Page<CustomerGetResponse> getAll(CustomerGetRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();
		Sort.Direction sortOrder = "asc".equalsIgnoreCase(request.getSortOrder())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1,
			request.getSize(),
			Sort.by(sortOrder, sortBy)
		);

		return customerRepository.searchCustomer(
			request.getKeyword(),
			request.getStatus(),
			pageable
		).map(CustomerGetResponse::from);

	}

	// 고객 상세 조회
	@Transactional(readOnly = true)
	public CustomerGetOneResponse getOne(Long customerId) {
		Customer customer = findCustomer(customerId);

		return CustomerGetOneResponse.from(customer);
	}

	// 고객 수정
	@Transactional
	public CustomerUpdateResponse update(Long customerId, CustomerUpdateRequest request) {
		Customer customer = findCustomer(customerId);

		if (!customer.getEmail().equals(request.getEmail())) {
			if (customerRepository.existsByEmail(request.getEmail())) {
				throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
			}
		}

		if (!customer.getPhoneNumber().equals(request.getPhoneNumber())) {
			if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
				throw new IllegalArgumentException("이미 존재하는 휴대폰 번호입니다.");
			}
		}
		customer.updateInfo(
			request.getName(),
			request.getEmail(),
			request.getPhoneNumber()
		);

		return CustomerUpdateResponse.from(customer);
	}

	// 고객 상태 변경
	@Transactional
	public CustomerStatusUpdateResponse updateStatus(Long customerId, CustomerStatusUpdateRequest request) {
		Customer customer = findCustomer(customerId);

		customer.updateStatus(
			request.getStatus()
		);

		return CustomerStatusUpdateResponse.from(customer);
	}

	// 고객 삭제
	@Transactional
	public void delete(Long customerId) {
		Customer customer = findCustomer(customerId);

		customerRepository.delete(customer);
	}


	// 고객 검증
	public Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 고객입니다.")
		);
	}

}


