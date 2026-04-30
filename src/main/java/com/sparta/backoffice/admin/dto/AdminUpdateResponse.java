package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminUpdateResponse {
	private final Long id;
	private final String name;
	private final String email;
	private final String phoneNumber;

	public static AdminUpdateResponse from(Admin admin) {
		return new AdminUpdateResponse(
			admin.getId(),
			admin.getName(),
			admin.getEmail(),
			admin.getPhoneNumber()
		);
	}
}
