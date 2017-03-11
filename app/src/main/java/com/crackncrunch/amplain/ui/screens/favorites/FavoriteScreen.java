package com.crackncrunch.amplain.ui.screens.favorites;

import android.os.Bundle;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.CatalogModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import mortar.MortarScope;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Lilian on 03-Mar-17.
 */

@Screen(R.layout.screen_favorite)
public class FavoriteScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerFavoriteScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(FavoriteScreen.class)
        FavoritePresenter provideFavoritePresenter() {
            return new FavoritePresenter();
        }

        @Provides
        @DaggerScope(FavoriteScreen.class)
        CatalogModel provideFavoriteModel() {
            return new CatalogModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class,
            modules = Module.class)
    @DaggerScope(FavoriteScreen.class)
    public interface Component {
        void inject(FavoritePresenter presenter);
        void inject(FavoriteView view);
        void inject(FavoriteAdapter adapter);

        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }

    //endregion

    //region ==================== Presenter ===================

    public class FavoritePresenter
            extends AbstractPresenter<FavoriteView, CatalogModel> {

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            getView().initView();
            mCompSubs.add(subscribeOnProductRealmObs());
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Favorite Products")
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((FavoriteScreen.Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        private Subscription subscribeOnProductRealmObs() {
            return mModel.getProductObs()
                    .filter(ProductRealm::isFavorite)
                    .subscribe(new RealmSubscriber());
        }

        private class RealmSubscriber extends Subscriber<ProductRealm> {
            FavoriteAdapter mAdapter = getView().getAdapter();

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (getRootView() != null) {
                    getRootView().showError(e);
                }
            }

            @Override
            public void onNext(ProductRealm productRealm) {
                mAdapter.addItem(productRealm);
            }
        }

        public void clickOnProduct(ProductRealm product) {
            //Flow.get(getView()).set(new DetailScreen(product));
        }
    }

    //endregion
}
