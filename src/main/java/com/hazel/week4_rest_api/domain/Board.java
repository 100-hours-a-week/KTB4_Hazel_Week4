package com.hazel.week4_rest_api.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
	private Integer id;
	private String title;
	private Integer likes;
	private Integer comments;
	private Integer views;
	private String writer;
	private String createdAt;
	private String text;
	private List<String> images;
	private Set<Long> likedUserIds = new HashSet<>();

	public Board(Integer id, String title, Integer likes, Integer comments, Integer views, String writer, String createdAt, String text,List<String> images) {
		this.id = id;
		this.title = title;
		this.likes = likes;
		this.comments = comments;
		this.views = views;
		this.writer = writer;
		this.createdAt = createdAt;
	}

	public Integer getId(){
		return this.id;
	}

	public String getTitle(){
		return this.title;
	}

	public Integer getLikes(){
		return this.likes;
	}

	public Integer getComments(){
		return this.comments;
	}

	public Integer getViews(){
		return this.views;
	}

	public String getWriter(){
		return this.writer;
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
			throw new IllegalArgumentException("이미 좋아요한 게시글입니다.");
		}

		likedUserIds.add(userId);
		this.likes++;
	}

	public void unlike(Long userId) {
		if (!likedUserIds.contains(userId)) {
			throw new IllegalArgumentException("좋아요를 누른 게시글이 아닙니다.");
		}

		likedUserIds.remove(userId);
		this.likes--;
	}

}
