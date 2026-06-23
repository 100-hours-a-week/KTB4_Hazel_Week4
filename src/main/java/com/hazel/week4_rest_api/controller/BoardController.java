package com.hazel.week4_rest_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazel.week4_rest_api.dto.board.BoardCommentResponse;
import com.hazel.week4_rest_api.dto.board.BoardCreateRequest;
import com.hazel.week4_rest_api.dto.board.BoardDetailResponse;
import com.hazel.week4_rest_api.dto.board.BoardResponse;
import com.hazel.week4_rest_api.dto.board.BoardUpdateRequest;
import com.hazel.week4_rest_api.dto.board.CommentCreateRequest;
import com.hazel.week4_rest_api.dto.board.CommentUpdateRequest;
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
		List<BoardResponse> response  = boardService.getBoards();

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
		BoardResponse response = boardService.createBoard(authorizationHeader, request);


		return new ApiResponse<>(
			"게시글 작성에 성공했습니다.",
			response
		);
	}

	@DeleteMapping("/{boardId}")
	public ApiResponse<Void> deleteBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Long boardId
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
		@PathVariable Long boardId
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
		@PathVariable Long boardId
	){
		boardService.unlikeBoard(authorizationHeader, boardId);

		return new ApiResponse<>(
			"좋아요 취소에 성공했습니다.",
			null
		);
	}

	@GetMapping("/{boardId}")
	public ApiResponse<BoardDetailResponse> getBoard(@PathVariable Long boardId) {
		BoardDetailResponse response = boardService.getDetailBoard(boardId);

		return new ApiResponse<>(
			"게시판 상세 조회에 성공했습니다.",
			response
		);
	}

	@GetMapping("/{boardId}/comments")
	public ApiResponse<BoardCommentResponse> getComments(@PathVariable Long boardId) {
		BoardCommentResponse response = boardService.getComments(boardId);

		return new ApiResponse<>(
			"게시판 댓글 조회에 성공했습니다.",
			response
		);
	}

	@PatchMapping("/{boardId}/comments/{commentId}")
	public ApiResponse<Void> updateComment(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Long boardId,
		@PathVariable Long commentId,
		@RequestBody CommentUpdateRequest request
	) {
		boardService.updateComment(authorizationHeader, boardId, commentId, request);

		return new ApiResponse<>(
			"게시판 댓글 수정에 성공했습니다.",
			null
		);
	}

	@DeleteMapping("/{boardId}/comments/{commentId}")
	public ApiResponse<Void> deleteComment(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Long boardId,
		@PathVariable Long commentId
	) {
		boardService.deleteComment(authorizationHeader, boardId, commentId);

		return new ApiResponse<>(
			"게시판 댓글 삭제에 성공했습니다.",
			null
		);
	}

	@PatchMapping("/{boardId}")
	public ApiResponse<BoardDetailResponse> updateBoard(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Long boardId,
		@RequestBody BoardUpdateRequest request
	) {
		BoardDetailResponse response = boardService.updateBoard(authorizationHeader, boardId, request);

		return new ApiResponse<>(
			"게시글 수정에 성공했습니다.",
			response
		);
	}

	@PostMapping("/{boardId}/comments")
	public ApiResponse<Void> createComment(
		@RequestHeader("Authorization") String authorizationHeader,
		@PathVariable Long boardId,
		@RequestBody CommentCreateRequest request
	) {
		boardService.createComment(authorizationHeader, boardId, request);

		return new ApiResponse<>(
			"게시판 댓글 작성에 성공했습니다.",
			null
		);
	}



}
