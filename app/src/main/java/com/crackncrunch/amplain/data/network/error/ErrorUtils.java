package com.crackncrunch.amplain.data.network.error;

import retrofit2.Response;

public class ErrorUtils {
    public static ApiError parseError(Response<?> response) {
        // TODO: 21.03.2017 correct parse error (without retrofit dependency)
        return new ApiError(response.code());
    }
}
