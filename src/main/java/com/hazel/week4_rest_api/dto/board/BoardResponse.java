package com.hazel.week4_rest_api.dto.board;

import java.util.List;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.BoardImage;

public class BoardResponse {
	private Long id;
	private String title;
	private int likeCount;
	private int commentCount;
	private int viewCount;
	private String createdAt;
	private String writer;
	private List<String> images;
	private String text;

	public BoardResponse(Board board, String writerNickname, Integer commentCount) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.likeCount = board.getLikeCount();
		this.commentCount = commentCount;
		this.viewCount = board.getViewCount();
		this.createdAt = board.getCreatedAt().toString();
		this.writer = writerNickname;
		this.images = board.getImages().stream()
			.map(BoardImage::getImageUrl)
			.toList();

		this.text = board.getText();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getWriter() {
		return writer;
	}

	public String getText() {
		return text;
	}

	public List<String> getImage() {
		return images;
	}
}