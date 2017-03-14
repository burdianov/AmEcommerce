package com.crackncrunch.amplain.mvp.models;

import com.birbit.android.jobqueue.JobManager;
import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.UserRes;

import rx.Observable;

/**
 * Created by Lilian on 19-Feb-17.
 */

public class AuthModel extends AbstractModel {

    public AuthModel() {
    }

    // for tests
    public AuthModel(DataManager dataManager, JobManager jobManager) {
        super(dataManager, jobManager);
    }

    public boolean isAuthUser() {
        return mDataManager.isAuthUser();
    }

    public Observable<UserRes> loginUser(String email, String password) {
        return mDataManager.loginUser(new UserLoginReq(email, password));
    }
}
