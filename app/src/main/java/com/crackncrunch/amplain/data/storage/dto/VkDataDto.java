package com.crackncrunch.amplain.data.storage.dto;


public class VkDataDto {
    private String accessToken;
    private String userId;
    private String email;

    public VkDataDto(String accessToken, String userId, String email) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
