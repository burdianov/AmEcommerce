package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.jobs.UploadAvatarJob;

import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AccountModel extends AbstractModel {

    private BehaviorSubject<UserInfoDto> mUserInfoSbj = BehaviorSubject.create();

    public AccountModel() {
        mUserInfoSbj.onNext(getUserProfileInfo());
    }

    //region ==================== Addresses ===================

    public Observable<UserAddressDto> getAddressObs() {
        return Observable.from(mDataManager.getUserAddresses());
    }

    public void updateOrInsertAddress(UserAddressDto addressDto) {
        mDataManager.updateOrInsertAddress(addressDto);
    }

    public void removeAddress(UserAddressDto addressDto) {
        mDataManager.removeAddress(addressDto);
    }

    public UserAddressDto getAddressFromPosition(int position) {
        return mDataManager.getUserAddresses().get(position);
    }

    //endregion

    //region ==================== Settings ===================

    public Observable<UserSettingsDto> getUserSettingsObs() {
        return Observable.just(getUserSettings());
    }

    private UserSettingsDto getUserSettings() {
        return mDataManager.getUserSettings();
    }

    public void saveSettings(UserSettingsDto settings) {
        mDataManager.saveSettings(settings);
    }

    //endregion

    //region ==================== User ===================

    public void saveUserProfileInfo(UserInfoDto userInfo) {
        mDataManager.saveProfileInfo(userInfo);
        mUserInfoSbj.onNext(userInfo);

        String uriAvatar = userInfo.getAvatar();
        if (!uriAvatar.contains("http")) {
            uploadAvatarToServer(uriAvatar);
        }
    }

    public UserInfoDto getUserProfileInfo() {
        return mDataManager.getUserProfileInfo();
    }

    public Observable<UserInfoDto> getUserInfoSbj() {
        return mUserInfoSbj;
    }

    private void uploadAvatarToServer(String imageUri) {
        mJobManager.addJobInBackground(new UploadAvatarJob(imageUri));
    }

    //endregion

    public Observable<Integer> getProductCountSbj() {
        RealmResults<ProductRealm> graterThanZeroProductCount = mDataManager.getProductInCart();
        int count = graterThanZeroProductCount.size();
        final BehaviorSubject<Integer> sbj = BehaviorSubject.create(count);
        graterThanZeroProductCount.addChangeListener(element -> sbj.onNext(element.size()));
        return sbj;
    }

    public void updateUserInfo() {
        mUserInfoSbj.onNext(getUserProfileInfo());
    }
}
