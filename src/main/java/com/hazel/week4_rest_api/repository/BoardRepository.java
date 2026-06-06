package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BoardRepository {
	private final List<Board> boards = new ArrayList<>();

	public BoardRepository() {
		boards.add(new Board(1, "첫 번째 게시글", 10, 3, 120, "hazel", "2026-06-07"));
		boards.add(new Board(2, "Spring Boot 과제 진행 중", 7, 2, 85, "startup", "2026-06-07"));
		boards.add(new Board(3, "REST API 설계 회고", 15, 5, 230, "tester", "2026-06-06"));
	}

	public List<Board> findAll() {
		return boards;
	}
}