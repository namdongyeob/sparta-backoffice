package com.sparta.backoffice.customer.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerGetOneResponse {
	private final String name;
	private final String email;
	private final String phoneNumber;
	private final CustomerStatus status;
	private final LocalDateTime createdAt;

	public static CustomerGetOneResponse from(Customer customer) {
		return new CustomerGetOneResponse(
			customer.getName(),
			customer.getEmail(),
			customer.getPhoneNumber(),
			customer.getStatus(),
			customer.getCreatedAt()
		);
	}
}
