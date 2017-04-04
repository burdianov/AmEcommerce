package com.crackncrunch.amplain.ui.screens.auth;

import android.content.Context;

import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.AppComponent;
import com.crackncrunch.amplain.di.components.DaggerAppComponent;
import com.crackncrunch.amplain.di.modules.AppModule;
import com.crackncrunch.amplain.di.modules.RootModule;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.models.AuthModel;
import com.crackncrunch.amplain.mvp.presenters.IAuthPresenter;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.resources.StubEntityFactory;
import com.crackncrunch.amplain.ui.activities.DaggerRootActivity_RootComponent;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Created by Lilian on 25-Mar-17.
 */

public class AuthPresenterTest {
    @Mock
    AccountModel mockAccountModel;
    @Mock
    RootPresenter mockRootPresenter;
    @Mock
    AuthView mockView;
    @Mock
    Context mockContext;
    @Mock
    AuthModel mockModel;
    @Mock
    RootActivity mockRootView;
    @Mock
    Flow mockFlow;

    @Mock(answer = Answers.RETURNS_SELF)
    RootPresenter.ActionBarBuilder mockActionBarBuilder;

    private AuthScreen.AuthPresenter mPresenter;
    private BundleServiceRunner mBundleServiceRunner;
    private MortarScope mMortarScope;
    private AuthScreen.Component mTestAuthComponent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        prepareDependency(); //подготавливаем Dependency
        prepareScope(); //подготавливаем Scope
        prepareRxSchedulers(); //подготавливаем Schedulers
        prepareStubs(); // подготавливаем заглушки

        mPresenter = new AuthScreen.AuthPresenter();
        mPresenter.testSocial(); // чтобы не падали остальные тесты
    }

    private void prepareStubs() {
        //noinspection WrongConstant
        given(mockContext.getSystemService(BundleServiceRunner.SERVICE_NAME)).willReturn(mBundleServiceRunner);
        //noinspection WrongConstant
        given(mockContext.getSystemService(MortarScope.class.getName())).willReturn(mMortarScope);
        //noinspection WrongConstant
        given(mockContext.getSystemService("flow.InternalContextWrapper.FLOW_SERVICE")).willReturn(mockFlow);

        given(mockRootPresenter.getRootView()).willReturn(mockRootView);
        given(mockView.getContext()).willReturn(mockContext);
        given(mockModel.isAuthUser()).willReturn(false);

        given(mockRootPresenter.newActionBarBuilder()).willReturn(mockActionBarBuilder);
    }

    private void prepareRxSchedulers() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
            }
        });
    }

    private void prepareScope() {
        mBundleServiceRunner = new BundleServiceRunner();
        mMortarScope = MortarScope.buildRootScope()
                .withService(BundleServiceRunner.SERVICE_NAME, mBundleServiceRunner)
                .withService(DaggerService.SERVICE_NAME, mTestAuthComponent)
                .build("MockRoot");
    }

    private void prepareDependency() {
        AppComponent testAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(mockContext))
                .build();

        RootActivity.RootComponent testRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(testAppComponent)
                .rootModule(new RootModule() {
                    @Override
                    public AccountModel provideAccountModel() {
                        return mockAccountModel; //мок модель для RootPresenter можно переопределить инъекции даггера исспользуя новые testComponent и TestModule наследуемые это production Component/Modules
                    }

                    @Override
                    public RootPresenter provideRootPresenter() {
                        return mockRootPresenter; //переопределяем презентер для вью не несет ключевого значения поэтому мока
                    }
                })
                .build();

        mTestAuthComponent = DaggerAuthScreen_Component.builder()
                .rootComponent(testRootComponent)
                .module(new AuthScreen.Module() {
                    @Override
                    AuthScreen.AuthPresenter providePresenter() {
                        return mock(AuthScreen.AuthPresenter.class);
                    }

                    @Override
                    AuthModel provideAuthModel() {
                        return mockModel;
                    }
                })
                .build();
    }

    @Test
    public void clickOnLogin_isIdle_SHOW_LOGIN_WIH_ANIM() throws Exception {
        //given
        given(mockView.isIdle()).willReturn(true);
        mPresenter.takeView(mockView);

        //when
        mPresenter.clickOnLogin();

        //then
        then(mockView).should(times(1)).showLoginWithAnim();
    }

    @Test
    public void clickOnLogin_notIdle_SIGN_IN_USER_REQUEST() throws Exception {
        //given
        String expectedEmail = "any@email.ru";
        String expectedPassword = "anyPassword";
        mPresenter.takeView(mockView);
        given(mockView.isIdle()).willReturn(false);
        given(mockView.getUserEmail()).willReturn(expectedEmail);
        given(mockView.getUserPassword()).willReturn(expectedPassword);
        given(mockView.getContext().getString(anyInt())).willReturn("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        given(mockModel.signInUser(expectedEmail, expectedPassword)).willReturn(Observable.empty());

        //when
        mPresenter.clickOnLogin();

        //then
        then(mockModel).should(times(1)).signInUser(expectedEmail, expectedPassword);
        then(mockView).should(times(1)).hideLoginBtn();
        then(mockView).should(times(1)).showIdleWithAnim();
    }

    @Test
    public void clickOnShowCatalog_anyAuthUser_RETURN_ON_CATALOG_SCREEN() throws Exception {
        //given
        mPresenter.takeView(mockView);

        //when
        mPresenter.clickOnShowCatalog();

        //then
        then(mockFlow).should(times(1)).goBack();
    }

    @Test
    public void isValidEmail_true() throws Exception {
        //given
        String expectedTarget = "sas@mail.ru";
        mPresenter.takeView(mockView);
        given(mockView.getContext().getString(anyInt())).willReturn("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        //when
        boolean actualResult = mPresenter.isValidEmail(expectedTarget);

        //then
        assertTrue(actualResult);
    }

    @Test
    public void isValidEmail_false() throws Exception {
        //given
        String expectedTarget = "sas@mail";
        mPresenter.takeView(mockView);
        given(mockView.getContext().getString(anyInt())).willReturn("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        //when
        boolean actualResult = mPresenter.isValidEmail(expectedTarget);

        //then
        assertFalse(actualResult);
    }

    @Test
    public void onLoad_notAuthUser_SHOW_LOGIN_BYN() throws Exception {
        //given
        given(mockModel.isAuthUser()).willReturn(false);

        //when
        mPresenter.takeView(mockView);

        //then
        then(mockView).should().showLoginBtn();
    }

    @Test
    public void onLoad_isAuthUser_HIDE_LOGIN_BTN() throws Exception {
        //given
        given(mockModel.isAuthUser()).willReturn(true);

        //when
        mPresenter.takeView(mockView);

        //then
        then(mockView).should().hideLoginBtn();
    }

    //@Test
    public void initActionBar_onLoad_SUCCSESS_BUILD() throws Exception {

        //given
        String expectedTitle = "Authorization";
        boolean expectedArrow = true;
        given(mockRootPresenter.newActionBarBuilder()).willCallRealMethod(); //вызываем реальный билдер экшен бара

        //when
        mPresenter.takeView(mockView); //инициализируем презентер

        //then
        then(mockRootView).should().setTitle(expectedTitle); //проверяем что заголовок в RootView установился
        then(mockRootView).should().setBackArrow(expectedArrow); //проверяем что стрелка в RootView установилась

    }

    @Test
    public void clickOnVk_notAuth_SUCCESS_USER_RES() throws Exception {
        //given
        String expectedUserName = "Lilian Burdianov";
        UserRes expectedUserRes = StubEntityFactory.makeStub(UserRes.class); //ожидаемый ответ от сервера
        given(mockModel.signInVk(anyString(), anyString(), anyString())).willReturn(Observable.just(expectedUserRes)); //возвращает ответ от сервера с ожидаемым пользователем
        mPresenter.takeView(mockView); // презентер проинициализирован
        VKAccessToken mockVKAccessToken = mock(VKAccessToken.class);
        mockVKAccessToken.accessToken = "anyToken";
        mockVKAccessToken.userId = "anyUserId";
        mockVKAccessToken.email = "anyEmail";

        //when
        mPresenter.clickOnVk();
        mPresenter.onSocialResult(mockVKAccessToken, IAuthPresenter.SocialSdkType.VK);

        //then
        then(mockRootView).should().showMessage("You have been successfully " +
                "authorized as " + expectedUserName); //показать сообщение о том что пользователь успешно авторизован
        then(mockRootView).should(never()).showError(any(Throwable.class));
        then(mockRootPresenter).should().updateUserInfo();
    }

    @Test
    public void clickOnVk_alreadyAuth_SHOW_YOU_AUTH() throws Exception {
        //given
        String expectedUserName="Lilian Burdianov";
        mPresenter.takeView(mockView); // презентер проинициализирован
        given(mockModel.isAuthUser()).willReturn(true); // Пользователь уже авторизован
        given(mockModel.getUserFullName()).willReturn(expectedUserName); // Пользователь уже авторизован

        //when
        mPresenter.clickOnVk();

        //then
        then(mockRootView).should().showMessage("You have already been " +
                "authorized as " + expectedUserName); //показать сообщение о том что пользователь уже авторизован
        then(mockRootView).should(never()).showError(any(Throwable.class));
    }

    @Test
    public void clickOnVk_cancelSignIn_SHOW_AUTH_CANCELED() throws Exception {
        //given
        mPresenter.takeView(mockView); // презентер проинициализирован

        //when
        mPresenter.clickOnVk();
        mPresenter.onSocialCancel();

        //then
        then(mockRootView).should().showMessage("Authorization cancelled"); //показать сообщение о том что пользователь отменил авторизацию
        then(mockRootView).should(never()).showError(any(Throwable.class));
    }

    @Test
    public void clickOnFb_notAuth_SUCCESS_USER_RES() throws Exception {
        //given
        String expectedUserName = "Lilian Burdianov";
        UserRes expectedUserRes = StubEntityFactory.makeStub(UserRes.class); //ожидаемый ответ от сервера
        given(mockModel.signInFb(anyString(), anyString()))
                .willReturn(Observable.just(expectedUserRes)); //возвращает ответ от сервера с ожидаемым пользователем
        mPresenter.takeView(mockView); // презентер проинициализирован
        LoginResult mockLoginResult = mock(LoginResult.class, RETURNS_DEEP_STUBS);
        given(mockLoginResult.getAccessToken().getToken()).willReturn("anyToken");
        given(mockLoginResult.getAccessToken().getUserId()).willReturn("anyId");

        //when
        mPresenter.clickOnFb();
        mPresenter.onSocialResult(mockLoginResult, IAuthPresenter.SocialSdkType.FB);

        //then
        then(mockRootView).should().showMessage("You have been successfully " +
                "authorized as " + expectedUserName); //показать сообщение о том что пользователь успешно авторизован
        then(mockRootView).should(never()).showError(any(Throwable.class));
        then(mockRootPresenter).should().updateUserInfo();
    }

    @Test
    public void clickOnFb_alreadyAuth_SHOW_YOU_AUTH() throws Exception {
        //given
        String expectedUserName="Lilian Burdianov";
        mPresenter.takeView(mockView); // презентер проинициализирован
        given(mockModel.isAuthUser()).willReturn(true); // Пользователь уже авторизован
        given(mockModel.getUserFullName()).willReturn(expectedUserName); // Пользователь уже авторизован

        //when
        mPresenter.clickOnFb();

        //then
        then(mockRootView).should().showMessage("You have already been " +
                "authorized as " + expectedUserName); //показать сообщение о том что пользователь уже авторизован
        then(mockRootView).should(never()).showError(any(Throwable.class));
    }

    @Test
    public void clickOnFb_cancelSignIn_SHOW_AUTH_CANCELED() throws Exception {
        //given
        mPresenter.takeView(mockView); // презентер проинициализирован

        //when
        mPresenter.clickOnFb();
        mPresenter.onSocialCancel();

        //then
        then(mockRootView).should().showMessage("Authorization cancelled"); //показать сообщение о том что пользователь отменил авторизацию
        then(mockRootView).should(never()).showError(any(Throwable.class));
    }

    @Test
    public void clickOnVk_failsSignIn_SHOW_FORBIDDEN() throws Exception {

    }
}