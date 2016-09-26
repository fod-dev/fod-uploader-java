package com.fortify.fod.fodapi.models;

public class ReleaseDTO {
    private int critical;
    private int high;
    private int medium;
    private int low;
	private int status;
	private boolean isPassed;
	private int passedFailReasonId;

	
	public int getStatus() {
		return status;
	}
	public boolean isPassed() {
		return isPassed;
	}
	public int getPassedFailReasonId() {
		return passedFailReasonId;
	}
	public int getCritical() {
		return critical;
	}
	public int getHigh() {
		return high;
	}
	public int getMedium() {
		return medium;
	}
	public int getLow() {
		return low;
	}
}
