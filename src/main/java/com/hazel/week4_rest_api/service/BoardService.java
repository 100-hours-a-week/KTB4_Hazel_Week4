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
import com.hazel.week4_rest_api.entity.BoardLike;
import com.hazel.week4_rest_api.repository.BoardLikeRepository;

import java.util.List;

@Service
@Transactional
public class BoardService {
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final TokenRepository tokenRepository;
	private final BoardLikeRepository boardLikeRepository;

	public BoardService(
		BoardRepository boardRepository,
		UserRepository userRepository,
		CommentRepository commentRepository,
		TokenRepository tokenRepository,
		BoardLikeRepository boardLikeRepository
	) {
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
		this.tokenRepository = tokenRepository;
		this.boardLikeRepository = boardLikeRepository;
	}

	@Transactional(readOnly = true)
	public List<BoardResponse> getBoards() {
		return boardRepository.findAllWithUser().stream()
			.map(board -> {
				String writerNickname = board.getUser().getNickname();

				return new BoardResponse(board, writerNickname, board.getCommentCount());
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
			request.getText()
		);

		board.updateImages(request.getImages());

		Board savedBoard = boardRepository.save(board);

		return new BoardResponse(savedBoard, user.getNickname(), savedBoard.getCommentCount());
	}

	public void deleteBoard(String authorizationHeader, Long boardId) {
		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		commentRepository.deleteByBoard(board);
		boardLikeRepository.deleteByBoard(board);

		boardRepository.delete(board);
	}

	public void likeBoard(String authorizationHeader, Long boardId) {
		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		// board_likes 테이블에서 이미 좋아요를 눌렀는지 확인함
		if (boardLikeRepository.existsByUserAndBoard(user, board)) {
			throw new CustomException(ErrorCode.ALREADY_LIKED);
		}

		// 누가 어떤 게시글에 좋아요를 눌렀는지 저장ㅎ맘
		BoardLike boardLike = new BoardLike(user, board);
		boardLikeRepository.save(boardLike);

		// like_count는 목록 조회 성능을 위한 숫자 캐시처럼 관리함
		board.increaseLikeCount();
	}

	public void unlikeBoard(String authorizationHeader, Long boardId) {
		User user = getUserFromToken(authorizationHeader);

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		BoardLike boardLike = boardLikeRepository.findByUserAndBoard(user, board)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_LIKED));

		boardLikeRepository.delete(boardLike);
		board.decreaseLikeCount();
	}

	public BoardDetailResponse getDetailBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

		board.increaseViewCount();

		String writerNickname = board.getUser().getNickname();

		return new BoardDetailResponse(board, writerNickname, board.getCommentCount());
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

		// comment_count 컬럼을 유지하기 위해 댓글 생성 시 증가시킨다.
		board.increaseCommentCount();
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

		// comment_count 컬럼을 유지하기 위해 댓글 삭제 시 감소시킨다.
		board.decreaseCommentCount();
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

		// 게시글의 기본 내용 수정
		board.update(
			request.getTitle(),
			request.getText()
		);

		// 이미지 목록은 BoardImage로 따로 관리하므로 리스트 전체를 교체한다.
		board.updateImages(request.getImages());

		return new BoardDetailResponse(board, user.getNickname(), board.getCommentCount());
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

}