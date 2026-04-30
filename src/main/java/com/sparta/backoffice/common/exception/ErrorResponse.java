package com.sparta.backoffice.common.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

/**
 * API 예외 발생 시 클라이언트에게 반환될 응답 형식을 정의하는 클래스입니다.
 */
@Getter
@JsonPropertyOrder({"status", "code", "message"})
public class ErrorResponse {
	/**
	 * HTTP 상태 코드 (예: 400, 404, 500)
	 */
	private final int status;
	/**
	 * 애플리케이션에서 정의한 에러 코드 (예: "ADMIN_NOT_FOUND")
	 */
	private final String code;
	/**
	 * 사용자에게 표시될 에러 메시지
	 */
	private final String message;

	/**
	 * ErrorCode를 사용하여 ErrorResponse 객체를 생성합니다.
	 *
	 * @param errorCode 발생한 에러의 정보를 담고 있는 ErrorCode 열거형 상수
	 */
	public ErrorResponse(ErrorCode errorCode) {
		this.status = errorCode.getStatus().value();
		this.code = errorCode.name();
		this.message = errorCode.getMessage();

	}

	/**
	 * 상태, 코드, 메시지를 직접 지정하여 ErrorResponse 객체를 생성합니다.
	 *
	 * @param status  HTTP 상태 코드
	 * @param code    에러 코드 문자열
	 * @param message 에러 메시지
	 */
	public ErrorResponse(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}