package com.fortify.fod.fodapi.models;

import java.util.List;

public class GenericErrorResponse {
    private List<ErrorResponse> errors;

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        String result = "";
        for (ErrorResponse error : errors) result += error.getMessage() + "\n";
        return result;
    }
}

class ErrorResponse {
    private int errorCode;
    private String message;

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}