package com.crackncrunch.amplain.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.crackncrunch.amplain.BuildConfig;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.flow.TreeKeyDispatcher;
import com.crackncrunch.amplain.mortar.ScreenScoper;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.mvp.views.IView;
import com.crackncrunch.amplain.ui.screens.auth.AuthScreen;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class SplashActivity extends AppCompatActivity implements IRootView {

    @BindView(R.id.coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;

    @Inject
    RootPresenter mRootPresenter;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new AuthScreen())
                .dispatcher(new TreeKeyDispatcher(this))
                .install();
        super.attachBaseContext(newBase);
    }

    @Override
    public Object getSystemService(String name) {
        MortarScope rootActivityScope = MortarScope.findChild
                (getApplicationContext(), RootActivity.class.getName());
        return rootActivityScope.hasService(name)
                ? rootActivityScope.getService(name)
                : super.getSystemService(name);
    }

    //region ==================== Lifecycle ================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        ButterKnife.bind(this);

        DaggerService.<RootActivity.RootComponent>getDaggerComponent(this)
                .inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState
                (outState);
    }

    @Override
    protected void onStart() {
        mRootPresenter.takeView(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mRootPresenter.dropView(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            ScreenScoper.destroyScreenScope(AuthScreen.class.getName());
        }
        super.onDestroy();
    }

    //endregion

    //region ==================== IAuthView ===============

    @Override
    public void showMessage(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(Throwable e) {
        if (BuildConfig.DEBUG) {
            showMessage(e.getMessage());
            e.printStackTrace();
        } else {
            showMessage("Sorry, something went wrong! Try again later");
            // TODO: 21-Oct-16 send error stacktrace to crashlitics
        }
    }

    @Override
    public void showLoad() {
        // TODO: 21-Oct-16 show load progress
    }

    @Override
    public void hideLoad() {
        // TODO: 21-Oct-16 hide load progress
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

    @Override
    public void initDrawer(UserInfoDto userInfoDto) {

    }

    //endregion

    @Override
    public void onBackPressed() {
        if (getCurrentScreen() != null
                && !getCurrentScreen().viewOnBackPressed()
                && !Flow.get(this).goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void startRootActivity() {
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }
}
