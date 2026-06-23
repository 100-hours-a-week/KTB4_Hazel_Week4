package com.hazel.week4_rest_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(
	name = "users",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_users_email", columnNames = "email"),
		@UniqueConstraint(name = "uk_users_nickname", columnNames = "nickname")
	}
)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(nullable = false, name = "profile_image")
	private String profileImage;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String password;


	protected User() {
	}

	public User(String profileImage, String email, String nickname, String password) {
		this.profileImage = profileImage;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
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