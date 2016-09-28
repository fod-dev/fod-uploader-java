package com.fortify.fod.legacy.models;

import com.fortify.fod.fodapi.models.ReleaseDTO;

public class ReleaseModel {

	private ReleaseDTO[] data;
	private int responseCode;
	private int count;
	
	
	public ReleaseDTO[] getData() {
		return data;
	}
	public int getResponseCode() {
		return responseCode;
	}
}
