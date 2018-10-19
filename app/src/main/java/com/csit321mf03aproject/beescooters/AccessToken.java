package com.csit321mf03aproject.beescooters;

//class that is part of GPS integration, work in progress for Tradeshow
public class AccessToken {
    private int code;
    private String message;
    private Result result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
