package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

public class FbProfileRes {
    public String email;
    public FbPicture picture;
    @Json(name = "first_name")
    public String firstName;
    @Json(name = "last_name")
    public String lastName;
    public String id;

    private static class FbPicture {

        public  FbPictureData data;

        private static class FbPictureData{
            public int height;
            @Json(name = "is_silhouette")
            public boolean isSilhouette;
            public String url;
            public int width;
        }
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return picture.data.url;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
