package com.hazel.week4_rest_api.domain;

public class Comment {
	private Integer boardId;
	private Integer id;
	private String writer;
	private String createdAt;
	private String content;

	public Comment(Integer boardId, Integer id, String writer, String createdAt, String content) {
		this.boardId = boardId;
		this.id = id;
		this.writer = writer;
		this.createdAt = createdAt;
		this.content = content;
	}

	public Integer getBoardId() {
		return boardId;
	}

	public Integer getId() {
		return id;
	}

	public String getWriter() {
		return writer;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getContent() {
		return content;
	}

	public void updateContent(String content) {
		this.content = content;
	}
}
