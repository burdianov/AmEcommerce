package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

import java.util.List;

public class ProductRes {
    @Json(name = "_id")
    private String id;
    private int remoteId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    @Json(name = "raiting")
    private float rating;
    private boolean active;
    private List<CommentRes> comments = null;

    public int getRemoteId() {
        return remoteId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public boolean isActive() {
        return active;
    }

    public String getId() {
        return id;
    }

    public List<CommentRes> getComments() {
        return comments;
    }
}
