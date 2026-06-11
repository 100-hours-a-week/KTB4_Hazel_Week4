package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.user.UserCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginResponse;
import com.hazel.week4_rest_api.dto.user.UserPasswordRequest;
import com.hazel.week4_rest_api.dto.user.UserUpdateRequest;
import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;
import com.hazel.week4_rest_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
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

		if (userRepository.isNicknameExists(request.getNickname())){
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		if (userRepository.isNicknameExists(request.getEmail())){
			throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
		}

		return userRepository.save(
			request.getProfileImage(),
			request.getEmail(),
			request.getNickname(),
			request.getPassword()
		);
	}

	// 로그인
	public UserLoginResponse login(UserLoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (!user.getPassword().equals(request.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String accessToken = UUID.randomUUID().toString();
		userRepository.saveToken(accessToken, user.getId());

		return new UserLoginResponse(
			accessToken,
			"Bearer",
			user.getId()
		);
	}

	// 내정보 조회
	public User getMyInfo(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		return getUser(userId);
	}

	public User getUser(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	// 내정보 수정
	public User updateMyInfo(String authorizationHeader, UserUpdateRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

		User user = getUser(userId);
		user.updateInfo(request.getNickname(), request.getProfileImage());

		return user;
	}

	// 로그아웃
	public void logout(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		userRepository.deleteToken(accessToken);
	}

	// 비밀번호 변경
	public void changePassword(String authorizationHeader, UserPasswordRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

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

		if(userRepository.isNicknameExists(nickname)) {
			throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
		}
	}

	// 이메일 중복 확인
	public void checkEmail(String email) {
		if(email == null || email.isBlank()) {
			throw new CustomException(ErrorCode.EMAIL_REQUIRED);
		}

		if(userRepository.isEmailExists(email)) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
	}

}