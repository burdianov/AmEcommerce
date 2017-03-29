package com.crackncrunch.amplain.data.network.error;

public class ApiError extends Throwable {
    private int statusCode;

    public ApiError(int statusCode) {
        super("status code : " + statusCode);
        this.statusCode = statusCode;
    }

    public ApiError(String message) {
        super(message);
    }

    public ApiError() {
    }
}
