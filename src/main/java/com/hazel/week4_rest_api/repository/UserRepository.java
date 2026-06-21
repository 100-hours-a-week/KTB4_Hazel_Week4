package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
}