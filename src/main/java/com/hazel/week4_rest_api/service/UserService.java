package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.entity.Board;
import com.hazel.week4_rest_api.entity.BoardLike;
import com.hazel.week4_rest_api.entity.Comment;
import com.hazel.week4_rest_api.entity.User;
import com.hazel.week4_rest_api.repository.BoardLikeRepository;
import com.hazel.week4_rest_api.repository.BoardRepository;
import com.hazel.week4_rest_api.repository.CommentRepository;
import com.hazel.week4_rest_api.repository.UserRepository;
import com.hazel.week4_rest_api.dto.user.UserCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginResponse;
import com.hazel.week4_rest_api.dto.user.UserPasswordRequest;
import com.hazel.week4_rest_api.dto.user.UserUpdateRequest;
import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;
import com.hazel.week4_rest_api.repository.TokenRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final BoardLikeRepository boardLikeRepository;

	public UserService(UserRepository userRepository,  TokenRepository tokenRepository,  BoardRepository boardRepository, CommentRepository commentRepository, BoardLikeRepository boardLikeRepository) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.boardRepository = boardRepository;
		this.commentRepository = commentRepository;
		this.boardLikeRepository = boardLikeRepository;
	}

	// 회원가입
	public User createUser(UserCreateRequest request) {
		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new CustomException(ErrorCode.EMAIL_REQUIRED);
		}

		if (request.getPassword() == null || request.getPassword().isBlank()) {
			throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
		}

		if (request.getNickname() == null || request.getNickname().isBlank()) {
			throw new CustomException(ErrorCode.NICKNAME_REQUIRED);
		}

		if (userRepository.existsByNickname(request.getNickname())){
			throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
		}

		if (userRepository.existsByEmail(request.getEmail())){
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		User user = new User(
			request.getProfileImage(),
			request.getEmail(),
			request.getNickname(),
			request.getPassword()
		);

		return userRepository.save(user);
	}

	// 로그인
	@Transactional(readOnly = true)
	public UserLoginResponse login(UserLoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (!user.getPassword().equals(request.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}

		String accessToken = UUID.randomUUID().toString();

		tokenRepository.saveToken(accessToken, user.getId());

		return new UserLoginResponse(
			accessToken,
			"Bearer",
			user.getId()
		);
	}

	// 내정보 조회
	@Transactional(readOnly = true)
	public User getMyInfo(String authorizationHeader) {
		Long userId = getUserIdFromToken(authorizationHeader);

		return getUser(userId);
	}


	public User getUser(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	// 내정보 수정
	@Transactional
	public User updateMyInfo(String authorizationHeader, String nickname,  MultipartFile profileImage) {
		Long userId = getUserIdFromToken(authorizationHeader);

		User user = getUser(userId);

		String profileImageUrl = user.getProfileImage();

		if (profileImage != null && !profileImage.isEmpty()) {
			String originalFilename = profileImage.getOriginalFilename();
			String fileName = UUID.randomUUID() + "_" + originalFilename;
			Path uploadPath = Paths.get("uploads", fileName).toAbsolutePath();

			try {
				Files.createDirectories(uploadPath.getParent());
				profileImage.transferTo(uploadPath.toFile());
			} catch (IOException e) {
				throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
			}

			profileImageUrl = "/uploads/" + fileName;
		}

		user.updateInfo(nickname, profileImageUrl);

		return user;
	}

	// 로그아웃
	public void logout(String authorizationHeader) {
		String accessToken = getAccessToken(authorizationHeader);

		tokenRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		tokenRepository.deleteToken(accessToken);
	}

	// 비밀번호 변경
	@Transactional
	public void changePassword(String authorizationHeader, UserPasswordRequest request) {
		Long userId = getUserIdFromToken(authorizationHeader);
		User user = getUser(userId);

		if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
			throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
		}

		if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
			throw new CustomException(ErrorCode.NEW_PASSWORD_REQUIRED);
		}

		if (!user.getPassword().equals(request.getCurrentPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}

		user.changePassword(request.getNewPassword());
	}

	// 닉네임 중복 확인
	public void checkNickname(String nickname) {
		if(nickname == null || nickname.isBlank()) {
			throw new CustomException(ErrorCode.NICKNAME_REQUIRED);
		}

		if(userRepository.existsByNickname(nickname)) {
			throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
		}
	}

	// 이메일 중복 확인
	public void checkEmail(String email) {
		if(email == null || email.isBlank()) {
			throw new CustomException(ErrorCode.EMAIL_REQUIRED);
		}

		if(userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
	}

	private Long getUserIdFromToken(String authorizationHeader) {
		String accessToken = getAccessToken(authorizationHeader);

		return tokenRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
	}

	private String getAccessToken(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		return authorizationHeader.substring(7);
	}

	@Transactional
	public void deleteMyAccount(String authorizationHeader) {
		String accessToken = getAccessToken(authorizationHeader);

		Long userId = tokenRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<Board> myBoards = boardRepository.findByUser(user);

		List<BoardLike> myLikes = boardLikeRepository.findByUser(user);
		for (BoardLike boardLike : myLikes) {
			Board likedBoard = boardLike.getBoard();

			if (!likedBoard.getUser().getId().equals(user.getId())) {
				likedBoard.decreaseLikeCount();
			}

			boardLikeRepository.delete(boardLike);
		}

		List<Comment> myComments = commentRepository.findByUser(user);
		for (Comment comment : myComments) {
			Board commentedBoard = comment.getBoard();

			if (!commentedBoard.getUser().getId().equals(user.getId())) {
				commentedBoard.decreaseCommentCount();
			}

			commentRepository.delete(comment);
		}

		for (Board board : myBoards) {
			commentRepository.deleteByBoard(board);
			boardLikeRepository.deleteByBoard(board);
			boardRepository.delete(board);
		}

		tokenRepository.deleteToken(accessToken);

		userRepository.delete(user);
	}

}