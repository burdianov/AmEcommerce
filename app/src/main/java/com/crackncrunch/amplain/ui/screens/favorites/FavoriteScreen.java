package com.crackncrunch.amplain.ui.screens.favorites;

import android.os.Bundle;
import android.view.View;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.FavoriteModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.screens.cart.CartScreen;
import com.crackncrunch.amplain.ui.screens.product_details.DetailScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import mortar.MortarScope;

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
        FavoriteModel provideFavoriteModel() {
            return new FavoriteModel();
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
            extends AbstractPresenter<FavoriteView, FavoriteModel> {

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().showFavoriteList(mModel.getAllFavorites());
        }

        @Override
        protected void initActionBar() {
            View.OnClickListener listener = item -> {
                Flow.get(getView()).set(new CartScreen());
            };

            mRootPresenter.newActionBarBuilder()
                    .setTitle("Favorites")
                    .setBackArrow(true)
                    .addAction(new MenuItemHolder("To Cart", R.layout
                            .icon_count_basket, listener))
                    .build();
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder()
                    .setVisible(false)
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((FavoriteScreen.Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        public void clickOnProduct(ProductRealm product) {
            Flow.get(getView()).set(new DetailScreen(product, new FavoriteScreen()));
        }

        public void onFavoriteClick(ProductRealm product) {
            if(getView() != null) getView().showOnRemoveFromFavoriteDialog(product);
        }

        public void onCartClick(ProductRealm product) {
            //TODO : implement this
        }

        public void deleteProductFromFavorites(ProductRealm product) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> product.toggleFavorite());
            realm.close();
        }
    }

    //endregion
}
