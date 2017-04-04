package com.crackncrunch.amplain.data.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.crackncrunch.amplain.data.managers.PreferencesManager.BASKET_COUNT_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.NOTIFICATION_ORDER_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.NOTIFICATION_PROMO_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PRODUCT_LAST_UPDATE_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_AUTH_TOKEN_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_PHONE_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_USER_ID_KEY;
import static com.crackncrunch.amplain.utils.ConstantsManager.UNIX_EPOCH_TIME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class PreferencesManagerTest {
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;
    @Mock
    Context mockContext;
    private Map<String, String> fakeStringMap = new HashMap<>();
    private Map<String, Boolean> fakeBooleanMap = new HashMap<>();
    private Map<String, Integer> fakeIntMap = new HashMap<>();

    private HashSet<String> fakeStringSet;
    private PreferencesManager mPreferencesManager;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        given(mockContext.getSharedPreferences(anyString(), anyInt())).willReturn(mockSharedPreferences);
        given(mockSharedPreferences.edit()).willReturn(mockEditor);

        mPreferencesManager = new PreferencesManager(mockContext);

    }

    //region ======================== prepare stub ========================

    private void preparePutBooleanStub() {
        given(mockEditor.putBoolean(anyString(), anyBoolean())).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Boolean value = invocation.getArgument(1);
            fakeBooleanMap.put(key, value);
            return null;
        });

        given(mockSharedPreferences.getBoolean(anyString(), anyBoolean())).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Boolean value = fakeBooleanMap.get(key);
            if (value == null) {
                value = invocation.getArgument(1);
            }
            return value;
        });
    }

    private void preparePutIntStub() {
        given(mockEditor.putInt(anyString(), nullable(Integer.class))).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Integer value = invocation.getArgument(1);
            fakeIntMap.put(key, value);
            return null;
        });

        given(mockSharedPreferences.getInt(anyString(), nullable(Integer.class))).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Integer value = fakeIntMap.get(key);
            if (value == null) {
                value = invocation.getArgument(1);
            }
            return value;
        });
    }

    private void preparePutStringSetStub() {
        given(mockEditor.putStringSet(anyString(), nullable(Set.class))).willAnswer(invocation -> {
            fakeStringSet = invocation.getArgument(1);
            return null;
        });

        given(mockSharedPreferences.getStringSet(anyString(), nullable(Set.class))).willAnswer(invocation -> {
            HashSet<String> value = fakeStringSet;
            if (value == null) {
                value = invocation.getArgument(1);
            }
            return value;
        });
    }

    private void preparePutStringStub() {
        given(mockEditor.putString(anyString(), nullable(String.class))).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            fakeStringSet = new HashSet<String>();
            fakeStringMap.put(key, value);
            return null;
        });

        given(mockSharedPreferences.getString(anyString(), nullable(String.class))).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = fakeStringMap.get(key);
            if (value == null) {
                value = invocation.getArgument(1);
            }
            return value;
        });
    }
    //endregion

    //region ======================== product update ========================


    @Test
    public void getLastProductUpdate_updateNotExist_DEFAULT_VALUE_DEFAULT_LAST_UPDATE() throws Exception {
        //given
        preparePutStringStub();

        //when
        String actualLastUpdate = mPreferencesManager.getLastProductUpdate();

        //then
        assertEquals(UNIX_EPOCH_TIME, actualLastUpdate);
    }

    @Test
    public void getLastProductUpdate_updateExist_LAST_UPDATE_EQ_EXPECTED_DATE() throws Exception {
        //given
        preparePutStringStub();
        String expectedDate = "Sun Jan 19 2017 14:00:00 GMT+0000 (UTC)";
        mockEditor.putString(PRODUCT_LAST_UPDATE_KEY, expectedDate);

        //when
        String actualLastUpdate = mPreferencesManager.getLastProductUpdate();

        //then
        assertEquals(expectedDate, actualLastUpdate);
    }

    //endregion

    //region ======================== token ========================

    @Test
    public void getAuthToken_tokenNotExist_DEFAULT_VALUE_NULL() throws Exception {
        //given
        preparePutStringStub();

        //when
        String actualToken = mPreferencesManager.getAuthToken();

        //then
        assertNull(actualToken);
    }

    @Test
    public void getAuthToken_tokenExist_USER_TOKEN_EQ_EXPECTED_TOKEN() throws Exception {
        //given
        preparePutStringStub();
        String expectedToken = "joihrtgbowithb[owithbwrhnoi9230840293rjk3p4oktjki-054t-gkr,g";
        mockEditor.putString(PROFILE_AUTH_TOKEN_KEY, expectedToken);

        //when
        String actualToken = mPreferencesManager.getAuthToken();

        //then
        assertEquals(expectedToken, actualToken);
    }

    //endregion

    //region ======================== isAuth ========================

    @Test
    public void isUserAuth_tokenNotExist_DEFAULT_VALUE_FALSE() throws Exception {
        //given
        preparePutBooleanStub();

        //when
        boolean actualResult = mPreferencesManager.isUserAuth();

        //then
        assertFalse(actualResult);
    }

    @Test
    public void isUserAuth_tokenExist_RESULT_TRUE() throws Exception {
        //given
        preparePutStringStub();
        String expectedToken = "joihrtgbowithb[owithbwrhnoi9230840293rjk3p4oktjki-054t-gkr,g";
        mockEditor.putString("PROFILE_AUTH_TOKEN_KEY", expectedToken);

        //when
        boolean result = mPreferencesManager.isUserAuth();

        //then
        assertTrue(result);
    }
    //endregion

    //region ======================== Profile Info ========================

    @Test
    public void saveProfileInfo_expectedUserDto_EXPECTED_USER_VALUES_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        UserInfoDto expectedUser = new UserInfoDto("Lilian Burdianov",
                "89179716463", "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg");

        //when
        mPreferencesManager.saveProfileInfo(expectedUser);

        //then
        then(mockEditor).should(times(1)).apply();
        assertEquals(expectedUser.getName(), mockSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null));
        assertEquals(expectedUser.getPhone(), mockSharedPreferences.getString(PROFILE_PHONE_KEY, null));
        assertEquals(expectedUser.getAvatar(), mockSharedPreferences.getString(PROFILE_AVATAR_KEY, null));
    }

    @Test
    public void saveProfileInfo_expectedUserRes_EXPECTED_USER_VALUES_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        UserRes expectedUser = new UserRes("58711631a242690011b1b26d",
                "Lilian Burdianov", "https://pp.userapi" +
                ".com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg", "token", "89179716463", null);

        //when
        mPreferencesManager.saveProfileInfo(expectedUser);

        //then
        then(mockEditor).should(times(1)).apply();
        assertEquals(expectedUser.getId(), mockSharedPreferences.getString(PROFILE_USER_ID_KEY, null));
        assertEquals(expectedUser.getFullName(), mockSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null));
        assertEquals(expectedUser.getPhone(), mockSharedPreferences.getString(PROFILE_PHONE_KEY, null));
        assertEquals(expectedUser.getAvatarUrl(), mockSharedPreferences.getString(PROFILE_AVATAR_KEY, null));
        assertEquals(expectedUser.getToken(), mockSharedPreferences.getString(PROFILE_AUTH_TOKEN_KEY, null));
    }

    @Test
    public void getUserProfileInfo_userExist_EXPECTED_USER_VALUES_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        UserInfoDto user = new UserInfoDto("Lilian Burdianov", "89179716463",
                "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg");

        //when
        mPreferencesManager.saveProfileInfo(user);

        //then
        then(mockEditor).should(times(1)).apply();
        assertNotNull(mPreferencesManager.getUserProfileInfo());
        assertEquals(user.getName(), mockSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null));
        assertEquals(user.getPhone(), mockSharedPreferences.getString(PROFILE_PHONE_KEY, null));
        assertEquals(user.getAvatar(), mockSharedPreferences.getString(PROFILE_AVATAR_KEY, null));
    }

    //endregion

    //region ======================== settings ========================

    @Test
    public void getSettings_notExist_DEFAULT_VALUES_FALSE() throws Exception {
        //given
        preparePutBooleanStub();

        //when
        UserSettingsDto actualSettings = mPreferencesManager.getUserSettings();

        //then
        assertFalse(actualSettings.isPromoNotification());
        assertFalse(actualSettings.isOrderNotification());
    }

    @Test
    public void getSettings_exist_EXPECTED_SETTINGS_FIELDS_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutBooleanStub();
        mockEditor.putBoolean(NOTIFICATION_ORDER_KEY, true);
        mockEditor.putBoolean(NOTIFICATION_PROMO_KEY, false);

        //when
        UserSettingsDto actualSettings = mPreferencesManager.getUserSettings();

        //then
        assertTrue(actualSettings.isOrderNotification());
        assertFalse(actualSettings.isPromoNotification());
    }

    @Test
    public void saveSettings_expectedSettings_EXPECTED_SETTINGS_FIELDS_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutBooleanStub();
        UserSettingsDto testSettings = new UserSettingsDto(true, false);

        //when
        mPreferencesManager.saveUserSettings(testSettings);

        //then
        verify(mockEditor, times(1)).apply();
        assertEquals(testSettings.isOrderNotification(), mockSharedPreferences.getBoolean(NOTIFICATION_ORDER_KEY, false));
        assertEquals(testSettings.isPromoNotification(), mockSharedPreferences.getBoolean(NOTIFICATION_PROMO_KEY, false));
    }

    //endregion

    //region ======================== Avatar ========================

    @Test
    public void getUserAvatar_expectedUrl_EXPECTED_URL_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        String expectedUrl = "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg";
        mockEditor.putString(PROFILE_AVATAR_KEY, expectedUrl);

        //when
        String actualUrl = mPreferencesManager.getUserAvatar();

        //then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void saveUserAvatar_expectedUrl_EXPECTED_URL_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        String expectedUrl = "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg";

        //when
        mPreferencesManager.saveUserAvatar(expectedUrl);

        //then
        verify(mockEditor, times(1)).apply();
        assertEquals(expectedUrl, mockSharedPreferences.getString(PROFILE_AVATAR_KEY, null));

    }
    //endregion

    //region ======================== User name ========================

    @Test
    public void getUserName_expectedName_EXPECTED_NAME_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringStub();
        String expectedName = "Lilian Burdianov";
        mockEditor.putString(PROFILE_FULL_NAME_KEY, expectedName);

        //when
        String actualUserName = mPreferencesManager.getUserName();

        //then
        assertEquals(expectedName, actualUserName);
    }

    //endregion

    //region ======================== Basket ========================

    @Test
    public void getBasketCounter_notExist_DEFAULT_VALUE_0() throws Exception {
        //given
        preparePutIntStub();

        //when
        int actualBasketCounter = mPreferencesManager.getBasketCounter();

        //then
        assertEquals(0, actualBasketCounter);
    }

    @Test
    public void getBasketCounter_expectedCount_EXPECTED_COUNT_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutIntStub();
        int expectedCount = 5;
        mockEditor.putInt(BASKET_COUNT_KEY, expectedCount);

        //when
        int actualCounter = mPreferencesManager.getBasketCounter();

        //then
        assertEquals(expectedCount, actualCounter);
    }

    @Test
    public void saveBasketCounter_expectedCount_EXPECTED_VALUE_EQ_PREFERENCES_VALUE() throws Exception {
        //given
        preparePutIntStub();
        int testCount = 5;

        //when
        mPreferencesManager.saveBasketCounter(testCount);

        //then
        verify(mockEditor, times(1)).apply();
        assertEquals(testCount, mockSharedPreferences.getInt(BASKET_COUNT_KEY, 0));
    }

    //endregion

    //region ======================== Addresses ========================

    @Test
    public void addUserAddress_expectedAddress_TEST_ADDRESSES_EQ_PREFERENCES_VALUES() throws Exception {
        //given
        preparePutStringSetStub();
        UserAddressDto expectedAddress = new UserAddressDto(1, "Работа", "Автостроителей", "53а", "2", 2, "единственный офис на этаже");

        //when
        mPreferencesManager.addUserAddress(expectedAddress);

        //then
        then(mockEditor).should(times(1)).apply();
        assertEquals(1, mPreferencesManager.getUserAddresses().size());
        assertEquals(expectedAddress.getId(), mPreferencesManager
                .getUserAddresses().get(0).getId());
    }

    @Test
    public void getUserAddress_addressesNotExist_RESULT_IS_EMPTY() throws Exception {
        //given
        preparePutStringSetStub();

        //when
        List<UserAddressDto> actualAddress = mPreferencesManager
                .getUserAddresses();

        //then
        assertTrue(actualAddress.isEmpty());
    }

    @Test
    public void getUserAddress_expectedList_EXPECTED_LIST_VALUES_EQ_RESULT_LIST_VALUES() throws Exception {
        //given
        preparePutStringSetStub();
        ArrayList<UserAddressDto> expectedAddressesList = new ArrayList<>();
        expectedAddressesList.add(new UserAddressDto(1, "Работа", "Автостроителей", "53а", "2", 2, "единственный офис на этаже"));
        expectedAddressesList.add(new UserAddressDto(2, "Дом", "Фрунзе", "53а", "2", 2, null));

        for (UserAddressDto userAddressDto : expectedAddressesList) {
            mPreferencesManager.addUserAddress(userAddressDto);
        }

        //when
        List<UserAddressDto> addressList = mPreferencesManager
                .getUserAddresses();

        //then
        for (int i = 0; i < addressList.size(); i++) {
            assertEquals(expectedAddressesList.get(i).getId(), addressList.get(i).getId());
            assertEquals(expectedAddressesList.get(i).getName(), addressList.get(i).getName());
            assertEquals(expectedAddressesList.get(i).getStreet(), addressList.get(i).getStreet());
            assertEquals(expectedAddressesList.get(i).getBuilding(), addressList
                    .get(i).getBuilding());
            assertEquals(expectedAddressesList.get(i).getApartment(), addressList.get(i).getApartment());
            assertEquals(expectedAddressesList.get(i).getFloor(), addressList.get(i).getFloor());
            assertEquals(expectedAddressesList.get(i).getComment(), addressList.get(i).getComment());
        }
    }

    @Test
    public void removeAddress_expectedAddress_RESULT_NOT_EXSIST_EXPECTED_ADDRESS() throws Exception {
        //given
        preparePutStringSetStub();
        ArrayList<UserAddressDto> expectedList = new ArrayList<>();
        expectedList.add(new UserAddressDto(1, "Работа", "Автостроителей", "53а", "2", 2, "единственный офис на этаже"));
        expectedList.add(new UserAddressDto(2, "Дом", "Фрунзе", "53а", "2", 2, null));

        for (UserAddressDto userAddressDto : expectedList) {
            mPreferencesManager.addUserAddress(userAddressDto);
        }

        //when
        mPreferencesManager.removeAddress(new UserAddressDto(1, "Работа", "Автостроителей", "53а", "2", 2, "единственный офис на этаже"));
        List<UserAddressDto> addressList = mPreferencesManager
                .getUserAddresses();

        //then
        then(mockEditor).should(times(expectedList.size() + 1)).apply();
        assertEquals(expectedList.size() - 1, addressList.size());
        for (int i = 0; i < addressList.size(); i++) {
            assertNotEquals(expectedList.get(i).getId(), addressList.get(i).getId());
        }
    }

    @Test
    public void updateUserAddress_expectedAddress_ACTUAL_ADDRESSES_EQ_EXPECTED_ADDRESSES() throws Exception {
        //given
        preparePutStringSetStub();
        ArrayList<UserAddressDto> expectedList = new ArrayList<>();
        expectedList.add(new UserAddressDto(1, "Работа", "Автостроителей", "53а", "2", 2, "единственный офис на этаже"));
        expectedList.add(new UserAddressDto(2, "Дом", "Фрунзе", "53а", "2", 2, null));

        for (UserAddressDto userAddressDto : expectedList) {
            mPreferencesManager.addUserAddress(userAddressDto);
        }

        UserAddressDto expectedAddress = new UserAddressDto(2, "Дом 2", "Жукова", "6", "111", 3, "Домофона нет");

        //when
        mPreferencesManager.updateUserAddress(expectedAddress);
        List<UserAddressDto> addressList = mPreferencesManager
                .getUserAddresses();

        //then
        then(mockEditor).should(times(expectedList.size() + 1)).apply();
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).getId() == expectedAddress.getId()) {
                assertNotEquals(expectedList.get(i).getName(), addressList.get(i).getName());
                assertNotEquals(expectedList.get(i).getStreet(), addressList.get(i).getStreet());
                assertNotEquals(expectedList.get(i).getBuilding(), addressList
                        .get(i).getBuilding());
                assertNotEquals(expectedList.get(i).getApartment(), addressList.get(i).getApartment());
                assertNotEquals(expectedList.get(i).getFloor(), addressList.get(i).getFloor());
                assertNotEquals(expectedList.get(i).getComment(), addressList.get(i).getComment());
            }
        }
    }

    //endregion
}