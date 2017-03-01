package com.crackncrunch.amplain.mvp.views;

import android.support.annotation.Nullable;

import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;

/**
 * Created by Lilian on 21-Feb-17.
 */
public interface IRootView extends IView {
    void showMessage(String message);
    void showError(Throwable e);

    void showLoad();
    void hideLoad();

    @Nullable
    IView getCurrentScreen();

    void initDrawer(UserInfoDto userInfoDto);
}
