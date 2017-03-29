package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.UserRes;

import rx.Observable;

/**
 * Created by Lilian on 19-Feb-17.
 */

public class AuthModel extends AbstractModel implements IAuthModel {

    public AuthModel() {
    }

    public boolean isAuthUser() {
        return mDataManager.getPreferencesManager().getAuthToken() != null;
    }

    public Observable<UserRes> signInUser(String email, String password) {
        return mDataManager.signInUser(new UserLoginReq(email, password));
    }
}
