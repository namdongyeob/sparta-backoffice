package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminRoleUpdateResponse {
	private final Long id;
	private final String name;
	private final AdminRole role;

	public static AdminRoleUpdateResponse from(Admin admin) {
		return new AdminRoleUpdateResponse(
			admin.getId(),
			admin.getName(),
			admin.getRole()
		);
	}
}
