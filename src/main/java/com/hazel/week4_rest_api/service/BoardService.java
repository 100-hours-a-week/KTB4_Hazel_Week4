package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.domain.Comment;
import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.board.BoardCommentResponse;
import com.hazel.week4_rest_api.dto.board.BoardCreateRequest;
import com.hazel.week4_rest_api.dto.board.BoardDetailResponse;
import com.hazel.week4_rest_api.dto.board.BoardResponse;
import com.hazel.week4_rest_api.dto.board.BoardUpdateRequest;
import com.hazel.week4_rest_api.dto.board.CommentCreateRequest;
import com.hazel.week4_rest_api.dto.board.CommentUpdateRequest;
import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;
import com.hazel.week4_rest_api.repository.BoardRepository;
import com.hazel.week4_rest_api.repository.UserRepository;
import com.hazel.week4_rest_api.repository.CommentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BoardService {
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

	public BoardService(BoardRepository boardRepository,  UserRepository userRepository, CommentRepository commentRepository) {
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
	}

	public List<BoardResponse> getBoards() {
		return boardRepository.findAll().stream()
			.map(board -> {
				User writer = userRepository.findById(board.getUserId())
					.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

				int commentCount = commentRepository.findByBoardId(board.getId()).size();

				return new BoardResponse(board, writer.getNickname(), commentCount);
			})
			.toList();
	}

	public BoardResponse createBoard(String authorizationHeader, BoardCreateRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new CustomException(ErrorCode.TITLE_REQUIRED);
		}

		if (request.getText() == null || request.getText().isBlank()) {
			throw new CustomException(ErrorCode.TEXT_REQUIRED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<String> images = request.getImages() == null
			? List.of()
			: request.getImages();

		Board board = boardRepository.save(
			request.getTitle(),
			images,
			request.getText(),
			user.getId(),
			LocalDate.now().toString()
		);


		int commentCount = commentRepository.findByBoardId(board.getId()).size();

		return new BoardResponse(board, user.getNickname(), commentCount);
	}

	public void deleteBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.getUserId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		commentRepository.deleteByBoardId(boardId);
		boardRepository.deleteById(boardId);
	}

	public void likeBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.like(userId);
	}

	public void unlikeBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.unlike(userId);
	}

	public BoardResponse getBoard(Integer boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		User writer = userRepository.findById(board.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		int commentCount = commentRepository.findByBoardId(board.getId()).size();

		return new BoardResponse(board, writer.getNickname(), commentCount);
	}

	// 게시글 상세 조회
	public BoardDetailResponse getDetailBoard(Integer boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.increaseViews();

		User writer = userRepository.findById(board.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		int commentCount = commentRepository.findByBoardId(board.getId()).size();

		return new BoardDetailResponse(board, writer.getNickname(), commentCount);
	}

	public BoardCommentResponse getComments(Integer boardId) {
		boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		List<Comment> comments = commentRepository.findByBoardId(boardId);

		List<BoardCommentResponse.CommentResponse> commentResponses = comments.stream()
			.map(comment -> new BoardCommentResponse.CommentResponse(
				comment.getId(),
				comment.getWriter(),
				comment.getCreatedAt(),
				comment.getContent()
			))
			.toList();

		return new BoardCommentResponse(boardId, commentResponses);
	}

	public void updateComment(
		String authorizationHeader,
		Integer boardId,
		Integer commentId,
		CommentUpdateRequest request
	) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		if (request.getContent() == null || request.getContent().isBlank()) {
			throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
		}

		boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getBoardId().equals(boardId)) {
			throw new CustomException(ErrorCode.COMMENT_BOARD_MISMATCH);
		}

		if (!comment.getWriter().equals(user.getNickname())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		comment.updateContent(request.getContent());
	}

	public void deleteComment(
		String authorizationHeader,
		Integer boardId,
		Integer commentId
	) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));


		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));


		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getBoardId().equals(boardId)) {
			throw new CustomException(ErrorCode.COMMENT_BOARD_MISMATCH);
		}

		if (!comment.getWriter().equals(user.getNickname())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		commentRepository.deleteById(commentId);
	}

	public void createComment(
		String authorizationHeader,
		Integer boardId,
		CommentCreateRequest request
	) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		if (request.getContent() == null || request.getContent().isBlank()) {
			throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
		}

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		commentRepository.save(
			boardId,
			user.getNickname(),
			LocalDate.now().toString(),
			request.getContent()
		);
	}

	public BoardDetailResponse updateBoard(String authorizationHeader, Integer boardId, BoardUpdateRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new CustomException(ErrorCode.TITLE_REQUIRED);
		}

		if (request.getText() == null || request.getText().isBlank()) {
			throw new CustomException(ErrorCode.TEXT_REQUIRED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.getUserId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		board.update(
			request.getTitle(),
			request.getImages(),
			request.getText()
		);

		int commentCount = commentRepository.findByBoardId(board.getId()).size();

		return new BoardDetailResponse(board, user.getNickname(), commentCount);
	}

}