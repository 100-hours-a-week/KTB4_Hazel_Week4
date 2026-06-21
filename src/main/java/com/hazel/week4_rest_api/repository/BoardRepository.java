package com.hazel.week4_rest_api.repository;

import com.hazel.week4_rest_api.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}