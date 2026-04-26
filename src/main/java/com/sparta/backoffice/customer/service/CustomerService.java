package com.sparta.backoffice.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.customer.dto.CustomerGetRequest;
import com.sparta.backoffice.customer.dto.CustomerGetResponse;
import com.sparta.backoffice.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public Page<CustomerGetResponse> getAll(CustomerGetRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();
		Sort.Direction sortOrder = "asc".equalsIgnoreCase(request.getSortOrder())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1,
			request.getLimit(),
			Sort.by(sortOrder, sortBy)
		);

		return customerRepository.searchCustomer(
			request.getKeyword(),
			request.getStatus(),
			pageable
		).map(CustomerGetResponse::from);

	}
}


