package com.sparta.backoffice.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public HttpStatus getStatus() {
		return errorCode.getStatus();
	}

	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}
}