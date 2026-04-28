package com.sparta.backoffice.admin.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminSignupResponse {
	private final Long id;
	private final String name;
	private final String email;
	private final AdminRole role;
	private final AdminStatus status;
	private final LocalDateTime createdAt;

	public static AdminSignupResponse from(Admin admin) {
		return new AdminSignupResponse(
			admin.getId(),
			admin.getName(),
			admin.getEmail(),
			admin.getRole(),
			admin.getStatus(),
			admin.getCreatedAt()
		);
	}
}
