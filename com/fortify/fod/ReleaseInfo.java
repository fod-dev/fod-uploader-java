package com.fortify.fod;

public class ReleaseInfo {

	private int status;
	private boolean isPassed;
	private int passedFailReasonId;
	private int critical;
	private int high;
	private int medium;
	private int low;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isPassed() {
		return isPassed;
	}
	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}
	public int getPassedFailReasonId() {
		return passedFailReasonId;
	}
	public void setPassedFailReasonId(int passedFailReasonId) {
		this.passedFailReasonId = passedFailReasonId;
	}
	public int getCritical() {
		return critical;
	}
	public void setCritical(int critical) {
		this.critical = critical;
	}
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}
	public int getMedium() {
		return medium;
	}
	public void setMedium(int medium) {
		this.medium = medium;
	}
	public int getLow() {
		return low;
	}
	public void setLow(int low) {
		this.low = low;
	}
	
}
