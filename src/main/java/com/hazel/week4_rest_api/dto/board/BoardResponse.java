package com.hazel.week4_rest_api.dto.board;

import java.util.List;

import com.hazel.week4_rest_api.domain.Board;

public class BoardResponse {
	private Integer id;
	private String title;
	private int likes;
	private int comments;
	private int views;
	private String createdAt;
	private String writer;
	private List<String> images;
	private String text;

	public BoardResponse(Board board,String writerNickname, Integer commentCount) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.likes = board.getLikes();
		this.comments = commentCount;
		this.views = board.getViews();
		this.createdAt = board.getCreatedAt();
		this.writer = writerNickname;
		this.images = board.getImages();
		this.text = board.getText();
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getLikes() {
		return likes;
	}

	public int getComments() {
		return comments;
	}

	public int getViews() {
		return views;
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

	public List<String> getImages() {
		return images;
	}
}