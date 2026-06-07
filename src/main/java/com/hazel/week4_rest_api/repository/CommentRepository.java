package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
	private Integer sequence = 1;
	private final List<Comment> comments = new ArrayList<>();

	public CommentRepository() {
		comments.add(new Comment(1, 1, "작성자 1", "2025-01-01 00:00:00", "댓글 내용"));
		comments.add(new Comment(2, 1, "작성자 2", "2026-01-01 00:10:00", "두 번째 댓글"));
		comments.add(new Comment(3, 2, "작성자 3", "2026-01-02 00:00:00", "2번 게시글 댓글"));
	}

	public Comment save(Integer boardId, String writer, String createdAt, String content) {
		Comment comment = new Comment(sequence++, boardId, writer, createdAt, content);
		comments.add(comment);
		return comment;
	}

	public List<Comment> findByBoardId(Integer boardId) {
		return comments.stream()
			.filter(comment -> comment.getBoardId().equals(boardId))
			.toList();
	}

	public Optional<Comment> findById(Integer id) {
		return comments.stream()
			.filter(comment -> comment.getId().equals(id))
			.findFirst();
	}

	public void deleteById(Integer id) {
		comments.removeIf(comment -> comment.getId().equals(id));
	}
}
