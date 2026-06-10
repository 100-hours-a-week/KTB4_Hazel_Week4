package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.domain.Board;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BoardRepository {
	private final Map<Integer, Board> store = new LinkedHashMap<>();
	private Integer sequence = 1;

	public BoardRepository() {
		save("첫 번째 게시글", List.of(), "첫 번째 게시글 내용입니다.", 1L, "2026-06-01");
		save("두 번째 게시글", List.of(), "어쩌구 저쩌구", 2L, "2026-06-07");
		save("세 번째 게시글", List.of(), "어쩌구어쩌구저쩌구.", 3L, "2026-06-06");
	}

	public List<Board> findAll() {
		return new ArrayList<>(store.values());
	}

	public Board save(String title, List<String> images, String text, Long writerId, String createdAt) {
		Board board = new Board(
			sequence++,
			title,
			0,
			0,
			0,
			writerId,
			createdAt,
			text,
			images
		);

		store.put(board.getId(), board);
		return board;
	}

	public Optional<Board> findById(Integer id) {
		return Optional.ofNullable(store.get(id));
	}

	public void deleteById(Integer id) {
		store.remove(id);
	}

}