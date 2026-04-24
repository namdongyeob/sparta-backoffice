package com.sparta.backoffice.admin.dto;

import java.time.LocalDateTime;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;

import lombok.Getter;

@Getter
public class AdminResponseDto {
	private final Long id;
	private final String name;
	private final String email;
	private final AdminRole role;
	private final AdminStatus status;
	private final LocalDateTime createdAt;

	private AdminResponseDto(Long id, String name, String email, AdminRole role, AdminStatus status,
		LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.role = role;
		this.status = status;
		this.createdAt = createdAt;
	}

	public static AdminResponseDto from(Admin admin) {
		return new AdminResponseDto(
			admin.getId(),
			admin.getName(),
			admin.getEmail(),
			admin.getRole(),
			admin.getStatus(),
			admin.getCreatedAt()
		);
	}
}
