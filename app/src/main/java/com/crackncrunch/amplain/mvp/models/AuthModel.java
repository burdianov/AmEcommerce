package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.dto.FbDataDto;
import com.crackncrunch.amplain.data.storage.dto.VkDataDto;

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

    public Observable<UserRes> signInVk(String accessToken, String userId,
                                        String email) {
        return mDataManager.signInVk(new VkDataDto(accessToken, userId, email));
    }

    public String getUserFullName() {
        return mDataManager.getUserFullName();
    }

    public Observable<UserRes> signInFb(String accessToken, String userId) {
        return mDataManager.signInFb(new FbDataDto(accessToken, userId));
    }
}