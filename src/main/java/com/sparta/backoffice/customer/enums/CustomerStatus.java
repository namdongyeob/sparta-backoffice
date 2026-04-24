package com.sparta.backoffice.customer.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerStatus {
    ACTIVE("활성"),

    INACTIVE("비활성"),

    SUSPENDED("정지");

    private final String description;
}
