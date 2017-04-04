package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class VkProfileRes {
    private List<VkResponse> response = new ArrayList<>();

    private static class VkResponse {
        public int uid;
        @Json(name = "first_name")
        String firstName;
        @Json(name = "last_name")
        String lastName;
        @Json(name = "photo_200")
        String photo200;
        @Json(name = "mobile_phone")
        String mobilePhone;
        @Json(name = "home_phone")
        String homePhone;
    }

    public String getFirstName() {
        return response.get(0).firstName;
    }

    public String getLastName() {
        return response.get(0).lastName;
    }

    public String getFullName() {
        return response.get(0).firstName + " " + response.get(0).lastName;
    }

    public String getPhone() {
        return response.get(0).mobilePhone;
    }

    public String getAvatar() {
        return response.get(0).photo200;
    }
}
