package com.hazel.week4_rest_api.domain;

public class Board {
	private Integer id;
	private String title;
	private Integer likes;
	private Integer comments;
	private Integer views;
	private String writer;
	private String createdAt;

	public Board(Integer id, String title, Integer likes, Integer comments, Integer views, String writer, String createdAt) {
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




}
