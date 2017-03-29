package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.network.res.UserRes;

import rx.Observable;

public interface IAuthModel {
    boolean isAuthUser();
    Observable<UserRes> signInUser(String email, String password);
}
