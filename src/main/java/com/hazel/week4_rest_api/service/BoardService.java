package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
	private final BoardRepository boardRepository;

	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public List<Board> getBoards() {
		return boardRepository.findAll();
	}
}