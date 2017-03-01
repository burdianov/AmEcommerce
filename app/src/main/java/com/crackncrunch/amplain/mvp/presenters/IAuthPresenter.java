package com.crackncrunch.amplain.mvp.presenters;

/**
 * Created by Lilian on 19-Feb-17.
 */

public interface IAuthPresenter {

    void clickOnLogin();
    void clickOnFb();
    void clickOnVk();
    void clickOnTwitter();
    void clickOnShowCatalog();

    boolean checkUserAuth();
}
