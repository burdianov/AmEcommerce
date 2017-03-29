package com.crackncrunch.amplain.ui.screens.auth;

import android.os.Bundle;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;

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
    public Object createScreenComponent(RootActivity.RootComponent parentRootComponent) {
        return DaggerAuthScreen_Component.builder()
                .rootComponent(parentRootComponent)
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

        /*@Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME))
                    .inject(this);
        }*/

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
            if (getRootView() != null) {
                getRootView().showMessage("clickOnFb");
            }
        }

        @Override
        public void clickOnVk() {
            if (getRootView() != null) {
                getRootView().showMessage("clickOnVk");
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
