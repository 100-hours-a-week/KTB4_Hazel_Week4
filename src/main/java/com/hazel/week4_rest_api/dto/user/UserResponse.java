package com.hazel.week4_rest_api.dto.user;

import com.hazel.week4_rest_api.entity.User;

public class UserResponse {
	private Long id;
	private String profileImage;
	private String email;
	private String nickname;

	public UserResponse(User user) {
		this.id = user.getId();
		this.profileImage = user.getProfileImage();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
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
}