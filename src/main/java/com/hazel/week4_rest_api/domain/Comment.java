package com.hazel.week4_rest_api.domain;

public class Comment {
	private Long boardId;
	private Long id;
	private String writer;
	private String createdAt;
	private String content;

	public Comment(Long boardId, Long id, String writer, String createdAt, String content) {
		this.boardId = boardId;
		this.id = id;
		this.writer = writer;
		this.createdAt = createdAt;
		this.content = content;
	}

	public Long getBoardId() {
		return boardId;
	}

	public Long getId() {
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
