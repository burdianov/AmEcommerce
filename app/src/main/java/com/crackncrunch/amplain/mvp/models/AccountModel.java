package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;
import com.crackncrunch.amplain.jobs.UploadAvatarJob;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static com.crackncrunch.amplain.data.managers.PreferencesManager.NOTIFICATION_ORDER_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.NOTIFICATION_PROMO_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_PHONE_KEY;

public class AccountModel extends AbstractModel {

    private BehaviorSubject<UserInfoDto> mUserInfoObs = BehaviorSubject.create();

    public AccountModel() {
        mUserInfoObs.onNext(getUserProfileInfo());
    }

    //region ==================== Addresses ===================

    public Observable<UserAddressDto> getAddressObs() {
        return Observable.from(getUserAddresses());
    }

    private List<UserAddressDto> getUserAddresses() {
        return mDataManager.getUserAddresses();
    }

    public void updateOrInsertAddress(UserAddressDto addressDto) {
        mDataManager.updateOrInsertAddress(addressDto);
    }

    public void removeAddress(UserAddressDto addressDto) {
        mDataManager.removeAddress(addressDto);
    }

    public UserAddressDto getAddressFromPosition(int position) {
        return getUserAddresses().get(position);
    }

    //endregion

    //region ==================== Settings ===================

    public Observable<UserSettingsDto> getUserSettingsObs() {
        return Observable.just(getUserSettings());
    }

    private UserSettingsDto getUserSettings() {
        Map<String, Boolean> map = mDataManager.getUserSettings();
        return new UserSettingsDto(map.get(NOTIFICATION_ORDER_KEY), map.get
                (NOTIFICATION_PROMO_KEY));
    }

    public void saveSettings(UserSettingsDto settings) {
        mDataManager.saveSetting(NOTIFICATION_ORDER_KEY, settings.isOrderNotification());
        mDataManager.saveSetting(NOTIFICATION_PROMO_KEY, settings.isPromoNotification());
    }

    //endregion

    //region ==================== User ===================

    public void saveUserProfileInfo(UserInfoDto userInfo) {
        mDataManager.saveUserProfileInfo(userInfo.getName(), userInfo.getPhone(),
                userInfo.getAvatar());
        mUserInfoObs.onNext(userInfo);

        String uriAvatar = userInfo.getAvatar();
        if (!uriAvatar.contains("http")) {
            uploadAvatarToServer(uriAvatar);
        }
    }

    public UserInfoDto getUserProfileInfo() {
        Map<String, String> map = mDataManager.getUserProfileInfo();
        return new UserInfoDto(
                map.get(PROFILE_FULL_NAME_KEY),
                map.get(PROFILE_PHONE_KEY),
                map.get(PROFILE_AVATAR_KEY));
    }

    public Observable<UserInfoDto> getUserInfoObs() {
        return mUserInfoObs;
    }

    private void uploadAvatarToServer(String imageUri) {
        mJobManager.addJobInBackground(new UploadAvatarJob(imageUri));
    }

    //endregion
}
