package com.crackncrunch.amplain.mvp.views;

/**
 * Created by Lilian on 19-Feb-17.
 */

public interface IAuthView extends IView {
    void setTypeface();
    void showLoginBtn();
    void hideLoginBtn();

    String getUserEmail();
    String getUserPassword();

    boolean isIdle();
}
