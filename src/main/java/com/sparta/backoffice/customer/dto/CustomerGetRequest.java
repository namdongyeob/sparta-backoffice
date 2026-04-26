package com.sparta.backoffice.customer.dto;

import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerGetRequest {
	private String keyword;
	private int page = 1;
	private int limit = 10;
	private String sortBy;  // 기본값 가입일
	private String sortOrder;
	private CustomerStatus status;

}
