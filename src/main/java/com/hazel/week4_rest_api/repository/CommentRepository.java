package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.Comment;
import com.hazel.week4_rest_api.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByBoard(Board board);

	List<Comment> findByUser(User user);

	void deleteByBoard(Board board);
}