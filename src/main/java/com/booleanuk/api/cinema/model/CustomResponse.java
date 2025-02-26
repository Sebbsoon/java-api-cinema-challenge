package com.booleanuk.api.cinema.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomResponse<T> {
	private String status;
	private T data;

	public CustomResponse(String status, T data) {
		this.status = status;
		this.data = data;
	}
}
