package com.crackncrunch.amplain.data.managers;

import com.crackncrunch.amplain.data.network.RestService;
import com.crackncrunch.amplain.data.network.error.AccessError;
import com.crackncrunch.amplain.data.network.error.ApiError;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.resources.MockResponses;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Lilian on 12-Mar-17.
 */
public class DataManagerTest {

    private Retrofit testRetrofit;
    private RestService testRestService;
    private MockWebServer testMockWebServer;
    private DataManager testDataManager;
    private TestSubscriber<UserRes> testSubscriber;

    @Before
    public void setUp() throws Exception {
        testMockWebServer = new MockWebServer();
        testRetrofit = new Retrofit.Builder()
                .baseUrl(testMockWebServer.url("").toString())
                .addConverterFactory(MoshiConverterFactory.create(new Moshi
                        .Builder()
                        // Для того чтобы тестировать разбор ответа от сервера, тут могот быть кастомные адаптеры
                        .build()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // для поддержки RX
                .client(new OkHttpClient.Builder()
                        // конфигурируем тестовый клиент (при необходимости)
                        .build())
                .build();
        testRestService = testRetrofit.create(RestService.class);
        testDataManager = new DataManager(testRestService);
        testSubscriber = new TestSubscriber<>();
    }

    @After
    public void tearDown() throws Exception {
        testMockWebServer.shutdown();
        testSubscriber.unsubscribe();
    }

    @Test(timeout = 800)
    public void loginUser_200_OK() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setHeader(ConstantsManager.LAST_MODIFIED_HEADER,
                        "Wed, 15 Nov 1995 04:58:08 GMT")
                .setBody(MockResponses.USER_RES_200)
                .throttleBody(512, 300, TimeUnit.MILLISECONDS);

        testMockWebServer.enqueue(mockResponse);

        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(userRes -> {
                    assertNotNull(userRes);
                    assertEquals("Вася", userRes.getFullName());
                }, throwable -> {
                    Assert.fail();
                });
    }

    /*@Test(expected = NullPointerException.class)
    public void exception_expected() throws Exception {
        testDataManager.getProductList.get(0);
    }*/

    @Test
    public void loginUser_200_RX_COMPLETED() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setHeader(ConstantsManager.LAST_MODIFIED_HEADER,
                        "Wed, 15 Nov 1995 04:58:08 GMT")
                .setBody(MockResponses.USER_RES_200);
        testMockWebServer.enqueue(mockResponse);

        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void loginUser_403_FORBIDDEN() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(403);
        testMockWebServer.enqueue(mockResponse);
        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(userRes -> {
                    Assert.fail();
                }, throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Incorrect login or password", throwable.getMessage());
                });
    }

    @Test
    public void loginUser_403_RX_THROWABLE() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(403);
        testMockWebServer.enqueue(mockResponse);

        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(testSubscriber);

        testSubscriber.assertError(AccessError.class);
    }

    @Test
    public void loginUser_500_UNKNOWN_API_EXCEPTION() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(500);
        testMockWebServer.enqueue(mockResponse);
        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(userRes -> {
                    Assert.fail();
                }, throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Server error 500", throwable.getMessage());
                });
    }

    @Test
    public void loginUser_500_RX_THROWABLE() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(500);
        testMockWebServer.enqueue(mockResponse);

        testDataManager.loginUser(new UserLoginReq("anyemail@mail.ru", "password"))
                .subscribe(testSubscriber);

        testSubscriber.assertError(ApiError.class);
    }
}