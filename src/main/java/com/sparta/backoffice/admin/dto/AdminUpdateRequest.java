package com.sparta.backoffice.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUpdateRequest {
	private String name;

	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String email;

	@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX여야 합니다.")
	private String phoneNumber;
}
