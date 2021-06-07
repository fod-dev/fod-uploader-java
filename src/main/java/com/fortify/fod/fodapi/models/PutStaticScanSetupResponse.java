package com.fortify.fod.fodapi.models;

import java.util.List;

public class PutStaticScanSetupResponse {
    private boolean success;
    private String bsiToken;
    private List<String> errors;
    private List<String> messages;

    public boolean getSuccess() {return success;}
    public String getBsiToken() {return bsiToken;}
    public List<String> getErrors() {
        return errors;
    }
    public List<String> getMessages() {
        return messages;
    }
}
