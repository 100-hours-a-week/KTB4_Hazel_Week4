package com.hazel.week4_rest_api.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hazel.week4_rest_api.exception.CustomException;
import com.hazel.week4_rest_api.exception.ErrorCode;


public class Board {
	private Integer boardId;
	private String title;
	private Integer likeCount;
	private Integer viewCount;
	private Long userId;
	private String createdAt;
	private String text;
	private List<String> images;
	private Set<Long> likedUserIds = new HashSet<>();

	public Board(Integer boardId, String title, Integer likeCount, Integer views, Long writerId, String createdAt, String text,List<String> images) {
		this.boardId = boardId;
		this.title = title;
		this.likeCount = likeCount;
		this.viewCount = views;
		this.userId = writerId ;
		this.createdAt = createdAt;
		this.text = text;
		this.images = images;
	}

	public Integer getId(){
		return this.boardId;
	}

	public String getTitle(){
		return this.title;
	}

	public Integer getLikeCount(){
		return this.likeCount;
	}

	public Integer getViewCount(){
		return this.viewCount;
	}

	public Long getUserId(){
		return this.userId;
	}

	public String getCreatedAt(){
		return this.createdAt;
	}

	public String getText(){
		return this.text;
	}

	public List<String> getImages(){
		return this.images;
	}

	public void update(String title, List<String> images, String text) {
		if (title != null && !title.isBlank()) {
			this.title = title;
		}

		if (images != null) {
			this.images = images;
		}

		if (text != null && !text.isBlank()) {
			this.text = text;
		}
	}

	public void like(Long userId) {
		if (likedUserIds.contains(userId)) {
			throw new CustomException(ErrorCode.ALREADY_LIKED);
		}

		likedUserIds.add(userId);
		this.likeCount++;
	}

	public void unlike(Long userId) {
		if (!likedUserIds.contains(userId)) {
			throw new CustomException(ErrorCode.NOT_LIKED);
		}

		likedUserIds.remove(userId);
		this.likeCount--;
	}

	public void increaseViews() {
		this.viewCount++;
	}

}
