package com.sparta.backoffice.admin.dto;

import com.sparta.backoffice.admin.entity.Admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminUpdateMeResponse {
	private final String name;
	private final String email;
	private final String phoneNumber;

	public static AdminUpdateMeResponse from(Admin admin) {
		return new AdminUpdateMeResponse(
			admin.getName(),
			admin.getEmail(),
			admin.getPhoneNumber()
		);
	}
}
