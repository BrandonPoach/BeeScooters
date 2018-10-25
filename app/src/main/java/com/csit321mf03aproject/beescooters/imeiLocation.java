package com.csit321mf03aproject.beescooters;

public class imeiLocation {
    String code, message;
    imeiResult result[];

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public imeiResult[] getResult() {
        return result;
    }

    public void setResult(imeiResult[] result) {
        this.result = result;
    }
}
