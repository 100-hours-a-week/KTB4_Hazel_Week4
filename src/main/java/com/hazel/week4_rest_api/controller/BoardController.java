package com.hazel.week4_rest_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.dto.board.BoardResponse;
import com.hazel.week4_rest_api.dto.common.ApiResponse;
import com.hazel.week4_rest_api.service.BoardService;


@RestController
@RequestMapping("/boards")
public class BoardController {
	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}


	@GetMapping
	public ApiResponse<List<BoardResponse>> getBoards() {
		List<Board> boards = boardService.getBoards();

		List<BoardResponse> response = boards.stream()
			.map(board -> new BoardResponse(board))
			.toList();

		return new ApiResponse<>(
			"게시글 목록 조회에 성공했습니다.",
			response
		);
	}
}
