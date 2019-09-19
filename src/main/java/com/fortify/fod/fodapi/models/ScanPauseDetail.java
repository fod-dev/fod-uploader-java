package com.fortify.fod.fodapi.models;

public class ScanPauseDetail {
    private String pausedOn = "";
    private String reason = "";
    private String notes = "";

    public String getPausedOn() {
        return pausedOn;
    }

    public String getReason() {
        return reason;
    }

    public String getNotes() {
        return notes;
    }
}