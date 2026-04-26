package com.sparta.backoffice.customer.dto;

import com.sparta.backoffice.customer.enums.CustomerStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerGetRequest {
	private String keyword;
	private int page = 1;
	private int size = 10;
	private String sortBy;  // 기본값 가입일
	private String sortOrder;
	private CustomerStatus status;

}
