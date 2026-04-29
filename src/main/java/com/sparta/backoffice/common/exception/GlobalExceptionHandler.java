package com.sparta.backoffice.common.exception;

import org.springframework.http.HttpStatus;
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
	public ResponseEntity<ErrorResponse> handleServiceException(CustomException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage());
		return ResponseEntity
			.status(e.getStatus())
			.body(response);
	}

	// @Valid 유효성 검증 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e
	) {
		String errorMessage = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(fieldError -> fieldError.getDefaultMessage())
			.orElse("입력 값이 올바르지 않습니다.");

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(errorMessage));
	}

	// 그 외 모든 예외 처리
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.error("예상하지 못한 오류가 발생했습니다.", e);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
	}
}