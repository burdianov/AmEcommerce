package com.crackncrunch.amplain.ui.screens.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.AuthScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.AuthModel;
import com.crackncrunch.amplain.mvp.presenters.IAuthPresenter;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.activities.SplashActivity;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Screen(R.layout.screen_auth)
public class AuthScreen extends AbstractScreen<RootActivity.RootComponent> {

    private int mCustomState = 1;

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
    public class Module {
        @Provides
        @AuthScope
        AuthPresenter providePresenter() {
            return new AuthPresenter();
        }

        @Provides
        @AuthScope
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class,
            modules = Module.class)
    @AuthScope
    public interface Component {
        void inject(AuthPresenter presenter);
        void inject(AuthView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class AuthPresenter extends ViewPresenter<AuthView> implements IAuthPresenter {

        @Inject
        AuthModel mAuthModel;
        @Inject
        RootPresenter mRootPresenter;

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME))
                    .inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            if (getView() != null) {
                if (checkUserAuth()) {
                    getView().hideLoginBtn();
                } else {
                    getView().showLoginBtn();
                }
                getView().setTypeface();
            }
        }

        @Nullable
        private IRootView getRootView() {
            return mRootPresenter.getRootView();
        }

        @Override
        public void clickOnLogin() {
            if (getView() != null && getRootView() != null) {
                if (getView().isIdle()) {
                    getView().showLoginWithAnim();
                } else {
                    // TODO: 21-Oct-16 auth user
                    mAuthModel.loginUser(getView().getUserEmail(),getView().getUserPassword());
                    getRootView().showMessage("request for user request");
                }
            }
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
                } else {
                    // TODO: 22-Feb-17 show Catalog Screen
                }
            }
        }

        @Override
        public boolean checkUserAuth() {
            return mAuthModel.isAuthUser();
        }
    }

    //endregion
}
