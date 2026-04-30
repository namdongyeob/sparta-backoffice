package com.sparta.backoffice.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 애플리케이션의 비즈니스 로직에서 발생하는 예외를 나타내는 사용자 정의 예외 클래스입니다.
 * {@link ErrorCode}를 포함하여 예외에 대한 구체적인 정보를 제공합니다.
 */
@Getter
public class CustomException extends RuntimeException {

	/**
	 * 발생한 예외의 상세 정보를 담고 있는 ErrorCode
	 */
	private final ErrorCode errorCode;

	/**
	 * ErrorCode를 인자로 받아 CustomException을 생성합니다.
	 *
	 * @param errorCode 발생한 예외에 해당하는 ErrorCode
	 */
	public CustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 예외에 해당하는 HTTP 상태 코드를 반환합니다.
	 *
	 * @return HttpStatus
	 */
	public HttpStatus getStatus() {
		return errorCode.getStatus();
	}

	/**
	 * 예외에 해당하는 메시지를 반환합니다.
	 *
	 * @return 예외 메시지 문자열
	 */
	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}
}