package com.hazel.week4_rest_api.service;

import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.user.UserCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginResponse;
import com.hazel.week4_rest_api.dto.user.UserUpdateRequest;
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
			throw new IllegalArgumentException("이메일은 필수값입니다.");
		}

		if (request.getPassword() == null || request.getPassword().isBlank()) {
			throw new IllegalArgumentException("비밀번호는 필수값입니다.");
		}

		if (request.getNickname() == null || request.getNickname().isBlank()) {
			throw new IllegalArgumentException("닉네임은 필수값입니다.");
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
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

		return getUser(userId);
	}

	public User getUser(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	}

	// 내정보 수정
	public User updateMyInfo(String authorizationHeader, UserUpdateRequest request) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("인증 정보가 없습니다.");
		}

		String accessToken = authorizationHeader.substring(7);

		Long userId = userRepository.findUserIdByToken(accessToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

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
}