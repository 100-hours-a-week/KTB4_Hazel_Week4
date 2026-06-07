package com.hazel.week4_rest_api.dto.board;

import java.util.List;

public class BoardUpdateRequest {
	private String title;
	private List<String> images;
	private String text;

	public String getTitle() {
		return title;
	}

	public List<String> getImages() {
		return images;
	}

	public String getText() {
		return text;
	}
}