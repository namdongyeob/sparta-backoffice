package com.sparta.backoffice.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CustomerUpdateRequest {

	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String name;

	@Email(message = "이메일은 필수 입력 항목입니다.")
	private String email;

	@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX여야 합니다.")
	private String phoneNumber;
}
