package com.hazel.week4_rest_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(
	name = "board_likes",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_board_likes_user_board",
			columnNames = {"user_id", "board_id"}
		)
	}
)
public class BoardLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_like_id")
	private Long boardLikeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	protected BoardLike() {
	}

	public BoardLike(User user, Board board) {
		this.user = user;
		this.board = board;
	}

	public Long getId() {
		return boardLikeId;
	}
}
