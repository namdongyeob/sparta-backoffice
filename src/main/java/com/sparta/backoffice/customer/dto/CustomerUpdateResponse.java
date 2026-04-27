package com.sparta.backoffice.customer.dto;

import com.sparta.backoffice.customer.entity.Customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerUpdateResponse {
	private final String name;
	private final String email;
	private final String phoneNumber;

	public static CustomerUpdateResponse from(Customer customer) {
		return new CustomerUpdateResponse(
			customer.getName(),
			customer.getEmail(),
			customer.getPhoneNumber()
		);
	}
}
