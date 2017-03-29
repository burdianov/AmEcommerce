package com.crackncrunch.amplain.data.managers;

import com.crackncrunch.amplain.data.network.RestService;
import com.crackncrunch.amplain.data.network.error.ApiError;
import com.crackncrunch.amplain.data.network.error.ForbiddenApiError;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.CommentJsonAdapter;
import com.crackncrunch.amplain.data.network.res.ProductRes;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.resources.StubEntityFactory;
import com.crackncrunch.amplain.resources.TestResponses;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.crackncrunch.amplain.utils.ConstantsManager.UNIX_EPOCH_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class DataManagerTest {

    private MockWebServer mMockWebServer;
    private RestService mRestService;
    private DataManager mDataManager;
    private Retrofit mRetrofit;

    @Before
    public void setUp() throws Exception {
        prepareMockServer();
        mRestService = mRetrofit.create(RestService.class);
        prepareRxSchedulers(); // для переопределения Schedulers Rx (при subscribeOn/observeOn)
    }

    //region ======================== prepare ========================

    private void prepareRxSchedulers() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
            }
        });
    }

    private void prepareMockServer() {
        mMockWebServer = new MockWebServer();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("").toString())
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder()
                        .add(new CommentJsonAdapter())
                        .build()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
    }

    private void prepareDispatcher_200() {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath(); // получаем path запрса
                switch (path) {
                    case "/login":   //RestService "/" + path  !!!!
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(TestResponses.SUCSESS_USER_RES_WITH_ADDRESS);

                    case "/products":
                        return new MockResponse()
                                .setResponseCode(200)
                                .setHeader(ConstantsManager
                                        .LAST_MODIFIED_HEADER, UNIX_EPOCH_TIME)
                                .setBody(TestResponses.SUCCSESS_GET_PRODUCTS);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mMockWebServer.setDispatcher(dispatcher);
    }

    //endregion

    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    @Test
    public void signInUser_200_SUCCSESS_USER_RES() throws Exception {
        //given
        prepareDispatcher_200(); // устанавливаем диспетчер запросов
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности (пригодится при тестировании товаров))
        mDataManager = new DataManager(mRestService, mockPrefManager, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        UserRes expectedUserRes = StubEntityFactory.makeStub(UserRes.class); // ожидаемый объект из запрса
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.signInUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности
        UserRes actualRes = subscriber.getOnNextEvents().get(0); // получаем первый и единственный элемент последовательности

        //then
        subscriber.assertNoErrors(); //не должен вернуть ошибок
        assertEquals(expectedUserRes.getFullName(), actualRes.getFullName()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getId(), actualRes.getId()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getToken(), actualRes.getToken()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getPhone(), actualRes.getPhone()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getAvatarUrl(), actualRes.getAvatarUrl()); // проверяем значения полей ожидаемого объекта и фактического
        assertFalse(actualRes.getAddresses().isEmpty()); // проверяем что адреса не пустые
        then(mockPrefManager).should(times(1)).saveProfileInfo(actualRes);
    }

    @Test
    public void sigInUser_403_FORBIDDEN() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(403));
        mDataManager = new DataManager(mRestService, null, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.signInUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности
        Throwable actualThrow = subscriber.getOnErrorEvents().get(0); // получаем Ошибку

        //then
        subscriber.assertError(ForbiddenApiError.class);
        assertEquals("Incorrect login or password", actualThrow.getMessage());
    }

    @Test
    public void sigInUser_500_API_ERROR() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mDataManager = new DataManager(mRestService, null, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.signInUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности

        //then
        subscriber.assertError(ApiError.class);
    }

    @Test
    public void getProductFromNetwork_200_RECORD_RESPONSE_TO_REALM_MANAGER() throws Exception {
        //given
        prepareDispatcher_200();
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности)
        given(mockPrefManager.getLastProductUpdate()).willReturn
                (UNIX_EPOCH_TIME);
        RealmManager mockRealmManager = mock(RealmManager.class); //мокируем реалм менеджер
        mDataManager = new DataManager(mRestService, mockPrefManager, mockRealmManager); // создаем DataManager
        TestSubscriber<ProductRealm> subscriber = new TestSubscriber<>();

        //when
        mDataManager.getProductsObsFromNetwork()
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent(); // ждем окончания последовательности

        //then
        subscriber.assertNoErrors(); //без ошибок
        subscriber.assertCompleted(); //последовательность холодная - должна завершиться
        subscriber.assertNoValues(); //без значений (последовательность сохраняется в Realm и возвращается пустая последовательность

        then(mockPrefManager).should(times(1)).saveLastProductUpdate(anyString()); //должена быть сохранена дата последнего обновления сущности
        then(mockRealmManager).should(times(2)).deleteFromRealm(any(), anyString()); // один неактивный товар должен быть удален из базы
        then(mockRealmManager).should(times(7)).saveProductResponseToRealm(any(ProductRes.class)); // 8 активных товаров должны быть сохранены
    }

    @Test
    public void getProductFromNetwork_304_NOT_RECORD_TO_REALM_MANAGER() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(304));
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности)
        given(mockPrefManager.getLastProductUpdate()).willReturn
                (UNIX_EPOCH_TIME);
        RealmManager mockRealmManager = mock(RealmManager.class); //мокируем реалм менеджер
        mDataManager = new DataManager(mRestService, mockPrefManager, mockRealmManager); // создаем DataManager
        TestSubscriber<ProductRealm> subscriber = new TestSubscriber<>();

        //when
        mDataManager.getProductsObsFromNetwork()
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent(); // ждем окончания последовательности

        //then
        subscriber.assertNoErrors(); //без ошибок
        subscriber.assertCompleted(); //последовательность холодная - должна завершиться
        subscriber.assertNoValues(); //без значений (последовательность сохраняется в Realm и возвращается пустая последовательность

        then(mockRealmManager).should(never()).deleteFromRealm(any(), anyString()); // никогда не вызывается
        then(mockRealmManager).should(never()).saveProductResponseToRealm(any(ProductRes.class)); // никогда не вызывается
    }
}