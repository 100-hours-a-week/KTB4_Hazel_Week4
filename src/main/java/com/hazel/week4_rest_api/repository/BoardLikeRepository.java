package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.BoardLike;
import com.hazel.week4_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

	boolean existsByUserAndBoard(User user, Board board);

	Optional<BoardLike> findByUserAndBoard(User user, Board board);

	void deleteByBoard(Board board);
}