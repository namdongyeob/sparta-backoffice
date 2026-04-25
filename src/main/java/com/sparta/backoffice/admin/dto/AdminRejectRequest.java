package com.sparta.backoffice.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminRejectRequest {
	@NotBlank(message = "거부 사유는 필수 입력 항목입니다.")
	private String rejectionReason;
}
