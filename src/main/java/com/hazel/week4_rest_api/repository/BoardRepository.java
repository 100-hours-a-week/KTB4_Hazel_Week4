package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

	@Query("select b from Board b join fetch b.user order by b.createdAt desc")
	List<Board> findAllWithUser();

	List<Board> findByUser(User user);
}