package com.hazel.week4_rest_api.exception;

import com.hazel.week4_rest_api.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 커스텀 에러 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(new ApiResponse<>(errorCode.getMessage(), null));
	}

	// 기본 예외 처리
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity
			.badRequest()
			.body(new ApiResponse<>(e.getMessage(), null));
	}

	// 예상치 못한 500에러 같은..거 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		return ResponseEntity
			.internalServerError()
			.body(new ApiResponse<>("internal_server_error", null));
	}
}