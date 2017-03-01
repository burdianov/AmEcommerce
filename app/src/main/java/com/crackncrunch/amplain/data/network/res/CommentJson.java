package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

public class CommentJson {
    @Json(name = "_id")
    public String id;
    public int remoteId;
    public String userName;
    public String avatar;
    @Json(name = "raiting")
    public float rating;
    public String commentDate;
    public String comment;
    public boolean active;
}
