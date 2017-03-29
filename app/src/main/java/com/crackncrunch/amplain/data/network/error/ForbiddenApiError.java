package com.crackncrunch.amplain.data.network.error;

public class ForbiddenApiError extends ApiError {
    public ForbiddenApiError() {
        super("Incorrect login or password");
    }
}
