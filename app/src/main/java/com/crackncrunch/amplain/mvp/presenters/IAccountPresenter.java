package com.crackncrunch.amplain.mvp.presenters;

public interface IAccountPresenter {
    void onClickAddress();
    void switchViewState();

    void takePhoto();
    void chooseCamera();
    void chooseGallery();

    void removeAddress(int position);
    void editAddress(int position);
}
