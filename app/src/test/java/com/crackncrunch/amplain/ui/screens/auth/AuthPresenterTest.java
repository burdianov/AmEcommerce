package com.crackncrunch.amplain.ui.screens.auth;

import android.content.Context;

import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.models.AuthModel;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.activities.SplashActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Lilian on 12-Mar-17.
 */
public class AuthPresenterTest {

    @Mock
    AuthView mockView;
    @Mock
    Context mockContext;
    @Mock
    AuthModel mockModel;
    @Mock
    RootPresenter mockRootPresenter;
    @Mock
    SplashActivity mockRootView;

    private AuthScreen.AuthPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        BundleServiceRunner mockBundleServiceRunner = new BundleServiceRunner();
        MortarScope mockMortarScope = MortarScope.buildRootScope()
                .withService(BundleServiceRunner.SERVICE_NAME, mockBundleServiceRunner)
                .withService(DaggerService.SERVICE_NAME, mock(AuthScreen
                        .Component.class))
                .build("MockScope");
        given(mockContext.getSystemService(BundleServiceRunner.SERVICE_NAME))
                .willReturn(mockBundleServiceRunner); // когда у контекста запрашивают системный сервис с именем BundleServiceRunner.SERVICE_NAME возвратить замокированный mockBundleServiceRunner
        given(mockContext.getSystemService(MortarScope.class.getName()))
                .willReturn(mockMortarScope); // когда запрашивается системный сервис с именем MortarScope, вернуть замокированный MortarScope
        given(mockView.getContext()).willReturn(mockContext); // когда у view презентера запрашивается контекст, вернуть замокированный контекст
        given(mockRootPresenter.getRootView()).willReturn(mockRootView); // когда у RootPresenter запрашивается RootView, вернуть замокированную RootView
        mPresenter = new AuthScreen.AuthPresenter(mockModel,
                mockRootPresenter); // создаем презентер с мокированной моделью и мокированным рут презентером
    }

    @Test
    public void onLoad_never_SHOW_ERROR() throws Exception {
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
    }

    @Test
    public void onLoad_isAuthUser_HIDE_LOGIN_BTN() throws Exception {
        given(mockModel.isAuthUser()).willReturn(true);
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
        verify(mockView, atMost(1)).hideLoginBtn();
    }

    @Test
    public void onLoad_notAuthUser_SHOW_LOGIN_BTN() throws Exception {
        given(mockModel.isAuthUser()).willReturn(false);
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
        verify(mockView, atMost(1)).showLoginBtn();
    }

    @Test
    public void clickOnLogin_isIdle_SHOW_LOGIN_WITH_ANIM() throws Exception {
        mPresenter.takeView(mockView);
        given(mockView.isIdle()).willReturn(true);
        mPresenter.clickOnLogin();
        verify(mockView).showLoginWithAnim();
    }

    @Test
    public void clickOnLogin_notIdle_LOGIN_USER() throws Exception {
        mPresenter.takeView(mockView);
        given(mockView.isIdle()).willReturn(false);
        given(mockView.getUserEmail()).willReturn("anyString");
        given(mockView.getUserPassword()).willReturn("anyString");
        mPresenter.clickOnLogin();
        verify(mockModel).loginUser(any(String.class), any(String.class));
    }

    @Test
    @Ignore
    public void clickOnFb() throws Exception {
        mPresenter.clickOnFb();
        verify(mockRootView).showMessage("clickOnFb");
    }

    @Test
    @Ignore
    public void clickOnVk() throws Exception {
        mPresenter.clickOnVk();
        verify(mockRootView).showMessage("clickOnVk");
    }

    @Test
    @Ignore
    public void clickOnTwitter() throws Exception {
        mPresenter.clickOnTwitter();
        verify(mockRootView).showMessage("clickOnTwitter");
    }

    @Test
    public void clickOnShowCatalog() throws Exception {
        mPresenter.takeView(mockView);
        mPresenter.clickOnShowCatalog();
        verify(mockRootView).startRootActivity();
    }

    @Test
    public void checkUserAuth() throws Exception {
        mPresenter.checkUserAuth();
        verify(mockModel).isAuthUser();
    }

    @Test
    public void isValidEmail_true_TRUE() {
        assertTrue(mPresenter.isValidEmail("sas@mail.ru"));
    }

    @Test
    public void isValidEmail_false_FALSE() {
        assertFalse(mPresenter.isValidEmail("sa"));
        assertFalse(mPresenter.isValidEmail("sas@mail"));
    }
}