package com.hazel.week4_rest_api.entity;

import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "boards")
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_id")
	private Long boardId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 225)
	private String title;

	@Column(name = "like_count", nullable = false)
	private Integer likeCount;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount;

	@Column(nullable = false, length = 5000)
	private String text;

	@Column(length = 225)
	private String image;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected Board() {
	}

	public Board(User user, String title, String text, String image) {
		this.user = user;
		this.title = title;
		this.text = text;
		this.image = image;
		this.likeCount = 0;
		this.viewCount = 0;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return boardId;
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getWriterNickname() {
		return user.getNickname();
	}

	public void update(String title, String image, String text) {
		if (title != null && !title.isBlank()) {
			this.title = title;
		}

		if (image != null && !image.isBlank()) {
			this.image = image;
		}

		if (text != null && !text.isBlank()) {
			this.text = text;
		}
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void increaseLikeCount() {
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (this.likeCount <= 0) {
			throw new CustomException(ErrorCode.NOT_LIKED);
		}

		this.likeCount--;
	}
}