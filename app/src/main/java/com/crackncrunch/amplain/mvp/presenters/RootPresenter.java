package com.crackncrunch.amplain.mvp.presenters;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.ActivityPermissionsResultDto;
import com.crackncrunch.amplain.data.storage.dto.ActivityResultDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.activities.SplashActivity;
import com.crackncrunch.amplain.utils.AvatarHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mortar.MortarScope;
import mortar.Presenter;
import mortar.bundler.BundleService;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.crackncrunch.amplain.utils.ConstantsManager.FILE_PROVIDER_AUTHORITY;
import static com.crackncrunch.amplain.utils.ConstantsManager.REQUEST_PROFILE_PHOTO_CAMERA;
import static com.crackncrunch.amplain.utils.ConstantsManager.REQUEST_PROFILE_PHOTO_GALLERY;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class RootPresenter extends Presenter<IRootView> {
    private static int DEFAULT_MODE = 0;
    private static int TAB_MODE = 1;

    @Inject
    AccountModel mAccountModel;

    CompositeSubscription mCompositeSubscription;
    private Subscription mUserInfoSub;
    private String mPhotoFileUrl;
    private PublishSubject<ActivityResultDto> mActivityResultSubject = PublishSubject.create();
    private BehaviorSubject<ActivityPermissionsResultDto> mActivityPermissionsResultSubject = BehaviorSubject.create();

    public RootPresenter() {
    }

    //region ==================== Getters for BehaviorSubjects ===================

    public PublishSubject<ActivityResultDto> getActivityResultSubject() {
        return mActivityResultSubject;
    }

    public BehaviorSubject<ActivityPermissionsResultDto> getActivityPermissionsResultSubject() {
        return mActivityPermissionsResultSubject;
    }

    //endregion

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
        ((RootActivity.RootComponent) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
    }

    @Override
    protected BundleService extractBundleService(IRootView view) {
        return (view instanceof RootActivity) ?
                BundleService.getBundleService((RootActivity) view) : // привязваем RootPresenter к RootActivity или SplashActivity
                BundleService.getBundleService((SplashActivity) view); // привязываем RootPresenter к SplashActivity
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        if (getView() instanceof RootActivity) {
            mCompositeSubscription.add(subscribeOnUserInfoObs());
            mCompositeSubscription.add(subscribeOnProductCountSbj());
        }
    }

    @Override
    public void dropView(IRootView view) {
        if (mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.dropView(view);
    }

    private Subscription subscribeOnUserInfoObs() {
        return mAccountModel.getUserInfoSbj()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userInfoDto -> {
                    if (getView() != null) {
                        getView().initDrawer(userInfoDto);
                    }
                }, throwable -> {
                    if (getView() != null) {
                        getView().showError(throwable);
                    }
                });
    }

    private Subscription subscribeOnProductCountSbj() {
        Observable<Integer> test = mAccountModel.getProductCountSbj();
        return mAccountModel.getProductCountSbj()
                .subscribe(cartCount -> getView().updateCartCounter(cartCount),
                        throwable -> getView().showError(throwable));
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

    public void startActivityForResult(Intent intent, int requestCode) {
        ((RootActivity) getView()).startActivityForResult(intent, requestCode);
    }

    public void onActivityResultHandler(int requestCode, int resultCode, Intent data) {
        mActivityResultSubject.onNext(new ActivityResultDto(requestCode, resultCode, data));
    }

    public boolean checkPermissionsAndRequestIfNotGranted(@NonNull String[] permissions, int requestCode) {
        boolean allGranted = true;
        allGranted = ((RootActivity) getView()).isAllGranted(permissions, allGranted);

        if (!allGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            }
            return false;
        }
        return allGranted;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mActivityResultSubject.onNext(new ActivityResultDto(requestCode,
                resultCode, intent));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mActivityPermissionsResultSubject.onNext(new ActivityPermissionsResultDto(requestCode, permissions, grantResults));
    }

    public boolean checkPermissions(@NonNull String[] permissions) {
        boolean allGranted = true;
        for (String permission : permissions) {
            int selfPermission = ContextCompat.checkSelfPermission(((RootActivity) getView()), permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        return allGranted;
    }

    public boolean requestPermissions(@NonNull String[] permissions, int requestCode) {
        boolean isRequested = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            isRequested = true;
        }
        return isRequested;
    }

    public Observable<String> getActivityResultPublishUrlSubject() {
        return getActivityResultSubject()
                .filter(res -> res.getResultCode() == Activity.RESULT_OK
                        && (res.getRequestCode() == REQUEST_PROFILE_PHOTO_GALLERY
                        || res.getRequestCode() == REQUEST_PROFILE_PHOTO_CAMERA))
                .map(res -> (res.getIntent() == null || res.getIntent().getData() == null ?
                        mPhotoFileUrl :
                        res.getIntent().getData().toString()))
                .filter(uri -> uri != null);
    }

    // FIXME: 22.03.2017 перенести в хелпер в утилитах (неявное изменение поля класса, не надо так)
    public Uri createFileForPhoto() {
        File file = AvatarHelper.createFileForPhoto();

        mPhotoFileUrl = Uri.fromFile(file).toString();

        return FileProvider.getUriForFile((Activity) getView(),
                FILE_PROVIDER_AUTHORITY, file);
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
