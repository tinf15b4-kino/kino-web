package de.tinf15b4.kino.web.rest;

import org.springframework.http.HttpStatus;

public class RestResponse {

	private String errorMsg;
	private HttpStatus status;
	private Object value;

	public RestResponse(String errorMsg, HttpStatus status, Object value) {
		super();
		this.errorMsg = errorMsg;
		this.status = status;
		this.value = value;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public Object getValue() {
		return value;
	}

	public boolean hasError() {
		return status != HttpStatus.OK;
	}

}
