package com.sparta.backoffice.common.dto;

import com.sparta.backoffice.admin.enums.AdminRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminInfo {
	private final Long id;
	private final String email;
	private final AdminRole adminRole;
}
