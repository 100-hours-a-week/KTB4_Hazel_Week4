package com.hazel.week4_rest_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "profile_image")
	private String profileImage;

	private String email;
	private String password;
	private String nickname;

	protected User() {
	}

	public User(String profileImage, String email, String password, String nickname) {
		this.profileImage = profileImage;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}

	public Long getId() {
		return userId;
	}

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