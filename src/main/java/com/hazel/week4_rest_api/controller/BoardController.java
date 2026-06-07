package com.hazel.week4_rest_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.dto.board.BoardCreateRequest;
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

	@PostMapping
	public ApiResponse<BoardResponse> createBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody BoardCreateRequest request
	) {
		Board board = boardService.createBoard(authorizationHeader, request);

		return new ApiResponse<>(
			"게시글 작성에 성공했습니다.",
			new BoardResponse(board)
		);
	}

	@DeleteMapping("/{boardId}")
	public ApiResponse<Void> deleteBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Integer boardId
	) {
		boardService.deleteBoard(authorizationHeader, boardId);

		return new ApiResponse<>(
			"게시글 삭제에 성공했습니다.",
			null
		);
	}

	@PostMapping("/{boardId}/likes")
	public ApiResponse<Void> likeBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Integer boardId
	){
		boardService.likeBoard(authorizationHeader, boardId);

		return new ApiResponse<>(
			"좋아요에 성공했습니다.",
			null
		);
	}

	@DeleteMapping("/{boardId}/likes")
	public ApiResponse<Void> unlikeBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Integer boardId
	){
		boardService.unlikeBoard(authorizationHeader, boardId);

		return new ApiResponse<>(
			"좋아요 취소에 성공했습니다.",
			null
		);
	}
}
