package com.hazel.week4_rest_api.dto;

public class UserCreateRequest {
	private String profileImage;
	private String email;
	private String nickname;
	private String password;

	public String getProfileImage() {
		return profileImage;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}
}