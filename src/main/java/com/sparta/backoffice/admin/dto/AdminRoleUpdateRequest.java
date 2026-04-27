package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.enums.AdminRole;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminRoleUpdateRequest {
	@NotNull(message = "변경할 역할은 필수 입력 항목입니다.")
	private AdminRole role;
}
