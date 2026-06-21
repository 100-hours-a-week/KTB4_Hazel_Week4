package com.hazel.week4_rest_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

import com.hazel.week4_rest_api.entity.Board;

@Entity
@Getter
@Table(name = "comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false, length = 5000)
	private String content;

	protected Comment() {
	}

	public Comment(Board board, User user, String content) {
		this.board = board;
		this.user = user;
		this.content = content;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return commentId;
	}

	public Long getBoardId() {
		return board.getId();
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getWriter() {
		return user.getNickname();
	}

	public void updateContent(String content) {
		if (content != null && !content.isBlank()) {
			this.content = content;
		}
	}

}
