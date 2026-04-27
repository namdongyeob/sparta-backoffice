package com.sparta.backoffice.customer.dto;

import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerStatusUpdateResponse {
	private final Long id;
	private final String name;
	private final CustomerStatus status;

	public static CustomerStatusUpdateResponse from(Customer customer) {
		return new CustomerStatusUpdateResponse(
			customer.getId(),
			customer.getName(),
			customer.getStatus()
		);
	}
}
