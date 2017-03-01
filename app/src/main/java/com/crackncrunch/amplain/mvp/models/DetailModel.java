package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.jobs.SendMessageJob;

public class DetailModel extends AbstractModel {

    public void sendComment(String id, CommentRealm commentRealm) {
        SendMessageJob job = new SendMessageJob(id, commentRealm);
        mJobManager.addJobInBackground(job);
    }
}
