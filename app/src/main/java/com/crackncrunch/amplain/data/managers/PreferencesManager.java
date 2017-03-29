package com.crackncrunch.amplain.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class PreferencesManager {
    static final String PROFILE_USER_ID_KEY = "PROFILE_USER_ID_KEY";
    static final String PROFILE_FULL_NAME_KEY = "PROFILE_FULL_NAME_KEY";
    static final String PROFILE_AVATAR_KEY = "PROFILE_AVATAR_KEY";
    static final String PROFILE_PHONE_KEY = "PROFILE_PHONE_KEY";
    static final String PROFILE_AUTH_TOKEN_KEY = "PROFILE_AUTH_TOKEN_KEY";
    static final String NOTIFICATION_ORDER_KEY = "NOTIFICATION_ORDER_KEY";
    static final String NOTIFICATION_PROMO_KEY = "NOTIFICATION_PROMO_KEY";

    static final String PRODUCT_LAST_UPDATE_KEY = "PRODUCT_LAST_UPDATE_KEY";
    static final String USER_ADDRESSES_KEY = "USER_ADDRESSES_KEY";
    static final String MOCK_PRODUCT_LIST = "MOCK_PRODUCT_LIST";

    static final String BASKET_COUNT_KEY = "BASKET_COUNT_KEY";

    private final SharedPreferences mSharedPreferences;
    private JsonAdapter<UserAddressDto> jsonAdapter;

    public PreferencesManager(Context context) {
        mSharedPreferences = context.getSharedPreferences("crackncrunch",
                Context.MODE_PRIVATE);
        Moshi moshi = new Moshi.Builder()
                .build();
        jsonAdapter = moshi.adapter(UserAddressDto.class);
    }

    //region ==================== User Authentication ===================

    public String getAuthToken() {
        return mSharedPreferences.getString(PROFILE_AUTH_TOKEN_KEY, null);
    }

    public boolean isUserAuth() {
        return mSharedPreferences.getString(PROFILE_AUTH_TOKEN_KEY, null) != null;
    }

    //endregion

    //region ==================== User Profile Info ===================

    public void saveProfileInfo(@NonNull UserInfoDto userInfoDto) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_FULL_NAME_KEY, userInfoDto.getName());
        editor.putString(PROFILE_AVATAR_KEY, userInfoDto.getAvatar());
        editor.putString(PROFILE_PHONE_KEY, userInfoDto.getPhone());
        editor.apply();
    }

    public void saveProfileInfo(@NonNull UserRes userRes) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_USER_ID_KEY, userRes.getId());
        editor.putString(PROFILE_AUTH_TOKEN_KEY, userRes.getToken());
        editor.putString(PROFILE_FULL_NAME_KEY, userRes.getFullName());
        editor.putString(PROFILE_AVATAR_KEY, userRes.getAvatarUrl());
        String phone = "unspecified";
        if (userRes.getPhone() != null) {
            phone = userRes.getPhone();
        }
        editor.putString(PROFILE_PHONE_KEY, phone);
        editor.apply();
    }

    @Nullable
    public UserInfoDto getUserProfileInfo() {
        String fullName = mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null);
        String phone = mSharedPreferences.getString(PROFILE_PHONE_KEY, null);
        String avatarUrl = mSharedPreferences.getString(PROFILE_AVATAR_KEY, null);
        return new UserInfoDto(fullName, phone, avatarUrl);
    }

    public void saveUserAvatar(@NonNull String avatarUrl) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_AVATAR_KEY, avatarUrl);
        editor.apply();
    }

    @NonNull
    public String getUserName() {
        return mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, "Unknown");
    }

    @Nullable
    public String getUserAvatar() {
        return mSharedPreferences.getString(PROFILE_AVATAR_KEY, null);
    }

    //endregion

    //region ==================== Addresses ===================

    public List<UserAddressDto> getUserAddresses() {
        Set<String> setJson = mSharedPreferences.getStringSet(USER_ADDRESSES_KEY, new HashSet<String>());
        ArrayList<String> listJson = new ArrayList<String>(setJson);
        ArrayList<UserAddressDto> arrayAddress = new ArrayList<UserAddressDto>();

        for (int i = 0; i < listJson.size(); i++) {
            try {
                arrayAddress.add(jsonAdapter.fromJson(listJson.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayAddress;
    }

    public void removeAddress(UserAddressDto userAddressDto) {
        List<UserAddressDto> arrayList = getUserAddresses();

        Iterator<UserAddressDto> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            UserAddressDto entry = iterator.next();
            if (entry.getId() == userAddressDto.getId()) {
                iterator.remove();
                break;
            }
        }
        arrayList.remove(userAddressDto);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < arrayList.size(); i++) {
            setJson.add(jsonAdapter.toJson(arrayList.get(i)));
        }
        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

    public void addUserAddress(UserAddressDto address) {
        List<UserAddressDto> list = getUserAddresses();
        if (address.getId() == 0) {
            address.setId(list.size() + 1);
        }
        list.add(address);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            setJson.add(jsonAdapter.toJson(list.get(i)));
        }
        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

    public void updateUserAddress(UserAddressDto userAddressDto) {
        List<UserAddressDto> list = getUserAddresses();
        for (UserAddressDto address : list) {
            if (address.getId() == userAddressDto.getId()) {
                address.update(userAddressDto);
                break;
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            setJson.add(jsonAdapter.toJson(list.get(i)));
        }
        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

    //endregion

    //region ==================== User Settings ===================

    public UserSettingsDto getUserSettings() {
        boolean isOrder = mSharedPreferences.getBoolean(NOTIFICATION_ORDER_KEY, false);
        boolean isPromo = mSharedPreferences.getBoolean(NOTIFICATION_PROMO_KEY, false);
        return new UserSettingsDto(isOrder, isPromo);
    }

    void saveUserSettings(UserSettingsDto settings) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_ORDER_KEY, settings.isOrderNotification());
        editor.putBoolean(NOTIFICATION_PROMO_KEY, settings.isPromoNotification());
        editor.apply();
    }

    //endregion

    //region ==================== Products ===================

    public String getLastProductUpdate() {
        return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY,
                ConstantsManager.UNIX_EPOCH_TIME);
    }

    public void saveLastProductUpdate(String lastModified) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PRODUCT_LAST_UPDATE_KEY, lastModified);
        editor.apply();
    }

    public List<ProductDto> getProductList() {
        String products = mSharedPreferences.getString(MOCK_PRODUCT_LIST, null);
        if (products != null) {
            Gson gson = new Gson();
            ProductDto[] productList = gson.fromJson(products, ProductDto[].class);
            return Arrays.asList(productList);
        }
        return null;
    }

    //endregion

    //region ======================== Basket ========================

    public void saveBasketCounter(int count) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(BASKET_COUNT_KEY, count);
        editor.apply();
    }

    public int getBasketCounter() {
        return mSharedPreferences.getInt(BASKET_COUNT_KEY, 0);
    }

    //endregion
}
