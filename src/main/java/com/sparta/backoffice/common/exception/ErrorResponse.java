package com.sparta.backoffice.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final int status;
	private final String code;
	private final String message;

	public ErrorResponse(ErrorCode errorCode) {
		this.status = errorCode.getStatus().value();
		this.code = errorCode.name();
		this.message = errorCode.getMessage();

	}

	public ErrorResponse(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}