package com.crackncrunch.amplain.data.managers;

import android.content.Context;
import android.util.Log;

import com.crackncrunch.amplain.App;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.network.RestCallTransformer;
import com.crackncrunch.amplain.data.network.RestService;
import com.crackncrunch.amplain.data.network.res.AvatarUrlRes;
import com.crackncrunch.amplain.data.network.res.CommentRes;
import com.crackncrunch.amplain.data.network.res.ProductRes;
import com.crackncrunch.amplain.data.storage.dto.CommentDto;
import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.DaggerDataManagerComponent;
import com.crackncrunch.amplain.di.components.DataManagerComponent;
import com.crackncrunch.amplain.di.modules.LocalModule;
import com.crackncrunch.amplain.di.modules.NetworkModule;
import com.crackncrunch.amplain.utils.AppConfig;
import com.crackncrunch.amplain.utils.NetworkStatusChecker;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static com.crackncrunch.amplain.data.managers.PreferencesManager.PROFILE_PHONE_KEY;

/**
 * Created by Lilian on 20-Feb-17.
 */

public class DataManager {

    private static DataManager sInstance = new DataManager();
    public static final String TAG = "DataManager";

    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RestService mRestService;
    @Inject
    Context mContext;
    @Inject
    Retrofit mRetrofit;
    @Inject
    RealmManager mRealmManager;

    private Map<String, String> mUserProfileInfo;
    private List<UserAddressDto> mUserAddresses;
    private Map<String, Boolean> mUserSettings;

    private DataManager() {
        DataManagerComponent component = DaggerService.getComponent
                (DataManagerComponent.class);
        if (component == null) {
            component = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, component);
        }
        component.inject(this);

        generateProductsMockData();
        initUserProfileData();
        initUserSettingsData();
        initAddressData();

        updateLocalDataWithTimer();
    }

    public static DataManager getInstance() {
        return sInstance;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    //region ==================== User Authentication ===================

    public void loginUser(String email, String password) {
        // TODO: 23-Oct-16 implement user authentication
    }

    public boolean isAuthUser() {
        // TODO: 20-Feb-17 Check User auth token in SharedPreferences
        return true;
    }

    //endregion

    //region ==================== User Profile ===================

    private void initUserProfileData() {
        mUserProfileInfo = new HashMap<>();

        mUserProfileInfo = mPreferencesManager.getUserProfileInfo();
        if (mUserProfileInfo.get(PROFILE_FULL_NAME_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, "Hulk Hogan");
        }
        if (mUserProfileInfo.get(PROFILE_AVATAR_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_AVATAR_KEY,
                    "http://a1.files.biography.com/image/upload/c_fill,cs_srgb," +
                            "dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjM0MDQyNzQ2Mzgw.jpg");
        }
        if (mUserProfileInfo.get(PROFILE_PHONE_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_PHONE_KEY, "+7(917)971-38-27");
        }
    }

    public Map<String, String> getUserProfileInfo() {
        return mUserProfileInfo;
    }

    public void saveUserProfileInfo(String name, String phone, String avatar) {
        mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, name);
        mUserProfileInfo.put(PROFILE_PHONE_KEY, phone);
        mUserProfileInfo.put(PROFILE_AVATAR_KEY, avatar);
        mPreferencesManager.saveProfileInfo(mUserProfileInfo);
    }

    public Observable<AvatarUrlRes> uploadUserPhoto(MultipartBody.Part body) {
        return mRestService.uploadUserAvatar(body);
    }

    //endregion

    //region ==================== Addresses ===================

    private void initAddressData() {
        List<UserAddressDto> userAddresses = getPreferencesManager().getUserAddresses();
        mUserAddresses = new ArrayList<>();

        if (userAddresses == null) {
            UserAddressDto userAddress;
            userAddress = new UserAddressDto("1", "Home", "Airport Road",
                    "24", "56", 9, "Beware of crazy dogs");
            mUserAddresses.add(userAddress);

            userAddress = new UserAddressDto("2", "Work", "Central Park",
                    "123", "67", 2, "In the middle of nowhere");
            mUserAddresses.add(userAddress);
        } else {
            mUserAddresses = userAddresses;
        }
    }

    public List<UserAddressDto> getUserAddresses() {
        return mUserAddresses;
    }

    public void updateOrInsertAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.set(mUserAddresses.indexOf(addressDto), addressDto);
        } else {
            mUserAddresses.add(0, addressDto);
        }
        mPreferencesManager.saveUserAddresses(mUserAddresses);
    }

    public void removeAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.remove(mUserAddresses.indexOf(addressDto));
            mPreferencesManager.saveUserAddresses(mUserAddresses);
        }
    }

    //endregion

    //region ==================== User Settings ===================

    private void initUserSettingsData() {
        mUserSettings = mPreferencesManager.getUserSettings();
    }

    public Map<String, Boolean> getUserSettings() {
        return mUserSettings;
    }

    public void saveSetting(String notificationKey, boolean isChecked) {
        mPreferencesManager.saveSetting(notificationKey, isChecked);
        mUserSettings.put(notificationKey, isChecked);
    }

    //endregion

    //region ==================== Products ===================

    private List<ProductDto> generateProductsMockData() {
        List<ProductDto> productDtoList = getPreferencesManager().getProductList();
        List<CommentDto> commentList = new ArrayList<>();

        if (productDtoList == null) {
            productDtoList = new ArrayList<>();

            productDtoList.add(new ProductDto(1,
                    getResVal(R.string.product_name_1),
                    getResVal(R.string.product_url_1),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(2,
                    getResVal(R.string.product_name_2),
                    getResVal(R.string.product_url_2),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(3,
                    getResVal(R.string.product_name_3),
                    getResVal(R.string.product_url_3),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(4,
                    getResVal(R.string.product_name_4),
                    getResVal(R.string.product_url_4),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(5,
                    getResVal(R.string.product_name_5),
                    getResVal(R.string.product_url_5),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(6,
                    getResVal(R.string.product_name_6),
                    getResVal(R.string.product_url_6),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(7,
                    getResVal(R.string.product_name_7),
                    getResVal(R.string.product_url_7),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(8,
                    getResVal(R.string.product_name_8),
                    getResVal(R.string.product_url_8),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(9,
                    getResVal(R.string.product_name_9),
                    getResVal(R.string.product_url_9),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(10,
                    getResVal(R.string.product_name_10),
                    getResVal(R.string.product_url_10),
                    getResVal(R.string.lorem_ipsum), 100, 1, false, commentList));
        }
        return productDtoList;
    }

    private void updateLocalDataWithTimer() {
        Log.e(TAG, "LOCAL UPDATE start : " + new Date());
        Observable.interval(AppConfig.UPDATE_DATA_INTERVAL, TimeUnit.SECONDS) // генерируем последовательность испускающую элементы каждые 30 секунд
                .flatMap(aLong -> NetworkStatusChecker.isInternetAvailable()) // проверяем состояние сети
                .filter(aBoolean -> aBoolean) // только если сеть доступна запрашиваем данные из сети
                .flatMap(aBoolean -> getProductsObsFromNetwork()) // запрашиваем данные из сети
                .subscribe(productRealm -> {
                    Log.e(TAG, "LOCAL UPDATE complete: ");
                }, throwable -> {
                    throwable.printStackTrace();
                    Log.e(TAG, "LOCAL UPDATE error: " + throwable.getMessage());
                });
    }

    @RxLogObservable
    public Observable<ProductRealm> getProductsObsFromNetwork() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(new RestCallTransformer<List<ProductRes>>()) // трансформируем response, выбрасываем ApiError в случае ошибки
                .flatMap(Observable::from) // преобразуем список товаров в последовательность товаров
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(productRes -> {
                    if (!productRes.isActive()) {
                        mRealmManager.deleteFromRealm(ProductRealm.class,
                                productRes.getId()); // удалить из базы данных если не активен
                    }
                })
                .distinct(ProductRes::getRemoteId)
                .filter(ProductRes::isActive) // пропускаем дальше только активные товары
                .doOnNext(productRes -> mRealmManager.saveProductResponseToRealm
                        (productRes)) // сохраняем на диск только активные товары
                .retryWhen(errorObservable ->
                        errorObservable.zipWith(Observable.range(1,
                                AppConfig.RETRY_REQUEST_COUNT),
                                (throwable, retryCount) -> retryCount) // генерируем последовательность чисел от 1 до 5 (число повторений запроса)
                                .doOnNext(retryCount -> Log.e(TAG, "LOCAL UPDATE request retry " +
                                        "count: " + retryCount + " " + new Date()))
                                .map(retryCount ->
                                        ((long) (AppConfig.RETRY_REQUEST_BASE_DELAY * Math
                                                .pow(Math.E, retryCount)))) // расчитываем экспоненциальную задержку
                                .doOnNext(delay -> Log.e(TAG, "LOCAL UPDATE delay: " +
                                        delay))
                                .flatMap(delay -> Observable.timer(delay,
                                        TimeUnit.MILLISECONDS)) // создаем и возвращаем задержку в миллисекундах
                )
                .flatMap(productRes -> Observable.empty());
    }

    public Observable<ProductRealm> getProductFromRealm() {
        return mRealmManager.getAllProductsFromRealm();
    }

    //endregion

    //region ==================== Comments ===================

    public Observable<CommentRes> sendComment(String productId, CommentRes comment) {
        return mRestService.sendComment(productId, comment);
    }

    //endregion

    private String getResVal(int resourceId) {
        return mContext.getString(resourceId);
    }
}
