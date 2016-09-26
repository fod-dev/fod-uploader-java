package com.fortify.fod.legacy.models;

import com.fortify.fod.fodapi.models.ReleaseDTO;

public class ReleaseModel {

	private ReleaseDTO[] data;
	private int responseCode;
	private int count;
	
	
	public ReleaseDTO[] getData() {
		return data;
	}
	public void setData(ReleaseDTO[] data) {
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
