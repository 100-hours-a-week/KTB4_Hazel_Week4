package com.hazel.week4_rest_api.domain;

public class User {
	private Long id;
	private String profileImage;
	private String email;
	private String nickname;
	private String password;

	public User(Long id, String profileImage, String email, String nickname, String password) {
		this.id = id;
		this.profileImage = profileImage;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

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

	// update할 때 필수값 아니여도 되게 처리
	public void updateInfo(String nickname, String profileImage) {
		if (nickname != null && !nickname.isBlank()) {
			this.nickname = nickname;
		}

		if (profileImage != null && !profileImage.isBlank()) {
			this.profileImage = profileImage;
		}
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
}