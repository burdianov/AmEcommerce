package com.crackncrunch.amplain.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackncrunch.amplain.BuildConfig;
import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.AppComponent;
import com.crackncrunch.amplain.di.modules.PicassoCacheModule;
import com.crackncrunch.amplain.di.modules.RootModule;
import com.crackncrunch.amplain.di.scopes.RootScope;
import com.crackncrunch.amplain.flow.TreeKeyDispatcher;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.mvp.views.IActionBarView;
import com.crackncrunch.amplain.mvp.views.IFabView;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.mvp.views.IView;
import com.crackncrunch.amplain.ui.screens.account.AccountScreen;
import com.crackncrunch.amplain.ui.screens.catalog.CatalogScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

import static com.crackncrunch.amplain.App.getContext;

public class RootActivity extends AppCompatActivity
        implements IRootView, IActionBarView, IFabView,
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.coordinator_container)
    CoordinatorLayout mCoordinatorContainer;
    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBar;

    @Inject
    RootPresenter mRootPresenter;
    @Inject

    Picasso mPicasso;
    protected ProgressDialog mProgressDialog;
    private AlertDialog.Builder mExitDialog;
    private ActionBarDrawerToggle mToggle;
    private ActionBar mActionBar;
    private List<MenuItemHolder> mActionBarMenuItems;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new CatalogScreen())
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        ButterKnife.bind(this);

        RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);

        initToolbar();
        initExitDialog();

        mRootPresenter.takeView(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState
                (outState);
    }

    @Override
    protected void onDestroy() {
        mRootPresenter.dropView(this);
        super.onDestroy();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mToggle = new ActionBarDrawerToggle(this,
                mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initExitDialog() {
        mExitDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.close_app)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes,
                        (dialog, which) -> finish())
                .setNegativeButton(R.string.no,
                        (dialog, which) -> {
                        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (getCurrentScreen() != null && !getCurrentScreen()
                .viewOnBackPressed() && !Flow.get(this).goBack()) {
            mExitDialog.show();
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRootPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRootPresenter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Object key = null;
        switch (item.getItemId()) {
            case R.id.nav_account:
                key = new AccountScreen();
                break;
            case R.id.nav_catalog:
                key = new CatalogScreen();
                break;
            case R.id.nav_favorites:
                break;
            case R.id.nav_orders:
                break;
            case R.id.nav_notifications:
                break;
        }
        if (key != null) {
            Flow.get(this).set(key);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //region ==================== IRootView ===================

    @Override
    public void showMessage(String message) {
        Snackbar.make(mCoordinatorContainer, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(Throwable e) {
        if (BuildConfig.DEBUG) {
            showMessage(e.getMessage());
            e.printStackTrace();
        } else {
            showMessage(getString(R.string.error_message));
            // TODO: 22-Oct-16 send error stacktrace to crashlytics
        }
    }

    @Override
    public void showLoad() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.CustomDialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable
                    (Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        } else {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }
    }

    @Override
    public void hideLoad() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

    @Override
    public void initDrawer(UserInfoDto userInfoDto) {
        View header = mNavigationView.getHeaderView(0);
        ImageView avatar = (ImageView) header.findViewById(R.id.drawer_user_avatar);
        TextView username = (TextView) header.findViewById(R.id.drawer_user_name);

        mPicasso.load(userInfoDto.getAvatar())
                .fit()
                .centerCrop()
                .into(avatar);

        username.setText(userInfoDto.getName());
    }

    //endregion

    //region ==================== ActionBar ===================

    @Override
    public void setVisible(boolean visible) {
        // TODO: 25-Feb-17 implement me
    }

    @Override
    public void setBackArrow(boolean enabled) {
        if (mToggle != null && mActionBar != null) {
            if (enabled) {
                mToggle.setDrawerIndicatorEnabled(false); // скрываем индикатор toggle
                mActionBar.setDisplayHomeAsUpEnabled(true); // устанавливаем индикатор тулбара
                if (mToggle.getToolbarNavigationClickListener() == null) {
                    mToggle.setToolbarNavigationClickListener(v ->
                            onBackPressed());// вешаем обработчик
                }
            } else {
                mActionBar.setDisplayHomeAsUpEnabled(false); // скрываем индикатор тулбара
                mToggle.setDrawerIndicatorEnabled(true); // активируем индикатор toggle
                mToggle.setToolbarNavigationClickListener(null); // зануляем обработчик на toggle
            }
            // если есть возможность вернуться назад (стрелка назад в ActionBar) то блокируем NavigationDrawer
            mDrawer.setDrawerLockMode(
                    enabled ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED :
                            DrawerLayout.LOCK_MODE_UNLOCKED
            );
            mToggle.syncState(); // синхронизируем состояние toggle с NavigationDrawer
        }
    }

    @Override
    public void setMenuItem(List<MenuItemHolder> items) {
        mActionBarMenuItems = items;
        supportInvalidateOptionsMenu(); // this method calls onPrepareOptionsMenu
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         if (mActionBarMenuItems != null && !mActionBarMenuItems.isEmpty()) {
            for (MenuItemHolder menuItem : mActionBarMenuItems) {
                MenuItem item = menu.add(menuItem.getIconResId());
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        .setIcon(menuItem.getIconResId())
                        .setOnMenuItemClickListener(menuItem.getListener());
            }
        } else {
             menu.clear();
         }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTabLayout(ViewPager pager) {
        TabLayout tabView = new TabLayout(this); // создаем TabLayout
        tabView.setupWithViewPager(pager); // связываем его с ViewPager
        tabView.setTabGravity(TabLayout.GRAVITY_FILL);
        mAppBar.addView(tabView); // добавляем табы в AppBar
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (tabView)); // регистрируем обработчик переключения по табам для ViewPager
    }

    @Override
    public void removeTabLayout() {
        View tabView = mAppBar.getChildAt(1);
        if (tabView != null && tabView instanceof TabLayout) { // проверяем если у аппбара есть дочерняя View являющаяся TabLayout
            mAppBar.removeView(tabView); // то удаляем ее
        }
    }

    //endregion

    //region ==================== FabView ===================

    @Override
    public void setFab(boolean isVisible, int icon,
                       View.OnClickListener onClickListener) {
        if (isVisible) {
            FloatingActionButton fab;
            if (mCoordinatorContainer.findViewById(R.id.common_fab) == null) {
                fab = (FloatingActionButton) LayoutInflater.from
                        (this).inflate(R.layout.fab, mCoordinatorContainer, false);
                mCoordinatorContainer.addView(fab);
            } else {
                fab = (FloatingActionButton) mCoordinatorContainer.findViewById(R.id.common_fab);
                fab.show();
            }
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), icon));
            fab.setOnClickListener(onClickListener);
        } else {
            removeFab();
        }
    }

    @Override
    public void removeFab() {
        View fab = mCoordinatorContainer.getChildAt(2);
        if (fab != null && fab instanceof FloatingActionButton) {
            fab.setOnClickListener(null);
            ((FloatingActionButton) fab).hide();
        }
    }

    //endregion

    //region ==================== DI ===================

    @dagger.Component(dependencies = AppComponent.class,
            modules = {RootModule.class, PicassoCacheModule.class})
    @RootScope
    public interface RootComponent {
        void inject(RootActivity activity);
        void inject(SplashActivity activity);
        void inject(RootPresenter presenter);

        AccountModel getAccountModel();
        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }

    //endregion
}