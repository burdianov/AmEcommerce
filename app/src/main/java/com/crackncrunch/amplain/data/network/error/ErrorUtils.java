package com.crackncrunch.amplain.data.network.error;

import com.crackncrunch.amplain.data.managers.DataManager;

import java.io.IOException;

import retrofit2.Response;

public class ErrorUtils {
    public static ApiError parseError(Response<?> response) {
        ApiError error;

        try {
            error = (ApiError) DataManager.getInstance()
                    .getRetrofit().responseBodyConverter(ApiError.class, ApiError
                            .class.getAnnotations())
                    .convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiError();
        }

        return error;
    }
}
