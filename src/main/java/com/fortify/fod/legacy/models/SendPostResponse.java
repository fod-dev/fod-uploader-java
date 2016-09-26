package com.fortify.fod.legacy.models;

import org.apache.http.HttpResponse;

public class SendPostResponse {

	private HttpResponse response;
	private String errorMessage;
	
	
	public HttpResponse getResponse() {
		return response;
	}
	
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
