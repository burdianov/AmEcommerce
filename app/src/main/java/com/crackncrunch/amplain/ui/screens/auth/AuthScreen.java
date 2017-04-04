package com.crackncrunch.amplain.ui.screens.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.AuthModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.IAuthPresenter;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.activities.SplashActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKError;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import rx.Subscription;

import static android.support.annotation.VisibleForTesting.NONE;

@Screen(R.layout.screen_auth)
public class AuthScreen extends AbstractScreen<RootActivity.RootComponent> {

    private int mCustomState = 1;
    private String mScreen;

    public AuthScreen(String screen) {
        mScreen = screen;
    }

    public void setCustomState(int customState) {
        mCustomState = customState;
    }

    public int getCustomState() {
        return mCustomState;
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerAuthScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ===================

    @dagger.Module
    public static class Module {
        @Provides
        @DaggerScope(AuthScreen.class)
        AuthPresenter providePresenter() {
            return new AuthPresenter();
        }

        @Provides
        @DaggerScope(AuthScreen.class)
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class,
            modules = Module.class)
    @DaggerScope(AuthScreen.class)
    public interface Component {
        void inject(AuthPresenter presenter);
        void inject(AuthView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public static class AuthPresenter
            extends AbstractPresenter<AuthView, AuthModel>
            implements IAuthPresenter {

        private boolean mTestMode;
        private LoginManager mLoginManager;
        private CallbackManager mCallbackManager;

        public AuthPresenter() {
        }

        protected void initActionBar() {
            if (getRootView() instanceof RootActivity) {
                mRootPresenter.newActionBarBuilder()
                        .setTitle("Authorization")
                        .setBackArrow(true)
                        .build();
            }
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            final AuthView view = getView();
            if (view != null) {
                if (checkUserAuth()) {
                    view.hideLoginBtn();
                } else {
                    view.showLoginBtn();
                }
            }
            if (!mTestMode) {
                initSocialSdk();
            }
        }

        @Override
        public void clickOnLogin() {
            if (getView() != null && getRootView() != null) {
                if (getView().isIdle()) {
                    getView().showLoginWithAnim();
                } else {
                    String email = getView().getUserEmail();
                    String pass = getView().getUserPassword();
                    if (isValidEmail(email) && isValidPassword(pass)) {
                        loginUser(email, pass);
                    } else {
                        getView().invalidLoginAnimation();
                        getRootView().showMessage(getView().getContext().getString(R.string.email_or_password_wrong_format));
                    }
                }
            }
        }

        private void loginUser(String userEmail, String userPassword) {
            mModel.signInUser(userEmail, userPassword)
                    .subscribe(userRes -> {
                            },
                            throwable -> {
                                getRootView().showError(throwable);
                            }, this::onLoginSuccess);
        }

        @Override
        public void clickOnFb() {
            if (!mModel.isAuthUser()) {
                if (mLoginManager != null) {
                    mLoginManager.logInWithReadPermissions(((Activity)
                            getRootView()), Arrays.asList("email"));
                }
            } else {
                if (getRootView() != null) {
                    getRootView().showMessage("You have already been " +
                            "authorized as " + mModel.getUserFullName());
                }
            }
        }

        @Override
        public void clickOnVk() {
            if (!mModel.isAuthUser()) {
                Activity runningActivity = (Activity) getRootView();
                VKSdk.login(runningActivity, "email");
            } else {
                if (getRootView() != null) {
                    getRootView().showMessage("You have already been " +
                            "authorized as " + mModel.getUserFullName());
                }
            }
        }

        @Override
        public void clickOnTwitter() {
            if (getRootView() != null) {
                getRootView().showMessage("clickOnTwitter");
            }
        }

        @Override
        public void clickOnShowCatalog() {
            if (getRootView() != null) {
                if (getRootView() instanceof SplashActivity) {
                    ((SplashActivity) getRootView()).startRootActivity();
                    ((SplashActivity) getRootView()).overridePendingTransition(R.anim.enter_pull_in, R.anim.exit_fade_out);

                } else {
                    //noinspection CheckResult
                    Flow.get(getView()).goBack();
                }
            }
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isAuthUser();
        }

        @Override
        public void initSocialSdk() {
            if (mLoginManager == null) {
                mLoginManager = LoginManager.getInstance();
            }
            if (mCallbackManager == null) {
                mCallbackManager = CallbackManager.Factory.create();
                mLoginManager.registerCallback(mCallbackManager, facebookCallback);
            }
            mCompSubs.add(subscribeOnActivityResult());
        }

        final FacebookCallback<LoginResult> facebookCallback = new
                FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        onSocialResult(loginResult, SocialSdkType.FB);
                    }

                    @Override
                    public void onCancel() {
                        onSocialCancel();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        onSocialError(error, SocialSdkType.FB);
                    }
                };

        @VisibleForTesting(otherwise = NONE)
        public void testSocial() {
            mTestMode = true;
        }

        private Subscription subscribeOnActivityResult() {
            return mRootPresenter.getActivityResultSubject()
                    .subscribe(activityResultDto -> {
                                final int requestCode = activityResultDto.getRequestCode();
                                final int resultCode = activityResultDto.getResultCode();
                                final Intent intent = activityResultDto.getIntent();

                                if (requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()) {
                                    VKSdk.onActivityResult(requestCode, resultCode, intent, vkCallback); // передаем результат авторизации в VK SDK
                                }

                                if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
                                    mCallbackManager.onActivityResult(requestCode, resultCode, intent);
                                }

                                if (resultCode == Activity.RESULT_CANCELED &&
                                        (requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()
                                                || requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode())) {
                                    onSocialCancel();
                                }
                            },
                            throwable -> {
                                getRootView().showError(throwable);
                            });
        }

        final VKCallback<VKAccessToken> vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                onSocialResult(res, SocialSdkType.VK);
            }

            @Override
            public void onError(VKError error) {
                onSocialError(error, SocialSdkType.VK);
            }
        };

        @Override
        public void onSocialResult(Object res, SocialSdkType type) {
            switch (type) {
                case VK:
                    VKAccessToken vkRes = ((VKAccessToken) res);
                    final String accessToken = vkRes.accessToken;
                    final String userId = vkRes.userId;
                    final String email = vkRes.email;
                    mModel.signInVk(accessToken, userId, email)
                            .subscribe(userRes -> {
                                        if (getRootView() != null) {
                                            getRootView().showMessage("You have been successfully " +
                                                    "authorized as " + userRes.getFullName());
                                        }
                                    },
                                    throwable -> {
                                        if (getRootView() != null) {
                                            getRootView().showError(throwable);
                                        }
                                    },
                                    () -> {
                                        mRootPresenter.updateUserInfo();
                                    });
                    break;
                case FB:
                    LoginResult fbRes = ((LoginResult) res);
                    mModel.signInFb(fbRes.getAccessToken().getToken(), fbRes.getAccessToken().getUserId())
                            .subscribe(userRes -> {
                                        if (getRootView() != null) {
                                            getRootView().showMessage("You have been successfully " +
                                                    "authorized as " + userRes.getFullName());
                                        }
                                    },
                                    throwable -> {
                                        if (getRootView() != null) {
                                            getRootView().showError(throwable);
                                        }
                                    },
                                    () -> {
                                        mRootPresenter.updateUserInfo();
                                    });
                    break;
                case TWITTER:
                    break;
            }
        }

        @Override
        public void onSocialError(Object res, SocialSdkType type) {
            // TODO: 31-Mar-17 handle error
        }

        @Override
        public void onSocialCancel() {
            if (getRootView() != null) {
                getRootView().showMessage("Authorization cancelled");
            }
        }

        public void onLoginSuccess() {
            if (getView() != null && getRootView() != null) {
                getRootView().showMessage(getView().getContext().getString(R.string.authentication_successful));
                getView().hideLoginBtn();
                getView().showIdleWithAnim();
            }
        }

        public boolean isValidEmail(CharSequence target) {
            if (getView() != null) {
                Pattern pattern = Pattern.compile(getView().getContext().getString(R.string.email_valid_reg_exp));
                Matcher matcher = pattern.matcher(target);
                return matcher.matches();
            } else {
                return false;
            }
        }

        public boolean isValidPassword(CharSequence target) {
            if (getView() != null) {
                return target.length() > 8;
            } else {
                return false;
            }
        }
    }

    //endregion
}
