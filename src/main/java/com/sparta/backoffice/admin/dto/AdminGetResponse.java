package com.sparta.backoffice.admin.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminGetResponse {
	private final Long id;
	private final String name;
	private final String email;
	private final String phoneNumber;
	private final AdminRole role;
	private final AdminStatus status;
	private final LocalDateTime createdAt;
	private final LocalDateTime approvedAt;

	public static AdminGetResponse from(Admin admin) {
		return new AdminGetResponse(
			admin.getId(),
			admin.getName(),
			admin.getEmail(),
			admin.getPhoneNumber(),
			admin.getRole(),
			admin.getStatus(),
			admin.getCreatedAt(),
			admin.getApprovedAt()
		);
	}
}
