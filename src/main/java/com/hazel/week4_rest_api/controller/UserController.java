package com.hazel.week4_rest_api.controller;

import com.hazel.week4_rest_api.domain.User;
import com.hazel.week4_rest_api.dto.UserCreateRequest;
import com.hazel.week4_rest_api.dto.UserLoginRequest;
import com.hazel.week4_rest_api.dto.UserLoginResponse;
import com.hazel.week4_rest_api.dto.UserResponse;
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
	public UserResponse createUser(@RequestBody UserCreateRequest request) {
		User user = userService.createUser(request);
		return new UserResponse(user);
	}

	@GetMapping("/{id}")
	public UserResponse getUser(@PathVariable Long id) {
		User user = userService.getUser(id);
		return new UserResponse(user);
	}

	@PostMapping("/login")
	public UserLoginResponse login(@RequestBody UserLoginRequest request) {
		return userService.login(request);
	}

	@GetMapping("/me")
	public UserResponse getMyInfo(@RequestHeader("Authorization") String authorizationHeader) {
		User user = userService.getMyInfo(authorizationHeader);
		return new UserResponse(user);
	}
}