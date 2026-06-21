package com.hazel.week4_rest_api.dto.board;

import java.util.List;

public class BoardCommentResponse {
	private Long id;
	private List<CommentResponse> comments;

	public BoardCommentResponse(Long id, List<CommentResponse> comments) {
		this.id = id;
		this.comments = comments;
	}

	public Long getId() {
		return id;
	}

	public List<CommentResponse> getComments() {
		return comments;
	}

	public static class CommentResponse {
		private Long id;
		private String writer;
		private String createdAt;
		private String content;

		public CommentResponse(Long id, String writer, String createdAt, String content) {
			this.id = id;
			this.writer = writer;
			this.createdAt = createdAt;
			this.content = content;
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
	}
}