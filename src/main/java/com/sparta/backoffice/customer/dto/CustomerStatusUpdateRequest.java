package com.sparta.backoffice.customer.dto;

import com.sparta.backoffice.customer.enums.CustomerStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CustomerStatusUpdateRequest {

	@NotNull(message = "변경할 상태는 필수 입력 항목입니다.")
	private CustomerStatus status;
}
