package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.domain.Comment;
import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.board.BoardCommentResponse;
import com.hazel.week4_rest_api.dto.board.BoardCreateRequest;
import com.hazel.week4_rest_api.dto.board.CommentUpdateRequest;
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

	public List<Board> getBoards() {
		return boardRepository.findAll();
	}

	public Board createBoard(String authorizationHeader, BoardCreateRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new IllegalArgumentException("제목은 필수값입니다.");
		}

		if (request.getText() == null || request.getText().isBlank()) {
			throw new IllegalArgumentException("본문은 필수값입니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		List<String> images = request.getImages() == null
			? List.of()
			: request.getImages();

		return boardRepository.save(
			request.getTitle(),
			images,
			request.getText(),
			user.getNickname(),
			LocalDate.now().toString()
		);
	}

	public void deleteBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (!board.getWriter().equals(user.getNickname())) {
			throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
		}

		boardRepository.deleteById(boardId);
	}

	public void likeBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		board.like(userId);
	}

	public void unlikeBoard(String authorizationHeader, Integer boardId) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		board.unlike(userId);
	}

	public Board getBoard(Integer boardId) {
		return boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
	}

	public BoardCommentResponse getComments(Integer boardId) {
		boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

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
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		if (request.getContent() == null || request.getContent().isBlank()) {
			throw new IllegalArgumentException("댓글 내용은 필수값입니다.");
		}

		boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		if (!comment.getBoardId().equals(boardId)) {
			throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
		}

		if (!comment.getWriter().equals(user.getNickname())) {
			throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
		}

		comment.updateContent(request.getContent());
	}

	public void deleteComment(
		String authorizationHeader,
		Integer boardId,
		Integer commentId
	) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		if (!comment.getBoardId().equals(boardId)) {
			throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
		}

		if (!comment.getWriter().equals(user.getNickname())) {
			throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
		}

		commentRepository.deleteById(commentId);
	}

}