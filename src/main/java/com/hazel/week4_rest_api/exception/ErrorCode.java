package com.hazel.week4_rest_api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	// 400 잘못된 요청
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	NOT_LIKED(HttpStatus.BAD_REQUEST, "좋아요를 누른 게시글이 아닙니다."),

	// 401 인증 실패
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

	// 403 권한 없음
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

	// 404 찾을 수 없거나 잘못된 값을 넣음
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

	// 409 요청 충돌
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
	ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요한 게시글입니다."),

	// 500 서버 오류
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
