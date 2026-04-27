package com.sparta.backoffice.customer.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.customer.dto.CustomerGetOneResponse;
import com.sparta.backoffice.customer.dto.CustomerGetRequest;
import com.sparta.backoffice.customer.dto.CustomerGetResponse;
import com.sparta.backoffice.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping
	public ResponseEntity<Page<CustomerGetResponse>> getAll(
		@ModelAttribute CustomerGetRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.getAll(request));
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerGetOneResponse> getOne(
		@PathVariable Long customerId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.getOne(customerId));
	}

}
