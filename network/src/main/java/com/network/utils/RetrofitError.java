package com.network.utils;

/**
 * @author yogi
 */

public class RetrofitError {
    private int statusCode;
    private String message;

    public RetrofitError() {
    }

    public RetrofitError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return this != null ? "status code : "+this.statusCode + " Message : "+this.message : "NULL";
    }
}
