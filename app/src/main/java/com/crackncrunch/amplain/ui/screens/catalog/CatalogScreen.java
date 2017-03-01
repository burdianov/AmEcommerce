package com.crackncrunch.amplain.ui.screens.catalog;

import android.content.Context;
import android.os.Bundle;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.CatalogModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.ICatalogPresenter;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.screens.auth.AuthScreen;
import com.crackncrunch.amplain.ui.screens.product.ProductScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import rx.Subscriber;
import rx.Subscription;

@Screen(R.layout.screen_catalog)
public class CatalogScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCatalogScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogModel provideCatalogModel() {
            return new CatalogModel();
        }

        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogPresenter provideCatalogPresenter() {
            return new CatalogPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class,
            modules = Module.class)
    @DaggerScope(CatalogScreen.class)
    public interface Component {
        void inject(CatalogPresenter presenter);

        void inject(CatalogView view);

        CatalogModel getCatalogModel();

        Picasso getPicasso();

        RootPresenter getRootPresenter();
    }

    //endregion

    //region ==================== Presenter ===================

    public class CatalogPresenter extends AbstractPresenter<CatalogView,
            CatalogModel> implements ICatalogPresenter {

        private int mLastPagerPosition;

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            mCompSubs.add(subscribeOnProductRealmObs());
        }

        @Override
        public void dropView(CatalogView view) {
            mLastPagerPosition = getView().getCurrentPagerPosition();
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Catalog")
                    .addAction(new MenuItemHolder("To Cart",
                            R.drawable.ic_shopping_basket_black_24dp,
                            item -> {
                                getRootView().showMessage("Go to Cart");
                                return true;
                            }))
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        public void clickOnBuyButton(int position) {
            if (getView() != null) {
                if (checkUserAuth()) {
                    getView().getCurrentProductView().startAddToCartAnim();
                } else {
                    Flow.get(getView()).set(new AuthScreen());
                }
            }
        }

        private Subscription subscribeOnProductRealmObs() {
            if (getRootView() != null) {
                getRootView().showLoad();
            }
            return mModel.getProductObs()
                    .subscribe(new RealmSubscriber());
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isUserAuth();
        }

        private class RealmSubscriber extends Subscriber<ProductRealm> {
            CatalogAdapter mAdapter = getView().getAdapter();

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
                if (mAdapter.getCount() - 1 == mLastPagerPosition) {
                    getRootView().hideLoad();
                    getView().showCatalogView();
                }
            }
        }
    }

    //endregion

    public static class Factory {
        public static Context createProductContext(ProductRealm product,
                                                   Context parentContext) {
            MortarScope parentScope = MortarScope.getScope(parentContext);
            MortarScope childScope;
            ProductScreen screen = new ProductScreen(product);
            String scopeName = String.format("%s_%s", screen.getScopeName(),
                    product.getId());

            if (parentScope.findChild(scopeName) == null) {
                childScope = parentScope.buildChild()
                        .withService(DaggerService.SERVICE_NAME, screen
                                .createScreenComponent(DaggerService
                                        .<Component>getDaggerComponent
                                                (parentContext)))
                        .build(scopeName);
            } else {
                childScope = parentScope.findChild(scopeName);
            }
            return childScope.createContext(parentContext);
        }
    }
}
