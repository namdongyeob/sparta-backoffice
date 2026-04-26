package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.enums.AdminStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminStatusUpdateRequest {
	@NotNull(message = "변경할 상태는 필수 입력 항목입니다.")
	private AdminStatus status;
}
