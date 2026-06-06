package com.hazel.week4_rest_api.controller;

import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.common.ApiResponse;
import com.hazel.week4_rest_api.dto.user.UserCreateRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginRequest;
import com.hazel.week4_rest_api.dto.user.UserLoginResponse;
import com.hazel.week4_rest_api.dto.user.UserPasswordRequest;
import com.hazel.week4_rest_api.dto.user.UserResponse;
import com.hazel.week4_rest_api.dto.user.UserUpdateRequest;
import com.hazel.week4_rest_api.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
		User user = userService.createUser(request);
		return new ApiResponse<>(
			"회원가입에 성공했습니다.",
			new UserResponse(user)
		);
	}

	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
		User user = userService.getUser(id);
		return new ApiResponse<>(
			"회원 정보 조회에 성공했습니다.",
			new UserResponse(user)
		);
	}

	@PostMapping("/login")
	public ApiResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
		UserLoginResponse response = userService.login(request);
		return new ApiResponse<>(
			"로그인에 성공했습니다.",
			response
		);
	}

	@GetMapping("/me")
	public ApiResponse<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
		User user = userService.getMyInfo(authorizationHeader);
		return new ApiResponse<>(
			"회원 정보 조회에 성공했습니다.",
			new UserResponse(user)
		);
	}


	@PatchMapping("/me")
	public ApiResponse<UserResponse> updateCurrentUser(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UserUpdateRequest request
	) {
		User user = userService.updateMyInfo(authorizationHeader, request);

		return new ApiResponse<>(
			"회원 정보 수정에 성공했습니다.",
			new UserResponse(user)
		);
	}

	@PostMapping("/logout")
	public ApiResponse<UserResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
		userService.logout(authorizationHeader);

		return new ApiResponse<>(
			"로그아웃에 성공했습니다.",
			null
		);
	}

	@PatchMapping("/me/password")
	public ApiResponse<Void> changePassword(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UserPasswordRequest request
	) {
		userService.changePassword(authorizationHeader, request);

		return new ApiResponse<>(
			"비밀번호 변경에 성공했습니다.",
			null
		);
	}

	@GetMapping("/nickname/check")
	public ApiResponse<UserResponse> checkNickname(@RequestParam String nickname) {
		userService.checkNickname(nickname);

		return new ApiResponse<>(
			"닉네임이 중복되지 않습니다.",
			null
		);
	}
}