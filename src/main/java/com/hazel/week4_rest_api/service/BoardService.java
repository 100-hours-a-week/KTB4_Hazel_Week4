package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.Board;
import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.board.BoardCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserResponse;
import com.hazel.week4_rest_api.repository.BoardRepository;
import com.hazel.week4_rest_api.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BoardService {
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	public BoardService(BoardRepository boardRepository,  UserRepository userRepository) {
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
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
}