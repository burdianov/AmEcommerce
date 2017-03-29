package com.crackncrunch.amplain.ui.screens.cart;

import android.os.Bundle;
import android.view.View;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.scopes.DaggerScope;
import com.crackncrunch.amplain.flow.AbstractScreen;
import com.crackncrunch.amplain.flow.Screen;
import com.crackncrunch.amplain.mvp.models.CartModel;
import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;
import com.crackncrunch.amplain.ui.activities.RootActivity;
import com.crackncrunch.amplain.ui.screens.product_details.DetailScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import mortar.MortarScope;

@Screen(R.layout.screen_cart)
public class CartScreen extends AbstractScreen<RootActivity.RootComponent> {
    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCartScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ============================== DI ===================================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CartScreen.class)
        CartModel provideFavoriteModel() {
            return new CartModel();
        }

        @Provides
        @DaggerScope(CartScreen.class)
        CartPresenter provideCartPresenter() {
            return new CartPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CartScreen.class)
    public interface Component {
        void inject(CartPresenter cartPresenter);
        void inject(CartView cartView);
        void inject(CartAdapter adapter);

        CartModel cartModel();
        Picasso getPicasso();
        RootPresenter getRootPresenter();
    }

    //endregion

    //region ============================== Presenter ===================================

    public class CartPresenter extends AbstractPresenter<CartView, CartModel> {
        private ProductRealm product;

        @Override
        protected void initActionBar() {
            View.OnClickListener listener = item -> {
                Flow.get(getView()).set(new CartScreen());
            };

            mRootPresenter.newActionBarBuilder()
                    .setTitle("My Products")
                    .addAction(new MenuItemHolder("To Cart", R.layout
                            .icon_count_basket, listener))
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().showCartList(getOrders());
            getView().initPrice(getOrders());
        }

        public RealmResults<OrdersRealm> getOrders() {
            return mModel.getAllOrders();
        }

        public void onProductImageClick(OrdersRealm order) {
            Realm realm = Realm.getDefaultInstance();
            product = realm.where(ProductRealm.class).equalTo("id", order.getProductId()).findFirst();
            realm.close();
            Flow.get(getView()).set(new DetailScreen(product, new CartScreen()));
        }

        public void onDeleteProduct(OrdersRealm order) {
            Realm realm = Realm.getDefaultInstance();
            RealmObject results = realm.where(OrdersRealm.class).equalTo("id", order.getId()).findFirst();
            realm.executeTransaction(realm1 -> results.deleteFromRealm());
            realm.close();
            getView().initPrice(getOrders());
            mModel.saverCartCounter();
            //mRootPresenter.getRootView().updateCartProductCounter();
        }

        //endregion
    }

    //endregion
}
