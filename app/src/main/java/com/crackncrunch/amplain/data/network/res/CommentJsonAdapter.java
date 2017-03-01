package com.crackncrunch.amplain.data.network.res;

import com.crackncrunch.amplain.utils.DateConverter;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.text.ParseException;
import java.util.Date;

public class CommentJsonAdapter {
    @FromJson
    CommentRes fromJson(CommentJson commentJson) {
        try {
            return new CommentRes(
                    commentJson.id,
                    commentJson.remoteId,
                    commentJson.avatar,
                    commentJson.userName,
                    commentJson.rating,
                    DateConverter.stringToDate(commentJson.commentDate),
                    commentJson.comment,
                    commentJson.active
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new CommentRes(
                commentJson.id,
                commentJson.remoteId,
                commentJson.avatar,
                commentJson.userName,
                commentJson.rating,
                new Date(),
                commentJson.comment,
                commentJson.active
        );
    }

    @ToJson
    CommentJson toJson(CommentRes commentRes) {
        CommentJson commentJson = new CommentJson();
        commentJson.id = commentRes.getId();
        commentJson.remoteId = commentRes.getRemoteId();
        commentJson.avatar = commentRes.getAvatar();
        commentJson.userName = commentRes.getUserName();
        commentJson.rating = commentRes.getRating();
        commentJson.commentDate = DateConverter.dateToString(commentRes
                .getCommentDate());
        commentJson.comment = commentRes.getComment();
        commentJson.active = commentRes.isActive();
        return commentJson;
    }
}