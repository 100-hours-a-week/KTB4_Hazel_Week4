package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
	private final Map<Long, User> store = new HashMap<>();
	private final Map<String, Long> tokenStore = new HashMap<>();
	private Long sequence = 1L;

	public User save(String profileImage, String email, String nickname, String password) {
		User user = new User(sequence++, profileImage, email, nickname, password);
		store.put(user.getId(), user);
		return user;
	}

	public Optional<User> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	public Optional<User> findByEmail(String email) {
		return store.values().stream()
			.filter(user -> user.getEmail().equals(email))
			.findFirst();
	}

	public void saveToken(String accessToken, Long userId) {
		tokenStore.put(accessToken, userId);
	}

	public Optional<Long> findUserIdByToken(String accessToken) {
		return Optional.ofNullable(tokenStore.get(accessToken));
	}

	public void deleteToken(String accessToken) {
		tokenStore.remove(accessToken);
	}

	public boolean isEmailExists(String email) {
		return store.values().stream()
			.anyMatch(user -> user.getEmail().equals(email));
	}
}