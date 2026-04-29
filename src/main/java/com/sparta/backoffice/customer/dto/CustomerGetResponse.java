package com.sparta.backoffice.customer.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerGetResponse {
	private final Long id;
	private final String name;
	private final String email;
	private final String phoneNumber;
	private final CustomerStatus status;
	private final LocalDateTime createdAt;
	private final long totalOrderCount;
	private final long totalOrderAmount;

	public static CustomerGetResponse from(Customer customer, long totalOrderCount, long totalOrderAmount) {
		return new CustomerGetResponse(
			customer.getId(),
			customer.getName(),
			customer.getEmail(),
			customer.getPhoneNumber(),
			customer.getStatus(),
			customer.getCreatedAt(),
			totalOrderCount,
			totalOrderAmount
		);
	}

}


