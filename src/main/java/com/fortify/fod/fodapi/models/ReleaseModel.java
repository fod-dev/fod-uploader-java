package com.fortify.fod.fodapi.models;

public class ReleaseModel {

	private ReleaseInfo[] data;
	private int responseCode;
	private int count;
	
	
	public ReleaseInfo[] getData() {
		return data;
	}
	public void setData(ReleaseInfo[] data) {
		this.data = data;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
