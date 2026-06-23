package com.hazel.week4_rest_api.dto.board;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.BoardImage;

import java.util.List;

public class BoardDetailResponse {
	private Long id;
	private String title;
	private String writer;
	private String createdAt;
	private List<String> images;
	private String text;
	private Integer likeCount;
	private Integer viewCount;
	private Integer commentCount;

	public BoardDetailResponse(Board board, String writerNickname, Integer commentCount) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.writer = writerNickname;
		this.createdAt = board.getCreatedAt().toString();
		this.images = board.getImages().stream()
			.map(BoardImage::getImageUrl)
			.toList();
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

	public List<String> getImages() {
		return images;
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