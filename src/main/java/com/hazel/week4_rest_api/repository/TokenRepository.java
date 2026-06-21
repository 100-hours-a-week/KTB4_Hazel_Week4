package com.hazel.week4_rest_api.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TokenRepository {

	private final Map<String, Long> tokenStore = new HashMap<>();

	public void saveToken(String accessToken, Long userId) {
		tokenStore.put(accessToken, userId);
	}

	public Optional<Long> findUserIdByToken(String accessToken) {
		return Optional.ofNullable(tokenStore.get(accessToken));
	}

	public void deleteToken(String accessToken) {
		tokenStore.remove(accessToken);
	}
}