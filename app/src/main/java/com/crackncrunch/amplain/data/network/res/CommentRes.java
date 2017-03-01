package com.crackncrunch.amplain.data.network.res;

import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.squareup.moshi.Json;

import java.util.Date;

public class CommentRes {
    @Json(name = "_id")
    private String id;
    private int remoteId;
    private String avatar;
    private String userName;
    @Json(name = "raiting")
    private float rating;
    private Date commentDate;
    private String comment;
    private boolean active;

    public CommentRes(String id, int remoteId, String avatar, String userName,
                      float rating, Date commentDate, String comment,
                      boolean active) {
        this.id = id;
        this.remoteId = remoteId;
        this.avatar = avatar;
        this.userName = userName;
        this.rating = rating;
        this.commentDate = commentDate;
        this.comment = comment;
        this.active = active;
    }

    public CommentRes(CommentRealm comment) {
        this.avatar = comment.getAvatar();
        this.userName = comment.getUserName();
        this.rating = comment.getRating();
        this.commentDate = comment.getCommentDate();
        this.comment = comment.getComment();
        this.active = comment.isActive();
    }

    public String getId() {
        return id;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }

    public boolean isActive() {
        return active;
    }
}
