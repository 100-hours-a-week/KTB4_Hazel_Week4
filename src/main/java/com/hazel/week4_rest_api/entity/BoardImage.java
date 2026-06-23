package com.hazel.week4_rest_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "board_images")
public class BoardImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_image_id")
	private Long boardImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "image_order", nullable = false)
	private Integer imageOrder;

	protected BoardImage() {
	}

	public BoardImage(Board board, String imageUrl, Integer imageOrder) {
		this.board = board;
		this.imageUrl = imageUrl;
		this.imageOrder = imageOrder;
	}

	public Long getId() {
		return boardImageId;
	}
}