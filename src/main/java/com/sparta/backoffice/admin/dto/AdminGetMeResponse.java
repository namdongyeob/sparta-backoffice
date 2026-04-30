package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminGetMeResponse {
	private final String name;
	private final String email;
	private final String phoneNumber;

	public static AdminGetMeResponse from(Admin admin) {
		return new AdminGetMeResponse(
			admin.getName(),
			admin.getEmail(),
			admin.getPhoneNumber()
		);
	}
}
