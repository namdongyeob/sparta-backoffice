package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminStatusUpdateResponse {
	private final Long id;
	private final String name;
	private final AdminStatus status;

	public static AdminStatusUpdateResponse from(Admin admin) {
		return new AdminStatusUpdateResponse(
			admin.getId(),
			admin.getName(),
			admin.getStatus()
		);
	}
}
