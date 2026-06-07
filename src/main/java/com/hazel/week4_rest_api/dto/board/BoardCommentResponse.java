package com.hazel.week4_rest_api.dto.board;

import java.util.List;

public class BoardCommentResponse {
	private Integer id;
	private List<CommentResponse> comments;

	public BoardCommentResponse(Integer id, List<CommentResponse> comments) {
		this.id = id;
		this.comments = comments;
	}

	public Integer getId() {
		return id;
	}

	public List<CommentResponse> getComments() {
		return comments;
	}

	public static class CommentResponse {
		private Integer id;
		private String writer;
		private String createdAt;
		private String content;

		public CommentResponse(Integer id, String writer, String createdAt, String content) {
			this.id = id;
			this.writer = writer;
			this.createdAt = createdAt;
			this.content = content;
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
	}
}