package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.Comment;
import com.hazel.week4_rest_api.entity.User;
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
import com.hazel.week4_rest_api.repository.CommentRepository;
import com.hazel.week4_rest_api.repository.TokenRepository;
import com.hazel.week4_rest_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardService {
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final TokenRepository tokenRepository;

	public BoardService(
		BoardRepository boardRepository,
		UserRepository userRepository,
		CommentRepository commentRepository,
		TokenRepository tokenRepository
	) {
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
		this.tokenRepository = tokenRepository;
	}

	@Transactional(readOnly = true)
	public List<BoardResponse> getBoards() {
		return boardRepository.findAll().stream()
			.map(board -> {
				String writerNickname = board.getUser().getNickname();
				int commentCount = commentRepository.findByBoard(board).size();

				return new BoardResponse(board, writerNickname, commentCount);
			})
			.toList();
	}

	public BoardResponse createBoard(String authorizationHeader, BoardCreateRequest request) {
		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new CustomException(ErrorCode.TITLE_REQUIRED);
		}

		if (request.getText() == null || request.getText().isBlank()) {
			throw new CustomException(ErrorCode.TEXT_REQUIRED);
		}

		User user = getUserFromToken(authorizationHeader);

		Board board = new Board(
			user,
			request.getTitle(),
			request.getText(),
			getFirstImage(request.getImages())
		);

		Board savedBoard = boardRepository.save(board);

		return new BoardResponse(savedBoard, user.getNickname(), 0);
	}

	public void deleteBoard(String authorizationHeader, Long boardId) {
		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		commentRepository.deleteByBoard(board);
		boardRepository.delete(board);
	}

	public void likeBoard(String authorizationHeader, Long boardId) {
		getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.increaseLikeCount();
	}

	public void unlikeBoard(String authorizationHeader, Long boardId) {
		getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.decreaseLikeCount();
	}

	@Transactional(readOnly = true)
	public BoardResponse getBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		String writerNickname = board.getUser().getNickname();
		int commentCount = commentRepository.findByBoard(board).size();

		return new BoardResponse(board, writerNickname, commentCount);
	}

	public BoardDetailResponse getDetailBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.increaseViewCount();

		String writerNickname = board.getUser().getNickname();
		int commentCount = commentRepository.findByBoard(board).size();

		return new BoardDetailResponse(board, writerNickname, commentCount);
	}

	@Transactional(readOnly = true)
	public BoardCommentResponse getComments(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		List<Comment> comments = commentRepository.findByBoard(board);

		List<BoardCommentResponse.CommentResponse> commentResponses = comments.stream()
			.map(comment -> new BoardCommentResponse.CommentResponse(
				comment.getId(),
				comment.getWriter(),
				comment.getCreatedAt().toString(),
				comment.getContent()
			))
			.toList();

		return new BoardCommentResponse(boardId, commentResponses);
	}

	public void createComment(
		String authorizationHeader,
		Long boardId,
		CommentCreateRequest request
	) {
		if (request.getContent() == null || request.getContent().isBlank()) {
			throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
		}

		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		Comment comment = new Comment(board, user, request.getContent());
		commentRepository.save(comment);
	}

	public void updateComment(
		String authorizationHeader,
		Long boardId,
		Long commentId,
		CommentUpdateRequest request
	) {
		if (request.getContent() == null || request.getContent().isBlank()) {
			throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
		}

		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getBoard().getId().equals(board.getId())) {
			throw new CustomException(ErrorCode.COMMENT_BOARD_MISMATCH);
		}

		if (!comment.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		comment.updateContent(request.getContent());
	}

	public void deleteComment(
		String authorizationHeader,
		Long boardId,
		Long commentId
	) {
		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getBoard().getId().equals(board.getId())) {
			throw new CustomException(ErrorCode.COMMENT_BOARD_MISMATCH);
		}

		if (!comment.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		commentRepository.delete(comment);
	}

	public BoardDetailResponse updateBoard(String authorizationHeader, Long boardId, BoardUpdateRequest request) {
		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new CustomException(ErrorCode.TITLE_REQUIRED);
		}

		if (request.getText() == null || request.getText().isBlank()) {
			throw new CustomException(ErrorCode.TEXT_REQUIRED);
		}

		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		board.update(
			request.getTitle(),
			getFirstImage(request.getImages()),
			request.getText()
		);

		int commentCount = commentRepository.findByBoard(board).size();

		return new BoardDetailResponse(board, user.getNickname(), commentCount);
	}

	private User getUserFromToken(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = tokenRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private String getFirstImage(List<String> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}

		return images.get(0);
	}
}