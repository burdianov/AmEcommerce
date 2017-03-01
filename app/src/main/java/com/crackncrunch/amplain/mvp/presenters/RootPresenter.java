package com.crackncrunch.amplain.mvp.presenters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.crackncrunch.amplain.App;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.ActivityResultDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.activities.SplashActivity;
import com.fernandocejas.frodo.annotation.RxLogSubscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mortar.Presenter;
import mortar.bundler.BundleService;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class RootPresenter extends Presenter<IRootView> {
    private static int DEFAULT_MODE = 0;
    private static int TAB_MODE = 1;

    @Inject
    AccountModel mAccountModel;

    private Subscription mUserInfoSub;
    private PublishSubject<ActivityResultDto> mActivityResultDtoObs =
            PublishSubject.create();

    public RootPresenter() {
        App.getmRootActivityRootComponent().inject(this);
    }

    @Override
    protected BundleService extractBundleService(IRootView view) {
        return (view instanceof RootActivity) ?
                BundleService.getBundleService((RootActivity) view) : // привязваем RootPresenter к RootActivity или SplashActivity
                BundleService.getBundleService((SplashActivity) view);
    }
    public PublishSubject<ActivityResultDto> getActivityResultDtoObs() {
        return mActivityResultDtoObs;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        if (getView() instanceof RootActivity) {
            mUserInfoSub = subscribeOnUserInfoObs();
        }
    }

    @Override
    protected void onExitScope() {
        if (mUserInfoSub != null) {
            mUserInfoSub.unsubscribe();
        }
        super.onExitScope();
    }

    private Subscription subscribeOnUserInfoObs() {
        return mAccountModel.getUserInfoObs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserInfoSubscriber());
    }

    @Nullable
    public IRootView getRootView() {
        return getView();
    }

    public ActionBarBuilder newActionBarBuilder() {
        return this.new ActionBarBuilder();
    }

    public FabBuilder newFabBuilder() {
        return this.new FabBuilder();
    }

    @RxLogSubscriber
    private class UserInfoSubscriber extends Subscriber<UserInfoDto> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (getView() != null) {
                getView().showError(e);
            }
        }

        @Override
        public void onNext(UserInfoDto userInfoDto) {
            if (getView() != null) {
                getView().initDrawer(userInfoDto);
            }
        }
    }

    public boolean checkPermissionsAndRequestIfNotGranted(
            @NonNull String[] permissions, int requestCode) {

        boolean allGranted = true;
        for (String permission : permissions) {
            int selfPermission = ContextCompat.checkSelfPermission(((RootActivity) getView()), permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            }
            return false;
        }
        return allGranted;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mActivityResultDtoObs.onNext(new ActivityResultDto(requestCode,
                resultCode, intent));
    }

    // TODO: 06-Dec-16 the following method shall be verified
    public void onRequestPermissionResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        // TODO: 23-Feb-17 implement me
    }

    public class ActionBarBuilder {
        private boolean isGoBack = false;
        private boolean isVisible = true;
        private CharSequence title;
        private List<MenuItemHolder> items = new ArrayList<>();
        private ViewPager pager;
        private int toolbarMode = DEFAULT_MODE;

        public ActionBarBuilder setBackArrow(boolean enabled) {
            this.isGoBack = enabled;
            return this;
        }

        public ActionBarBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public ActionBarBuilder setVisible(boolean visible) {
            this.isVisible = visible;
            return this;
        }

        public ActionBarBuilder addAction(MenuItemHolder menuItem) {
            this.items.add(menuItem);
            return this;
        }

        public ActionBarBuilder setTab(ViewPager pager) {
            this.toolbarMode = TAB_MODE;
            this.pager = pager;
            return this;
        }

        public void build() {
            if (getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setVisible(isVisible);
                activity.setTitle(title);
                activity.setBackArrow(isGoBack);
                activity.setMenuItem(items);
                if (toolbarMode == TAB_MODE) {
                    activity.setTabLayout(pager);
                } else {
                    activity.removeTabLayout();
                }
            }
        }
    }

    public class FabBuilder {
        private boolean isVisible = false;
        private int icon = R.drawable.ic_favorite_white_24dp;
        private View.OnClickListener onClickListener = null;

        public FabBuilder setVisible(boolean isVisible) {
            this.isVisible = isVisible;
            return this;
        }

        public FabBuilder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public FabBuilder setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public void build() {
            if (getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setFab(isVisible, icon, onClickListener);
            }
        }
    }
}
