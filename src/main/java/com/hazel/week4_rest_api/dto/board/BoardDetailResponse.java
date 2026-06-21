package com.hazel.week4_rest_api.dto.board;

import com.hazel.week4_rest_api.entity.Board;

import java.util.List;

public class BoardDetailResponse {
	private Long id;
	private String title;
	private String writer;
	private String createdAt;
	private String image;
	private String text;
	private Integer likeCount;
	private Integer viewCount;
	private Integer commentCount;

	public BoardDetailResponse(Board board, String writerNickname, Integer commentCount) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.writer = writerNickname;
		this.createdAt = board.getCreatedAt().toString();
		this.image = board.getImage();
		this.text = board.getText();
		this.likeCount = board.getLikeCount();
		this.viewCount = board.getViewCount();
		this.commentCount = commentCount;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getWriter() {
		return writer;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getImages() {
		return image;
	}

	public String getText() {
		return text;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public Integer getViews() {
		return viewCount;
	}

	public Integer getComments() {
		return commentCount;
	}
}