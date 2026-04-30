package com.sparta.backoffice.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// CustomException 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.warn("비즈니스 예외 발생 - code: {}, message: {}",
			e.getErrorCode(), e.getMessage());

		return ResponseEntity
			.status(e.getStatus())
			.body(new ErrorResponse(e.getErrorCode()));
	}

	// @Valid 유효성 검증 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
		MethodArgumentNotValidException e
	) {
		String errorMessage = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(fieldError -> fieldError.getDefaultMessage())
			.orElse(ErrorCode.VALIDATION_ERROR.getMessage());

		return ResponseEntity
			.status(ErrorCode.VALIDATION_ERROR.getStatus())
			.body(new ErrorResponse(
				ErrorCode.VALIDATION_ERROR.getStatus().value(),
				ErrorCode.VALIDATION_ERROR.name(),
				errorMessage
			));
	}
}