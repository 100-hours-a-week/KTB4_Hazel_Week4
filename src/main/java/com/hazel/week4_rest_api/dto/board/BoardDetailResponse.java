package com.hazel.week4_rest_api.dto.board;

import com.hazel.week4_rest_api.domain.Board;

import java.util.List;

public class BoardDetailResponse {
	private Integer id;
	private String title;
	private String writer;
	private String createdAt;
	private List<String> images;
	private String text;
	private Integer likes;
	private Integer views;
	private Integer comments;

	public BoardDetailResponse(Board board, String writerNickname) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.writer = writerNickname;
		this.createdAt = board.getCreatedAt();
		this.images = board.getImages();
		this.text = board.getText();
		this.likes = board.getLikes();
		this.views = board.getViews();
		this.comments = board.getComments();
	}

	public Integer getId() {
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

	public Integer getLikes() {
		return likes;
	}

	public Integer getViews() {
		return views;
	}

	public Integer getComments() {
		return comments;
	}
}