package com.crackncrunch.amplain.data.storage.dto;

public class FbDataDto {
    private String accessToken;
    private String userId;

    public FbDataDto(String accessToken, String userId) {
        this.accessToken = accessToken;
        this.userId=userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }
}
