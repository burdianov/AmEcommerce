package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Lilian on 11-Mar-17.
 */

public class UserRes {
    @Json(name = "_id")
    private String id;
    private String fullName;
    private String avatarUrl;
    private String token;
    private String phone;
    private List<UserAddressRes> addresses;

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

    public List<UserAddressRes> getAddresses() {
        return addresses;
    }
}
