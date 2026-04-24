package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AdminSignupRequestDto {
	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String name;
	@NotBlank(message = "이메일은 필수 입력 항목입니다.")
	@Email(message = "올바른 이메일 형식이여야 합니다.")
	private String email;
	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이여야 합니다.")
	private String password;
	@NotBlank(message = "전화번호는 필수 입력 항목입니다.")
	@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX여야 합니다.")
	private String phoneNumber;
	@NotNull(message = "역활 선택은 필수 입니다.")
	private AdminRole role;

	public Admin toEntity(String encodedPassword) {
		return new Admin(
			this.name,
			this.email,
			encodedPassword,
			this.phoneNumber,
			this.role
		);
	}
}
