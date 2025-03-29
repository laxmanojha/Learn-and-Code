package com.itt.tubmlr.dto;

public class ApiRequestParamsDto {
	private String blogName;
	private int num;
	private int start;
	
	public ApiRequestParamsDto(String blogName, int num, int start) {
		super();
		this.blogName = blogName;
		this.num = num;
		this.start = start;
	}
	
	public String getBlogName() {
		return blogName;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getStart() {
		return start;
	}
}
