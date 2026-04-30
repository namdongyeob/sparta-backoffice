package com.sparta.backoffice.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 전역에서 발생하는 예외를 공통으로 처리하는 글로벌 예외 처리기입니다.
 * RestControllerAdvice를 사용하여 모든 컨트롤러에서 던져지는 예외를 가로채서 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 비즈니스 로직에서 발생한 CustomException을 처리합니다.
	 *
	 * @param e 발생한 CustomException
	 * @return 예외 정보를 담은 ErrorResponse와 상태 코드가 포함된 ResponseEntity
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.warn("비즈니스 예외 발생 - code: {}, message: {}",
			e.getErrorCode(), e.getMessage());

		return ResponseEntity
			.status(e.getStatus())
			.body(new ErrorResponse(e.getErrorCode()));
	}

	/**
	 * 요청 데이터의 @Valid 유효성 검증 실패 시 발생하는 MethodArgumentNotValidException을 처리합니다.
	 * 발생한 여러 검증 오류 중 첫 번째 메시지를 반환합니다.
	 * 람다식으로 변경
	 * @param e 발생한 MethodArgumentNotValidException
	 * @return 유효성 검증 실패 정보를 담은 ErrorResponse와 400 Bad Request 상태 코드가 포함된 ResponseEntity
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
		MethodArgumentNotValidException e
	) {
		String errorMessage = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
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