package com.crackncrunch.amplain.data.managers;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.crackncrunch.amplain.App;
import com.crackncrunch.amplain.AppConfig;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.network.RestCallTransformer;
import com.crackncrunch.amplain.data.network.RestService;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.AvatarUrlRes;
import com.crackncrunch.amplain.data.network.res.CommentRes;
import com.crackncrunch.amplain.data.network.res.ProductRes;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.dto.CommentDto;
import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.data.storage.dto.UserSettingsDto;
import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.DaggerDataManagerComponent;
import com.crackncrunch.amplain.di.components.DataManagerComponent;
import com.crackncrunch.amplain.di.modules.LocalModule;
import com.crackncrunch.amplain.di.modules.NetworkModule;
import com.crackncrunch.amplain.utils.NetworkStatusChecker;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.realm.RealmResults;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.annotation.VisibleForTesting.NONE;

/**
 * Created by Lilian on 20-Feb-17.
 */

public class DataManager {

    public static final String TAG = "DataManager";
    private static DataManager sInstance = null;
    private final RestCallTransformer mRestCallTransformer;

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

        mRestCallTransformer = new RestCallTransformer<>();

        updateLocalDataWithTimer();
    }

    @VisibleForTesting(otherwise = NONE)
    DataManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mRestCallTransformer = new RestCallTransformer<>();
        sInstance = this;
    }

    @VisibleForTesting(otherwise = NONE)
    DataManager(RestService restService, PreferencesManager preferencesManager, RealmManager realmManager) {
        mRestService = restService;
        mPreferencesManager = preferencesManager;
        mRealmManager = realmManager;
        mRestCallTransformer = new RestCallTransformer<>();
        mRestCallTransformer.setTestMode();
        sInstance = this;
    }

    public static DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }

    public RealmManager getRealmManager() {
        return mRealmManager;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    //region ==================== User Authentication ===================

    public Observable<UserRes> signInUser(final UserLoginReq loginReq) {
        return mRestService.loginUser(loginReq)
                .compose(((RestCallTransformer<UserRes>) mRestCallTransformer))
                .doOnNext(userRes -> mPreferencesManager.saveProfileInfo(userRes));
    }

    public boolean isAuthUser() {
        // TODO: 20-Feb-17 Check User auth token in SharedPreferences
        return false;
    }

    //endregion

    //region ==================== User Profile ===================

    public UserInfoDto getUserProfileInfo() {
        return mPreferencesManager.getUserProfileInfo();
    }

    public void saveProfileInfo(UserInfoDto userInfoDto) {
        mPreferencesManager.saveProfileInfo(userInfoDto);
    }

    public Observable<AvatarUrlRes> uploadUserPhoto(MultipartBody.Part body) {
        return mRestService.uploadUserAvatar(body);
    }

    //endregion

    //region ==================== Addresses ===================

    public void updateOrInsertAddress(UserAddressDto address) {
        if (address.getId() == 0) {
            mPreferencesManager.addUserAddress(address);
        } else {
            for (UserAddressDto entry : mPreferencesManager.getUserAddresses()) {
                if (entry.getId() == address.getId()) {
                    mPreferencesManager.updateUserAddress(address);
                    break;
                }
            }
        }
    }

    public void removeAddress(UserAddressDto userAddressDto) {
        mPreferencesManager.removeAddress(userAddressDto);
    }

    public List<UserAddressDto> getUserAddresses() {
        return mPreferencesManager.getUserAddresses();
    }

    //endregion

    //region ==================== User Settings ===================

    public UserSettingsDto getUserSettings() {
        return mPreferencesManager.getUserSettings();
    }

    public void saveSettings(UserSettingsDto settings) {
        mPreferencesManager.saveUserSettings(settings);
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
        Observable.interval(AppConfig.UPDATE_DATA_INTERVAL, TimeUnit.SECONDS) //генерируем послед каждые 30 сек
                .flatMap(aLong -> NetworkStatusChecker.isInternetAvailable()) //проверяем состояние сети
                .filter(aBoolean -> aBoolean) //только если есть сеть, то запрашиваем данные
                .flatMap(aBoolean -> getProductsObsFromNetwork()) //запрашиваем данные из сети
                .subscribe(productRealm -> {
                    Log.e(TAG, "updateLocalDataWithTimer: LOCAL UPDATE complete:");
                }, throwable -> {
                    Log.e(TAG, "updateLocalDataWithTimer: ERROR" + throwable.getMessage());
                });
    }

    @RxLogObservable
    public Observable<ProductRealm> getProductsObsFromNetwork() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(((RestCallTransformer<List<ProductRes>>) mRestCallTransformer))
                .flatMap(Observable::from) // преобразуем список List в последовательность
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(productRes -> {
                    if (!productRes.isActive()) {
                        mRealmManager.deleteFromRealm(ProductRealm.class, productRes.getId());
                    }
                })
                .filter(ProductRes::isActive) //пропускаем дальше только активные(неактивные не нужно показывать, они же пустые)
                .doOnNext(productRes -> {
                    Log.e(TAG, "getProductsObsFromNetwork: " + productRes.getId());
                    mRealmManager.saveProductResponseToRealm(productRes);//сохраняем на диск только активные
                })
                .retryWhen(errorObservable ->
                        errorObservable.zipWith(Observable.range(1,
                                2/*AppConfig.RETRY_REQUEST_COUNT*/),
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

    public RealmResults<ProductRealm> getAllFavoriteProducts() {
        return mRealmManager.getAllFavoriteProducts();
    }

    public RealmResults<OrdersRealm> getAllOrders() {
        return mRealmManager.getAllOrders();
    }

    public void addOrderFromRealm(ProductRealm product) {
        mRealmManager.addOrder(product);
    }

    //endregion

    //region ==================== Comments ===================

    public Observable<CommentRes> sendComment(String productId, CommentRes comment) {
        return mRestService.sendCommentToServer(productId, comment);
    }

    //endregion

    private String getResVal(int resourceId) {
        return mContext.getString(resourceId);
    }

    //region ==================== Cart ===================

    public RealmResults<ProductRealm> getProductInCart() {
        return mRealmManager.getProductInCart();
    }

    public void saveCartProductCounter(int count) {
        mPreferencesManager.saveBasketCounter(count);
    }

    public int loadCartProductCounter() {
        return mPreferencesManager.getBasketCounter();
    }

    //endregion
}
