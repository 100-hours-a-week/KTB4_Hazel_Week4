package com.hazel.week4_rest_api.entity;

import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "boards")
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_id")
	private Long boardId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 225)
	private String title;

	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@Column(name = "comment_count", nullable = false)
	private Integer commentCount = 0;

	@Column(nullable = false, length = 5000)
	private String text;

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BoardImage> images = new ArrayList<>();

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected Board() {
	}

	public Board(User user, String title, String text) {
		this.user = user;
		this.title = title;
		this.text = text;
		this.likeCount = 0;
		this.viewCount = 0;
		this.commentCount = 0;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	@PreUpdate // update직전에날리는거임..
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return boardId;
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getWriterNickname() {
		return user.getNickname();
	}

	public void update(String title, String text) {
		if (title != null && !title.isBlank()) {
			this.title = title;
		}

		if (text != null && !text.isBlank()) {
			this.text = text;
		}
	}

	public void addImage(String imageUrl, Integer imageOrder) {
		this.images.add(new BoardImage(this, imageUrl, imageOrder));
	}

	public void clearImages() {
		this.images.clear();
	}

	public void updateImages(List<String> imageUrls) {
		this.images.clear();

		if (imageUrls == null) {
			return;
		}

		for (int i = 0; i < imageUrls.size(); i++) {
			this.images.add(new BoardImage(this, imageUrls.get(i), i + 1));
		}
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void increaseLikeCount() {
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (this.likeCount <= 0) {
			throw new CustomException(ErrorCode.NOT_LIKED);
		}

		this.likeCount--;
	}

	public void increaseCommentCount() {
		this.commentCount++;
	}

	public void decreaseCommentCount() {
		if (this.commentCount > 0) {
			this.commentCount--;
		}
	}
}