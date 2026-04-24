package com.sparta.backoffice.admin.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminRole {
    SUPER_ADMIN("슈퍼 관리자"),
    OPERATION_ADMIN("운영 관리자"),
    CS_ADMIN("CS 관리자");

    private final String description;
}
