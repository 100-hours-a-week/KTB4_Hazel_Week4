package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByBoard(Board board);

	void deleteByBoard(Board board);
}