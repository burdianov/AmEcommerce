package com.crackncrunch.amplain.data.network.error;

public class ApiError extends Throwable {
    private int statusCode;
    private String message;

    public ApiError() {
        super("Unknown Server Error");
    }

    public ApiError(int statusCode) {
        super("Server error " + statusCode);
    }
}
