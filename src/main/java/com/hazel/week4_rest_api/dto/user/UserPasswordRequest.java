package com.hazel.week4_rest_api.dto.user;

public class UserPasswordRequest {
	private String currentPassword;
	private String newPassword;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}
}
