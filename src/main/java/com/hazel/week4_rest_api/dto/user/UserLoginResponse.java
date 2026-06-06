package com.hazel.week4_rest_api.dto.user;

public class UserLoginResponse {
	private String accessToken;
	private String tokenType;
	private Long userId;

	public UserLoginResponse(String accessToken, String tokenType, Long userId) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public Long getUserId() {
		return userId;
	}
}