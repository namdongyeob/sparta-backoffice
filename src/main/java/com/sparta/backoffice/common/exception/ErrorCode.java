package com.sparta.backoffice.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 관리자 관련
	ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
	ADMIN_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
	ADMIN_INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 올바르지 않습니다."),
	ADMIN_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
	ADMIN_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 달라야 합니다."),

	ADMIN_PENDING(HttpStatus.FORBIDDEN, "승인 대기 중인 계정입니다."),
	ADMIN_REJECTED(HttpStatus.FORBIDDEN, "거부된 계정입니다."),
	ADMIN_SUSPENDED(HttpStatus.FORBIDDEN, "정지된 계정입니다."),
	ADMIN_INACTIVE(HttpStatus.FORBIDDEN, "비활성화된 계정입니다."),
	ADMIN_UNAUTHORIZED(HttpStatus.FORBIDDEN, "슈퍼 관리자만 접근 가능합니다."),
	ADMIN_INVALID_STATUS(HttpStatus.BAD_REQUEST, "승인 대기 상태의 관리자만 승인 또는 거절할 수 있습니다."),

	// 상품 관련
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
	PRODUCT_INVALID_NAME(HttpStatus.BAD_REQUEST, "상품명은 비어있을 수 없습니다."),
	PRODUCT_INVALID_PRICE(HttpStatus.BAD_REQUEST, "가격은 1원 이상이어야 합니다."),
	PRODUCT_INVALID_STOCK(HttpStatus.BAD_REQUEST, "재고는 0 이상이어야 합니다."),
	PRODUCT_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다."),

	PRODUCT_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
	PRODUCT_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "단종 상품은 상태를 변경할 수 없습니다."),
	PRODUCT_DISCONTINUED(HttpStatus.BAD_REQUEST, "단종 상품은 주문할 수 없습니다."),
	PRODUCT_SOLD_OUT(HttpStatus.BAD_REQUEST, "품절 상품은 주문할 수 없습니다."),

	// 주문 관련
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
	ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "취소된 주문은 변경 불가합니다."),
	ORDER_ALREADY_DELIVERED(HttpStatus.BAD_REQUEST, "배송완료 주문은 변경 불가합니다."),
	INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 상태 변경입니다."),
	CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "준비중 상태만 취소 가능합니다."),

	// 고객 관련
	CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 고객입니다."),
	CUSTOMER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
	CUSTOMER_PHONE_NUMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 휴대폰 번호입니다."),

	// 리뷰 관련
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
	REVIEW_UNAUTHORIZED(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");

	private final HttpStatus status;
	private final String message;
}